/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexandra Buzila- initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.ecp.view.model.internal.preview.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.view.model.internal.preview.Activator;
import org.eclipse.emf.ecp.view.model.internal.preview.e3.views.PreviewView;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.treemasterdetail.ui.swt.MasterDetailAction;
import org.eclipse.emfforms.spi.editor.IToolbarAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.FrameworkUtil;

/** Opens the {@link PreviewView}. */
public class OpenPreviewHandler extends MasterDetailAction implements IToolbarAction {
	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Object selection = ((IStructuredSelection) HandlerUtil.getActiveMenuSelection(event)).getFirstElement();
		if (selection == null || !(selection instanceof EObject)) {
			return null;
		}
		execute((EObject) selection);
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.view.spi.treemasterdetail.ui.swt.MasterDetailAction#execute(EObject)
	 */
	@Override
	public void execute(EObject object) {
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			page.showView("org.eclipse.emf.ecp.view.model.preview.e3.views.PreviewView", null, //$NON-NLS-1$
				IWorkbenchPage.VIEW_VISIBLE);
		} catch (final PartInitException e) {
			Activator
			.getDefault()
			.getLog()
			.log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getMessage(), e));
		}

	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.view.spi.treemasterdetail.ui.swt.MasterDetailAction#shouldShow(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean shouldShow(EObject eObject) {
		return true;
	}

	@Override
	public Action getAction(final Object currentObject) {
		final Action previewAction = new Action("Open Preview") {
			@Override
			public void run() {
				execute(((ResourceSet) currentObject).getResources().get(0).getAllContents().next());
			}
		};
		previewAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(this.getClass())
			.getResource("icons/preview.png")));
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
