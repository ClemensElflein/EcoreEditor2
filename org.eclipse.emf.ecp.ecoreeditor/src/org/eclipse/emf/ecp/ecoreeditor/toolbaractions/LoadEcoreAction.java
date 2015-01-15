package org.eclipse.emf.ecp.ecoreeditor.toolbaractions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ecoreeditor.ui.IToolbarAction;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class LoadEcoreAction extends Object implements IToolbarAction {

	@Override
	public String getLabel() {
		return "Load Ecore";
	}

	@Override
	public String getImagePath() {
		return "icons/chart_organisation_add.png";
	}

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

	@Override
	public boolean canExecute(Object object) {
		return object instanceof ResourceSet
				&& ((ResourceSet) object).getResources().size() > 0
				&& ((ResourceSet) object).getResources().get(0) instanceof EPackage;
	}

}
