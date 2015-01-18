/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

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

/**
 * The Class CreateChildActionWithAccelerator.
 * It extends the CreateChildAction to allow to run with a keyboard shortcut.
 */
public class CreateChildActionWithAccelerator extends CreateChildAction {

	/**
	 * Instantiates a new creates the child action with accelerator.
	 *
	 * @param editingDomain the editing domain
	 * @param selection the selection
	 * @param descriptor the descriptor
	 */
	public CreateChildActionWithAccelerator(EditingDomain editingDomain,
			ISelection selection, Object descriptor) {
		super(editingDomain, selection, descriptor);
		Object value = ((CommandParameter) descriptor).getValue();
		if (value instanceof EClass) {
			setAccelerator('c');
		} else if (value instanceof EPackage) {
			setAccelerator('p');
		} else if (value instanceof EEnum) {
			setAccelerator('e');
		} else if (value instanceof EDataType) {
			setAccelerator('d');
		} else if (value instanceof EAttribute) {
			setAccelerator('a');
		} else if (value instanceof EReference) {
			setAccelerator('r');
		}
	}

}
