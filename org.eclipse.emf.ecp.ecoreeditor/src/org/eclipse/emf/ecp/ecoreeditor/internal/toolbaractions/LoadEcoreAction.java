/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.toolbaractions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;
import org.eclipse.emf.ecp.ecoreeditor.internal.helpers.ResourceSetHelpers;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
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
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
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
		}
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

}
