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
package org.eclipse.emfforms.internal.editor.viewmodel.toolbaractions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emfforms.internal.editor.viewmodel.Activator;
import org.eclipse.emfforms.spi.treemasterdetail.swt.IToolbarAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.FrameworkUtil;

/**
 *
 * The ToolbarAction allowing the User to generate Java code for the currently visible Genmodel.
 *
 */
public class PreviewViewmodelAction implements IToolbarAction {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emfforms.spi.treemasterdetail.swt.IToolbarAction#getAction(java.lang.Object)
	 */
	@Override
	public Action getAction(Object currentObject) {
		final Action previewAction = new Action("Preview") {
			/**
			 * {@inheritDoc}
			 *
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try {
					page.showView("org.eclipse.emfforms.internal.editor.viewmodel.views.PreviewView", null, //$NON-NLS-1$
						IWorkbenchPage.VIEW_VISIBLE);
				} catch (final PartInitException e) {
					Activator
					.getDefault()
					.getLog()
					.log(
							new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e
								.getMessage(), e));
				}
			}
		};

		previewAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(this.getClass())
			.getResource("icons/arrow_refresh.png")));

		return previewAction;
	}

	@Override
	public boolean canExecute(Object object) {
		// We can't execute our Action on Objects other than ResourceSet
		if (!(object instanceof ResourceSet)) {
			return false;
		}
		// Check, if the ResourceSet contains a Genmodel. If so, we also can't execute our Action
		final ResourceSet resourceSet = (ResourceSet) object;
		for (final Resource r : resourceSet.getResources()) {
			if (r.getContents().size() > 0 && r.getContents().get(0) instanceof VView) {
				return true;
			}
		}
		return false;
	}
}
