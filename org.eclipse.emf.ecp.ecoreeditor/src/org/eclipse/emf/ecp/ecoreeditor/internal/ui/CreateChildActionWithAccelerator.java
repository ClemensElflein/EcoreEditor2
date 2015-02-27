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

package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import java.util.LinkedHashMap;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * The Class CreateChildActionWithAccelerator.
 * It extends the CreateChildAction to allow to run with a keyboard shortcut.
 */

public final class CreateChildActionWithAccelerator extends CreateChildAction {

	private static final LinkedHashMap<Class<?>, Character> ACCELERATORS = new LinkedHashMap<Class<?>, Character>();

	static {
		ACCELERATORS.put(EClass.class, 'c');
		ACCELERATORS.put(EPackage.class, 'p');
		ACCELERATORS.put(EEnum.class, 'e');
		ACCELERATORS.put(EDataType.class, 'd');
		ACCELERATORS.put(EAttribute.class, 'a');
		ACCELERATORS.put(EReference.class, 'r');
		ACCELERATORS.put(EAnnotation.class, 'n');
		ACCELERATORS.put(EOperation.class, 'o');
		ACCELERATORS.put(EEnumLiteral.class, 'l');
	}

	/**
	 * Instantiates a new creates the child action with accelerator.
	 *
	 * @param editingDomain the editing domain
	 * @param selectionProvider the selectionProvider
	 * @param descriptor the descriptor
	 */
	public CreateChildActionWithAccelerator(EObject parent, EditingDomain editingDomain,
		ISelectionProvider selectionProvider, CommandParameter descriptor) {
		super(parent, editingDomain, selectionProvider, descriptor);
		final Object value = descriptor.getValue();

		for (final Class<?> c : ACCELERATORS.keySet()) {
			if (c.isInstance(value)) {
				setAccelerator(ACCELERATORS.get(c));
				break;
			}
		}
	}

}
