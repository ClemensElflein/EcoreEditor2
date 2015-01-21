/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EOperation;
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

	private static final LinkedHashMap<Class<?>, Character> accelerators = new LinkedHashMap<Class<?>, Character>(){{
		put(EClass.class, 'c');
		put(EPackage.class, 'p');
		put(EEnum.class, 'e');
		put(EDataType.class, 'd');
		put(EAttribute.class, 'a');
		put(EReference.class, 'r');
		put(EAnnotation.class, 'n');
		put(EOperation.class, 'o');
		put(EEnumLiteral.class, 'l');
	}};
	
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
		
		for(Class<?> c : accelerators.keySet()) {
			if(c.isInstance(value)) {
				setAccelerator(accelerators.get(c));
				break;
			}
		}
	}

}
