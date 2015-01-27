package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.view.internal.core.swt.renderer.TextControlSWTRenderer;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;


public class TextRenderer extends TextControlSWTRenderer {
	@Override
	protected Binding bindValue(Control text, IObservableValue modelValue, DataBindingContext dataBindingContext,
		UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget) {
		final IObservableValue value = SWTObservables.observeText(text, SWT.Modify);
		final Binding binding = dataBindingContext.bindValue(value, modelValue, targetToModel, modelToTarget);
		return binding;
	}
}
