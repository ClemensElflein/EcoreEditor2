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

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlJFaceViewerSWTRenderer;
import org.eclipse.emfforms.internal.editor.helpers.ResourceSetHelpers;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * A control to select the DataType for ETypedElements.
 */
public class DataTypeControl extends SimpleControlJFaceViewerSWTRenderer {
	@Override
	protected String getUnsetText() {
		return "No EDataType set!";
	}

	@Override
	protected Viewer createJFaceViewer(Composite parent, Setting setting) {
		final ComboViewer combo = new ComboViewer(parent, SWT.DROP_DOWN);

		Class<?> type = EClassifier.class;
		boolean includeEcorePackage = false;

		if (getViewModelContext().getDomainModel() instanceof EAttribute) {
			type = EDataType.class;
			includeEcorePackage = true;
		} else if (getViewModelContext().getDomainModel() instanceof EReference) {
			type = EClass.class;
			includeEcorePackage = false;
		}

		final List<?> classifiers = ResourceSetHelpers.findAllOfTypeInResourceSet(
			getViewModelContext().getDomainModel(), type,
			includeEcorePackage);

		combo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof EClassifier) {
					return ((EClassifier) element).getName();
				}
				return super.getText(element);
			}
		});
		combo.setContentProvider(new ArrayContentProvider());
		combo.setInput(classifiers.toArray());

		new AutoCompleteField(combo.getCombo(), new ComboContentAdapter(),
			combo.getCombo().getItems());

		return combo;
	}

	@Override
	protected Binding[] createBindings(final Viewer viewer, final Setting setting) {
		final Binding binding = getDataBindingContext().bindValue(
			WidgetProperties.text().observe(
				((ComboViewer) viewer).getCombo()),
				getModelValue(setting),
				new EMFUpdateValueStrategy().setConverter(new Converter(
					String.class, EClassifier.class) {
					@Override
					public Object convert(Object fromObject) {
						// We want the result for such a request to be null, not a DataType with null name (can occur)
						if (fromObject == null) {
							return null;
						}

						final Object[] classifiers = (Object[]) ((ComboViewer) viewer).getInput();
						for (int i = 0; i < classifiers.length; i++) {
							if (fromObject
								.equals(((EClassifier) classifiers[i])
									.getName())) {
								return classifiers[i];
							}
						}

						// If we haven't found the DataType yet, Try adding an E to the Input and search again.
						// So we find EString even if String was searched.
						final String fromStringWithE = "E" + fromObject.toString();
						for (int i = 0; i < classifiers.length; i++) {
							if (fromStringWithE.equals(((EClassifier) classifiers[i]).getName())) {
								return classifiers[i];
							}
						}

						return null;
					}
				}), new EMFUpdateValueStrategy().setConverter(new Converter(EClassifier.class, String.class) {
					@Override
					public Object convert(Object fromObject) {
						if (fromObject == null) {
							return "";
						}
						return ((EClassifier) fromObject).getName();
					}
				}));
		// TODO: this update creates a nasty usability bug!
		/*
		 * ((ComboViewer)viewer).getCombo().addFocusListener(new FocusListener() {
		 * @Override
		 * public void focusLost(FocusEvent e) {
		 * binding.updateModelToTarget();
		 * }
		 * @Override
		 * public void focusGained(FocusEvent e) {
		 * }
		 * });
		 */
		return new Binding[] { binding };
	}

	@Override
	protected boolean isUnsettable() {
		// We unset the property via databinding as soon as the input is invalid
		return false;
	}
}
