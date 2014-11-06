package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlSWTControlSWTRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class EEnumLiteralRenderer extends SimpleControlSWTControlSWTRenderer {

	@Override
	protected Binding[] createBindings(Control control, Setting setting) {
		return null;
	}

	@Override
	protected Control createSWTControl(Composite parent, Setting setting) {
		Label label = new Label(parent, SWT.None);
		Object value = setting.get(true);
		label.setText(value != null ? value.toString() : "(null)");
		return label;
	}

	@Override
	protected String getUnsetText() {
		return null;
	}

	@Override
	protected boolean isUnsettable() {
		return false;
	}

}
