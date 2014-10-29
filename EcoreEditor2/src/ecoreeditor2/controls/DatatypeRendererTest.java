package ecoreeditor2.controls;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;

public class DatatypeRendererTest implements ECPRendererTester {

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
