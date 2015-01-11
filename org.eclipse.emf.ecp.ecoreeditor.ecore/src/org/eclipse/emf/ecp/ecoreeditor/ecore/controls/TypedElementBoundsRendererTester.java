package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;

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
