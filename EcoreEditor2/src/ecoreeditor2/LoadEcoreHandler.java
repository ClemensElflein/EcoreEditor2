package ecoreeditor2;


import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.view.treemasterdetail.ui.swt.internal.MasterDetailAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import ecoreeditor2.helpers.ResourceSetHelpers;


public class LoadEcoreHandler extends MasterDetailAction {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Object selection = ((IStructuredSelection) HandlerUtil.getActiveMenuSelection(event)).getFirstElement();
		if (selection == null || !(selection instanceof EObject)) {
			return null;
		}
		execute((EObject) selection);
		return null;
	}

	@Override
	public boolean shouldShow(EObject eObject) {
		return true;
	}

	@Override
	public void execute(EObject object) {
		ElementTreeSelectionDialog dialog= new ElementTreeSelectionDialog(Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
        
		int result = dialog.open();
        if(result == Window.OK) {
        	// Get the Resource Set and Add the File to it
        	IResource selectedResource = (IResource) dialog.getFirstResult();
        	ResourceSetHelpers.addResourceToSet(object.eResource().getResourceSet(),
        			URI.createFileURI(selectedResource.getFullPath().toOSString()));
        }
        
	}

}