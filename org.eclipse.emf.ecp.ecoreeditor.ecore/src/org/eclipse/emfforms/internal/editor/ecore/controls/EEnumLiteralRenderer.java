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

import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlSWTControlSWTRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * The Renderer for EENumLiterals.
 */
public class EEnumLiteralRenderer extends SimpleControlSWTControlSWTRenderer {

	@Override
	protected Binding[] createBindings(Control control, Setting setting) {
		return null;
	}

	@Override
	protected Control createSWTControl(Composite parent, Setting setting) {
		final Label label = new Label(parent, SWT.None);
		final Object value = setting.get(true);
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
