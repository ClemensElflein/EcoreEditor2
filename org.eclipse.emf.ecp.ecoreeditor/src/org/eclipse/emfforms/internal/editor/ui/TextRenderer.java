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
package org.eclipse.emfforms.internal.editor.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecp.view.spi.core.swt.renderer.TextControlSWTRenderer;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * Extends the default TextControlSWTRenderer by changing the update strategy.
 */
public class TextRenderer extends TextControlSWTRenderer {
	@Override
	protected Binding bindValue(Control text, IObservableValue modelValue, DataBindingContext dataBindingContext,
		UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget) {

		final IObservableValue value = SWTObservables.observeDelayedValue(600,
			SWTObservables.observeText(text, SWT.Modify));
		final Binding binding = dataBindingContext.bindValue(value, modelValue, targetToModel, modelToTarget);
		return binding;
	}
}
