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
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;

/**
 * Tester for the TypedElementBoundsRenderer.
 * It returns a priority of 10, if the current feature equals ETypedElement_UpperBound.
 */
public class TypedElementBoundsRendererTester implements ECPRendererTester {

	@Override
	public int isApplicable(VElement vElement, ViewModelContext viewModelContext) {
		if (!VControl.class.isInstance(vElement)) {
			return NOT_APPLICABLE;
		}
		final EStructuralFeature feature = VControl.class.cast(vElement)
			.getDomainModelReference().getEStructuralFeatureIterator()
			.next();

		return viewModelContext.getDomainModel() instanceof ETypedElement
			&& feature.equals(EcorePackage.eINSTANCE
				.getETypedElement_UpperBound()) ? 10 : NOT_APPLICABLE;
	}

}
