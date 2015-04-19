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
package org.eclipse.emfforms.internal.editor.helpers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EcorePackage;

/**
 * Provides methods to simplify working with Ecore files.
 */
public final class EcoreHelpers {

	private EcoreHelpers() {
	}

	/**
	 * Checks if the feature is generic.
	 *
	 * @param feature The feature to check
	 * @return true, if the provided feature is generic
	 */
	public static boolean isGenericFeature(Object feature)
	{
		return feature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES ||
			feature == EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS ||
			feature == EcorePackage.Literals.EOPERATION__EGENERIC_EXCEPTIONS ||
			feature == EcorePackage.Literals.EOPERATION__ETYPE_PARAMETERS ||
			feature == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE;
	}

	/**
	 * Checks if the given element is generic.
	 *
	 * @param element The element to check
	 * @return true, if the element is generic.
	 */
	public static boolean isGenericElement(Object element) {
		return EcorePackage.Literals.EGENERIC_TYPE.isInstance(element) ||
			EcorePackage.Literals.ETYPE_PARAMETER.isInstance(element);
	}

	/**
	 * Removes all generic elements from a list of elements.
	 *
	 * @param elements The list of elements to be filtered
	 * @return a list without generic elements
	 */
	public static Object[] filterGenericElements(Object[] elements) {
		final List<Object> elementList = new LinkedList<Object>();
		for (final Object e : elements) {
			if (!isGenericElement(e)) {
				elementList.add(e);
			}
		}
		return elementList.toArray();
	}
}
