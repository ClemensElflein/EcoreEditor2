package org.eclipse.emf.ecp.ecoreeditor.ecore.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ecoreeditor.treeinput.TreeInput;
import org.eclipse.emf.ecp.view.treemasterdetail.ui.swt.internal.MasterDetailAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class LoadEcoreHandler extends MasterDetailAction {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Object selection = ((IStructuredSelection) HandlerUtil
				.getActiveMenuSelection(event)).getFirstElement();

		if (selection == null || !(selection instanceof EObject)) {
			return null;
		}
		execute((EObject) selection);
		return null;
	}

	@Override
	public boolean shouldShow(EObject eObject) {
		return eObject instanceof TreeInput;
	}

	@Override
	public void execute(EObject object) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				Display.getDefault().getActiveShell(),
				new WorkbenchLabelProvider(),
				new BaseWorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace());
		int result = dialog.open();
		if (result == Window.OK) {

			ResourceSet resourceSet = (ResourceSet) (((TreeInput) object)
					.getInput());
			IResource selectedResource = (IResource) dialog.getFirstResult();
			if (!selectedResource.isAccessible()) {
				return;
			}
			ResourceSetHelpers
					.addResourceToSet(resourceSet, URI
							.createFileURI(selectedResource.getLocation()
									.toOSString()));
		}
	}

}
