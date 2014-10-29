package ecoreeditor2.controls;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VElement;

public class EEnumLiteralRendererTest implements ECPRendererTester {

	@Override
	public int isApplicable(VElement vElement, ViewModelContext viewModelContext) {
		if (!VControl.class.isInstance(vElement)) {
			return NOT_APPLICABLE;
		}
		final EStructuralFeature feature = VControl.class.cast(vElement)
				.getDomainModelReference().getEStructuralFeatureIterator()
				.next();

		return viewModelContext.getDomainModel() instanceof EEnum
				&& feature.getEType() instanceof EDataType ? 10 : 0;
	}

}
