/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Clemens Elflein - initial API and implementation
 ******************************************************************************/

package org.eclipse.emf.ecp.ecoreeditor.internal.toolbaractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * The Class LoadEcoreAction.
 * It displays the "Load Ecore" action in the editor's toolbar, if appropriate.
 */
public class LoadEcoreAction extends Object implements IToolbarAction {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Load Ecore";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#getImagePath()
	 */
	@Override
	public String getImagePath() {
		return "icons/chart_organisation_add.png";
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object currentObject) {
		new ExtendedLoadResourceDialog(Display.getDefault().getActiveShell(),
			AdapterFactoryEditingDomain.getEditingDomainFor(currentObject)).open();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#canExecute(java.lang.Object)
	 */
	@Override
	public boolean canExecute(Object object) {
		// We cannot execute the action on objects other than ResourceSet
		if (!(object instanceof ResourceSet)
			|| ((ResourceSet) object).getResources().size() == 0) {
			return false;
		}
		// We cannot execute the action, when the first Resource's root is not a EPackage
		final Resource firstResource = ((ResourceSet) object).getResources().get(0);
		if (firstResource.getContents().size() == 0 || firstResource.getContents().get(0) instanceof GenModel) {
			return false;
		}
		return true;
	}

	/**
	 * The Load Resource dialog for selecting other Resources.
	 * It was reused from the Sample Ecore Model Editor.
	 */
	private static class ExtendedLoadResourceDialog extends LoadResourceDialog
	{
		/**
		 * @author cleme_000
		 *
		 */
		private final class RegisteredPackageSelectionAdapter extends SelectionAdapter {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				final RegisteredPackageDialog registeredPackageDialog = new RegisteredPackageDialog(getShell());
				registeredPackageDialog.open();
				final Object[] result = registeredPackageDialog.getResult();
				if (result != null)
				{
					final List<?> nsURIs = Arrays.asList(result);
					if (registeredPackageDialog.isDevelopmentTimeVersion())
					{
						final ResourceSet resourceSet = new ResourceSetImpl();
						resourceSet.getURIConverter().getURIMap()
							.putAll(EcorePlugin.computePlatformURIMap(false));

						// To support Xcore resources, we need a resource with a URI that helps determine the
						// containing project
						//
						final Resource dummyResource = domain == null ? null : resourceSet
							.createResource(domain.getResourceSet().getResources().get(0).getURI());

						final StringBuffer uris = new StringBuffer();
						final Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin
							.getEPackageNsURIToGenModelLocationMap(false);
						for (int i = 0, length = result.length; i < length; i++)
						{
							final URI location = ePackageNsURItoGenModelLocationMap.get(result[i]);
							final Resource resource = resourceSet.getResource(location, true);
							EcoreUtil.resolveAll(resource);
						}

						final EList<Resource> resources = resourceSet.getResources();
						resources.remove(dummyResource);

						for (final Resource resource : resources)
						{
							for (final EPackage ePackage : getAllPackages(resource))
							{
								if (nsURIs.contains(ePackage.getNsURI()))
								{
									uris.append(resource.getURI());
									uris.append("  ");
									break;
								}
							}
						}
						uriField.setText((uriField.getText() + "  " + uris.toString()).trim());
					}
					else
					{
						final StringBuffer uris = new StringBuffer();
						for (int i = 0, length = result.length; i < length; i++)
						{
							uris.append(result[i]);
							uris.append("  ");
						}
						uriField.setText((uriField.getText() + "  " + uris.toString()).trim());
					}
				}
			}
		}

		/**
		 * @author cleme_000
		 *
		 */
		private final class TargetPlatformSelectionAdapter extends SelectionAdapter {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				final TargetPlatformPackageDialog classpathPackageDialog = new TargetPlatformPackageDialog(
					getShell());
				classpathPackageDialog.open();
				final Object[] result = classpathPackageDialog.getResult();
				if (result != null)
				{
					final List<?> nsURIs = Arrays.asList(result);
					final ResourceSet resourceSet = new ResourceSetImpl();
					resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(true));

					// To support Xcore resources, we need a resource with a URI that helps determine the
					// containing project
					//
					final Resource dummyResource = domain == null ? null : resourceSet.createResource(domain
						.getResourceSet().getResources().get(0).getURI());

					final StringBuffer uris = new StringBuffer();
					final Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin
						.getEPackageNsURIToGenModelLocationMap(true);
					for (int i = 0, length = result.length; i < length; i++)
					{
						final URI location = ePackageNsURItoGenModelLocationMap.get(result[i]);
						final Resource resource = resourceSet.getResource(location, true);
						EcoreUtil.resolveAll(resource);
					}

					final EList<Resource> resources = resourceSet.getResources();
					resources.remove(dummyResource);

					for (final Resource resource : resources)
					{
						for (final EPackage ePackage : getAllPackages(resource))
						{
							if (nsURIs.contains(ePackage.getNsURI()))
							{
								uris.append(resource.getURI());
								uris.append("  ");
								break;
							}
						}
					}
					uriField.setText((uriField.getText() + "  " + uris.toString()).trim());
				}
			}
		}

		private final Set<EPackage> registeredPackages = new LinkedHashSet<EPackage>();

		public ExtendedLoadResourceDialog(Shell parent, EditingDomain domain)
		{
			super(parent, domain);
		}

		@Override
		protected boolean processResource(Resource resource)
		{
			// Put all static package in the package registry.
			//
			final ResourceSet resourceSet = domain.getResourceSet();
			if (!resourceSet.getResources().contains(resource))
			{
				final Registry packageRegistry = resourceSet.getPackageRegistry();
				for (final EPackage ePackage : getAllPackages(resource))
				{
					packageRegistry.put(ePackage.getNsURI(), ePackage);
					registeredPackages.add(ePackage);
				}
			}
			return true;
		}

		protected Collection<EPackage> getAllPackages(Resource resource)
		{
			final List<EPackage> result = new ArrayList<EPackage>();
			for (final TreeIterator<?> j =
				new EcoreUtil.ContentTreeIterator<Object>(resource.getContents())
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected Iterator<? extends EObject> getEObjectChildren(EObject eObject)
					{
						return
						eObject instanceof EPackage ?
							((EPackage) eObject).getESubpackages().iterator() :
							Collections.<EObject> emptyList().iterator();
					}
				}; j.hasNext();)
			{
				final Object content = j.next();
				if (content instanceof EPackage)
				{
					result.add((EPackage) content);
				}
			}
			return result;
		}

		@Override
		protected Control createDialogArea(Composite parent)
		{
			final Composite composite = (Composite) super.createDialogArea(parent);
			final Composite buttonComposite = (Composite) composite.getChildren()[0];

			final Button browseRegisteredPackagesButton = new Button(buttonComposite, SWT.PUSH);
			browseRegisteredPackagesButton.setText("Browser Registered Packages");
			prepareBrowseRegisteredPackagesButton(browseRegisteredPackagesButton);
			{
				final FormData data = new FormData();
				final Control[] children = buttonComposite.getChildren();
				data.right = new FormAttachment(children[0], -CONTROL_OFFSET);
				browseRegisteredPackagesButton.setLayoutData(data);
			}

			final Button browseTargetPlatformPackagesButton = new Button(buttonComposite, SWT.PUSH);
			browseTargetPlatformPackagesButton.setText("Browse Target Platform Packages");
			prepareBrowseTargetPlatformPackagesButton(browseTargetPlatformPackagesButton);
			{
				final FormData data = new FormData();
				data.right = new FormAttachment(browseRegisteredPackagesButton, -CONTROL_OFFSET);
				browseTargetPlatformPackagesButton.setLayoutData(data);
			}

			return composite;
		}

		/**
		 * @since 2.9
		 */
		protected void prepareBrowseTargetPlatformPackagesButton(Button browseTargetPlatformPackagesButton)
		{
			browseTargetPlatformPackagesButton.addSelectionListener(new TargetPlatformSelectionAdapter());
		}

		protected void prepareBrowseRegisteredPackagesButton(Button browseRegisteredPackagesButton)
		{
			browseRegisteredPackagesButton.addSelectionListener(new RegisteredPackageSelectionAdapter());
		}
	}

	/**
	 * @since 2.9
	 */
	private static class TargetPlatformPackageDialog extends ElementListSelectionDialog
	{
		public TargetPlatformPackageDialog(Shell parent)
		{
			super(parent,
				new LabelProvider()
				{
					@Override
					public Image getImage(Object element)
					{
						return ExtendedImageRegistry.getInstance().getImage(
							EcoreEditPlugin.INSTANCE.getImage("full/obj16/EPackage"));
					}
				});

			setMultipleSelection(true);
			setMessage("Select Registered Package URI");
			setFilter("*");
			setTitle("PackageSelection");
		}

		protected void updateElements()
		{
			final Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin
				.getEPackageNsURIToGenModelLocationMap(true);
			final Object[] result = ePackageNsURItoGenModelLocationMap.keySet().toArray(
				new Object[ePackageNsURItoGenModelLocationMap.size()]);
			Arrays.sort(result);
			setListElements(result);
		}

		@Override
		protected Control createDialogArea(Composite parent)
		{
			final Composite result = (Composite) super.createDialogArea(parent);
			updateElements();
			return result;
		}
	}

	/**
	 *
	 * Dialog to select registered packages.
	 * It was reused from the Sample Ecore Model Editor.
	 *
	 */
	private static class RegisteredPackageDialog extends ElementListSelectionDialog
	{
		private boolean isDevelopmentTimeVersion = true;

		public RegisteredPackageDialog(Shell parent)
		{
			super(parent,
				new LabelProvider()
				{
					@Override
					public Image getImage(Object element)
					{
						return ExtendedImageRegistry.getInstance().getImage(
							EcoreEditPlugin.INSTANCE.getImage("full/obj16/EPackage"));
					}
				});

			setMultipleSelection(true);
			setMessage("Select Registered Package URI");
			setFilter("*");
			setTitle("Package Selection");
		}

		public boolean isDevelopmentTimeVersion()
		{
			return isDevelopmentTimeVersion;
		}

		protected void updateElements()
		{
			if (isDevelopmentTimeVersion)
			{
				final Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin
					.getEPackageNsURIToGenModelLocationMap(false);
				final Object[] result = ePackageNsURItoGenModelLocationMap.keySet().toArray(
					new Object[ePackageNsURItoGenModelLocationMap.size()]);
				Arrays.sort(result);
				setListElements(result);
			}
			else
			{
				final Object[] result = EPackage.Registry.INSTANCE.keySet().toArray(
					new Object[EPackage.Registry.INSTANCE.size()]);
				Arrays.sort(result);
				setListElements(result);
			}
		}

		@Override
		protected Control createDialogArea(Composite parent)
		{
			final Composite result = (Composite) super.createDialogArea(parent);
			final Composite buttonGroup = new Composite(result, SWT.NONE);
			final GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			buttonGroup.setLayout(layout);
			final Button developmentTimeVersionButton = new Button(buttonGroup, SWT.RADIO);
			developmentTimeVersionButton.addSelectionListener
				(new SelectionAdapter()
				{
					@Override
					public void widgetSelected(SelectionEvent event)
					{
						isDevelopmentTimeVersion = developmentTimeVersionButton.getSelection();
						updateElements();
					}
				});
			developmentTimeVersionButton.setText("Development Time Version");
			final Button runtimeTimeVersionButton = new Button(buttonGroup, SWT.RADIO);
			runtimeTimeVersionButton.setText("Runtime Version");
			developmentTimeVersionButton.setSelection(true);

			updateElements();

			return result;
		}
	}

}
