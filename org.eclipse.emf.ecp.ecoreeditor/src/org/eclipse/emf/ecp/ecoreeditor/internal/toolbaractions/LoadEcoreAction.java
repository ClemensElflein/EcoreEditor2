/*
 * @author Clemens Elflein
 */
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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.ui.dialogs.DiagnosticDialog;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
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
import org.eclipse.emf.ecp.ecoreeditor.internal.Activator;
import org.eclipse.emf.ecp.ecoreeditor.internal.helpers.ResourceSetHelpers;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.LoadResourceAction;
import org.eclipse.emf.edit.ui.action.LoadResourceAction.LoadResourceDialog;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;


/**
 * The Class LoadEcoreAction.
 * It displays the "Load Ecore" action in the editor's toolbar, if appropriate.
 */
public class LoadEcoreAction extends Object implements IToolbarAction {

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#getLabel()
	 */
	@Override
	public String getLabel() {
		return "Load Ecore";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#getImagePath()
	 */
	@Override
	public String getImagePath() {
		return "icons/chart_organisation_add.png";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#execute(java.lang.Object)
	 */
	@Override
	public void execute(Object currentObject) {
		/*ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				Display.getDefault().getActiveShell(),
				new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace());
		dialog.setTitle("Load Ecore Model");
		int result = dialog.open();
		if (result == Window.OK) {
			ResourceSet resourceSet = (ResourceSet) currentObject;
			IResource selectedResource = (IResource) dialog.getFirstResult();
			if (!selectedResource.isAccessible()) {
				return;
			}
			ResourceSetHelpers
					.addResourceToSet(resourceSet, URI
							.createFileURI(selectedResource.getLocation()
									.toOSString()));
		}*/
		new ExtendedLoadResourceDialog(Display.getDefault().getActiveShell(), AdapterFactoryEditingDomain.getEditingDomainFor(currentObject)).open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecp.ecoreeditor.IToolbarAction#canExecute(java.lang.Object)
	 */
	@Override
	public boolean canExecute(Object object) {
		// We cannot execute the action on objects other than ResourceSet
		if(!(object instanceof ResourceSet)
				|| ((ResourceSet) object).getResources().size() == 0) {
			return false;
		}
		// We cannot execute the action, when the first Resource's root is not a EPackage
		Resource firstResource = ((ResourceSet)object).getResources().get(0);
		if(firstResource.getContents().size() == 0 || !(firstResource.getContents().get(0) instanceof EPackage)) {
			return false;
		}
		return true;
	}
	
	    public static class ExtendedLoadResourceDialog extends LoadResourceDialog
	    {
	      protected Set<EPackage> registeredPackages = new LinkedHashSet<EPackage>();

	      public ExtendedLoadResourceDialog(Shell parent, EditingDomain domain)
	      {
	        super(parent, domain);
	      }

	      @Override
	      protected boolean processResource(Resource resource)
	      {
	        // Put all static package in the package registry.
	        //
	        ResourceSet resourceSet = domain.getResourceSet();
	        if (!resourceSet.getResources().contains(resource))
	        {
	          Registry packageRegistry = resourceSet.getPackageRegistry();
	          for (EPackage ePackage : getAllPackages(resource))
	          {
	            packageRegistry.put(ePackage.getNsURI(), ePackage);
	            registeredPackages.add(ePackage);
	          }
	        }
	        return true;
	      }

	      public Set<EPackage> getRegisteredPackages()
	      {
	        return registeredPackages;
	      }

	      protected Collection<EPackage> getAllPackages(Resource resource)
	      {
	        List<EPackage> result = new ArrayList<EPackage>();
	        for (TreeIterator<?> j =
	               new EcoreUtil.ContentTreeIterator<Object>(resource.getContents())
	               {
	                 private static final long serialVersionUID = 1L;

	                 @Override
	                 protected Iterator<? extends EObject> getEObjectChildren(EObject eObject)
	                 {
	                   return
	                     eObject instanceof EPackage ?
	                       ((EPackage)eObject).getESubpackages().iterator() :
	                         Collections.<EObject>emptyList().iterator();
	                 }
	               };
	             j.hasNext(); )
	        {
	          Object content = j.next();
	          if (content instanceof EPackage)
	          {
	            result.add((EPackage)content);
	          }
	        }
	        return result;
	      }

	      @Override
	      protected Control createDialogArea(Composite parent)
	      {
	        Composite composite = (Composite)super.createDialogArea(parent);
	        Composite buttonComposite = (Composite)composite.getChildren()[0];

	        Button browseRegisteredPackagesButton = new Button(buttonComposite, SWT.PUSH);
	        browseRegisteredPackagesButton.setText("Browser Registered Packages");
	        prepareBrowseRegisteredPackagesButton(browseRegisteredPackagesButton);
	        {
	          FormData data = new FormData();
	          Control [] children = buttonComposite.getChildren();
	          data.right = new FormAttachment(children[0], -CONTROL_OFFSET);
	          browseRegisteredPackagesButton.setLayoutData(data);
	        }

	        Button browseTargetPlatformPackagesButton = new Button(buttonComposite, SWT.PUSH);
	        browseTargetPlatformPackagesButton.setText("Browse Target Platform Packages");
	        prepareBrowseTargetPlatformPackagesButton(browseTargetPlatformPackagesButton);
	        {
	          FormData data = new FormData();
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
	        browseTargetPlatformPackagesButton.addSelectionListener
	          (new SelectionAdapter()
	           {
	             @Override
	             public void widgetSelected(SelectionEvent event)
	             {
	               TargetPlatformPackageDialog classpathPackageDialog = new TargetPlatformPackageDialog(getShell());
	               classpathPackageDialog.open();
	               Object [] result = classpathPackageDialog.getResult();
	               if (result != null)
	               {
	                 List<?> nsURIs = Arrays.asList(result);
	                 ResourceSet resourceSet = new ResourceSetImpl();
	                 resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(true));

	                 // To support Xcore resources, we need a resource with a URI that helps determine the containing project
	                 //
	                 Resource dummyResource = domain == null ? null : resourceSet.createResource(domain.getResourceSet().getResources().get(0).getURI());

	                 StringBuffer uris = new StringBuffer();
	                 Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap(true);
	                 for (int i = 0, length = result.length; i < length; i++)
	                 {
	                   URI location = ePackageNsURItoGenModelLocationMap.get(result[i]);
	                   Resource resource = resourceSet.getResource(location, true);
	                   EcoreUtil.resolveAll(resource);
	                 }

	                 EList<Resource> resources = resourceSet.getResources();
	                 resources.remove(dummyResource);

	                 for (Resource resource : resources)
	                 {
	                   for (EPackage ePackage : getAllPackages(resource))
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
	           });
	      }

	      protected void prepareBrowseRegisteredPackagesButton(Button browseRegisteredPackagesButton)
	      {
	        browseRegisteredPackagesButton.addSelectionListener
	          (new SelectionAdapter()
	           {
	             @Override
	             public void widgetSelected(SelectionEvent event)
	             {
	               RegisteredPackageDialog registeredPackageDialog = new RegisteredPackageDialog(getShell());
	               registeredPackageDialog.open();
	               Object [] result = registeredPackageDialog.getResult();
	               if (result != null)
	               {
	                 List<?> nsURIs = Arrays.asList(result);
	                 if (registeredPackageDialog.isDevelopmentTimeVersion())
	                 {
	                   ResourceSet resourceSet = new ResourceSetImpl();
	                   resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(false));

	                   // To support Xcore resources, we need a resource with a URI that helps determine the containing project
	                   //
	                   Resource dummyResource = domain == null ? null : resourceSet.createResource(domain.getResourceSet().getResources().get(0).getURI());

	                   StringBuffer uris = new StringBuffer();
	                   Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap(false);
	                   for (int i = 0, length = result.length; i < length; i++)
	                   {
	                     URI location = ePackageNsURItoGenModelLocationMap.get(result[i]);
	                     Resource resource = resourceSet.getResource(location, true);
	                     EcoreUtil.resolveAll(resource);
	                   }

	                   EList<Resource> resources = resourceSet.getResources();
	                   resources.remove(dummyResource);

	                   for (Resource resource : resources)
	                   {
	                     for (EPackage ePackage : getAllPackages(resource))
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
	                   StringBuffer uris = new StringBuffer();
	                   for (int i = 0, length = result.length; i < length; i++)
	                   {
	                     uris.append(result[i]);
	                     uris.append("  ");
	                   }
	                   uriField.setText((uriField.getText() + "  " + uris.toString()).trim());
	                 }
	               }
	             }
	           });
	      }
	    }

	    /**
	     * @since 2.9
	     */
	    public static class TargetPlatformPackageDialog extends ElementListSelectionDialog
	    {
	      public TargetPlatformPackageDialog(Shell parent)
	      {
	        super
	          (parent,
	           new LabelProvider()
	           {
	             @Override
	            public Image getImage(Object element)
	             {
	               return ExtendedImageRegistry.getInstance().getImage(EcoreEditPlugin.INSTANCE.getImage("full/obj16/EPackage"));
	             }
	           });

	        setMultipleSelection(true);
	        setMessage("Select Registered Package URI");
	        setFilter("*");
	        setTitle("PackageSelection");
	      }

	      protected void updateElements()
	      {
	        Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap(true);
	        Object [] result = ePackageNsURItoGenModelLocationMap.keySet().toArray(new Object[ePackageNsURItoGenModelLocationMap.size()]);
	        Arrays.sort(result);
	        setListElements(result);
	      }

	      @Override
	      protected Control createDialogArea(Composite parent)
	      {
	        Composite result = (Composite)super.createDialogArea(parent);
	        updateElements();
	        return result;
	      }
	    }

	    public static class RegisteredPackageDialog extends ElementListSelectionDialog
	    {
	      protected boolean isDevelopmentTimeVersion = true;

	      public RegisteredPackageDialog(Shell parent)
	      {
	        super
	          (parent,
	           new LabelProvider()
	           {
	             @Override
	            public Image getImage(Object element)
	             {
	               return ExtendedImageRegistry.getInstance().getImage(EcoreEditPlugin.INSTANCE.getImage("full/obj16/EPackage"));
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
	          Map<String, URI> ePackageNsURItoGenModelLocationMap = EcorePlugin.getEPackageNsURIToGenModelLocationMap(false);
	          Object [] result = ePackageNsURItoGenModelLocationMap.keySet().toArray(new Object[ePackageNsURItoGenModelLocationMap.size()]);
	          Arrays.sort(result);
	          setListElements(result);
	        }
	        else
	        {
	          Object [] result = EPackage.Registry.INSTANCE.keySet().toArray(new Object[EPackage.Registry.INSTANCE.size()]);
	          Arrays.sort(result);
	          setListElements(result);
	        }
	      }

	      @Override
	      protected Control createDialogArea(Composite parent)
	      {
	        Composite result = (Composite)super.createDialogArea(parent);
	        Composite buttonGroup = new Composite(result, SWT.NONE);
	        GridLayout layout = new GridLayout();
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
	        Button runtimeTimeVersionButton = new Button(buttonGroup, SWT.RADIO);
	        runtimeTimeVersionButton.setText("Runtime Version");
	        developmentTimeVersionButton.setSelection(true);

	        updateElements();

	        return result;
	      }
	    }
	  

}
