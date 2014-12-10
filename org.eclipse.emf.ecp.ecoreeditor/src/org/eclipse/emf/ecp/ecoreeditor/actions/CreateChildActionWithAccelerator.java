package org.eclipse.emf.ecp.ecoreeditor.actions;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.ecp.CreateChildAction;
import org.eclipse.jface.viewers.ISelection;

public class CreateChildActionWithAccelerator extends CreateChildAction {

	public CreateChildActionWithAccelerator(EditingDomain editingDomain,
			ISelection selection, Object descriptor) {
		super(editingDomain, selection, descriptor);
		Object value = ((CommandParameter) descriptor).getValue();
		if (value instanceof EClass) {
			setAccelerator('c');
		} else if (value instanceof EPackage) {
			setAccelerator('p');
		} else if (value instanceof EDataType) {
			setAccelerator('d');
		} else if (value instanceof EEnum) {
			setAccelerator('e');
		} else if (value instanceof EAttribute) {
			setAccelerator('a');
		} else if (value instanceof EReference) {
			setAccelerator('r');
		}
	}

}
