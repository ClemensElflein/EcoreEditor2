package org.eclipse.emf.ecp.ecoreeditor.masterdetail;

import org.eclipse.emf.ecp.ecoreeditor.treeinput.TreeInput;
import org.eclipse.emf.ecp.view.model.common.ECPRendererTester;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VElement;

public class MasterDetailRendererTester implements ECPRendererTester {

	@Override
	public int isApplicable(VElement vElement, ViewModelContext viewModelContext) {
		if(viewModelContext.getDomainModel() instanceof TreeInput) {
			return 10;
		}
		return NOT_APPLICABLE;
	}

}
