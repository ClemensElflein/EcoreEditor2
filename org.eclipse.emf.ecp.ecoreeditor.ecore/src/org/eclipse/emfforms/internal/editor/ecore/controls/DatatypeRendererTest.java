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
package org.eclipse.emfforms.internal.editor.ecore.controls;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;

/**
 * The Tester for the DatatypeRenderer. It returns a priority of 10, if the feature is ETypedElement_EType. Else
 * NOT_APPLICABLE.
 */
public class DatatypeRendererTest implements ECPRendererTester {

	/**
	 * @param feature The feature to test
	 * @param vElement the element containing the feature
	 * @param context the current ViewModelContext
	 * @return the priority
	 */
	public int isApplicableForFeature(EStructuralFeature feature, VElement vElement, ViewModelContext context) {
		if (feature.equals(EcorePackage.eINSTANCE.getETypedElement_EType())) {
			return 10;
		}
		return NOT_APPLICABLE;
	}

	@Override
	public int isApplicable(VElement vElement, ViewModelContext viewModelContext) {
		if (!VControl.class.isInstance(vElement)) {
			return NOT_APPLICABLE;
		}
		final EStructuralFeature feature = VControl.class.cast(vElement).getDomainModelReference()
			.getEStructuralFeatureIterator().next();
		return isApplicableForFeature(feature, vElement, viewModelContext);
	}

}
