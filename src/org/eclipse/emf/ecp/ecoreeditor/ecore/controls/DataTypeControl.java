package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlJFaceViewerSWTRenderer;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class DataTypeControl extends SimpleControlJFaceViewerSWTRenderer {
	@Override
	protected String getUnsetText() {
		return "No EDataType set!";
	}

	@Override
	protected Viewer createJFaceViewer(Composite parent, Setting setting) {
		final ComboViewer combo = new ComboViewer(parent, SWT.DROP_DOWN);

		List<EDataType> dataTypes = ResourceSetHelpers
				.findAllOfTypeInResourceSet(getViewModelContext()
						.getDomainModel(), EDataType.class, true);

		combo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof EClassifier)
					return ((EClassifier) element).getName();
				return super.getText(element);
			}
		});
		combo.setContentProvider(new ArrayContentProvider());
		combo.setInput(dataTypes.toArray());

		new AutoCompleteField(combo.getCombo(), new ComboContentAdapter(),
				combo.getCombo().getItems());
		return combo;
	}

	@Override
	protected Binding[] createBindings(Viewer viewer, Setting setting) {
		final Binding binding = getDataBindingContext().bindValue(
				WidgetProperties.text().observe(
						((ComboViewer) viewer).getCombo()),
				getModelValue(setting),
				new EMFUpdateValueStrategy().setConverter(new Converter(
						String.class, EDataType.class) {
					@Override
					public Object convert(Object fromObject) {
						// We want the result for such a request to be null, not
						// a DataType with null name (can occur)
						if (fromObject == null)
							return null;

						Object[] dataTypes = (Object[]) ((ComboViewer) viewer)
								.getInput();
						for (int i = 0; i < dataTypes.length; i++) {
							if (fromObject.equals(((EDataType) dataTypes[i])
									.getName())) {
								return dataTypes[i];
							}
						}

						// If we haven't found the DataType yet, Try adding an E
						// to the Input and search again
						// So we find EString even if String was searched. If
						// there is a DataType named String, we have no problem
						// As it would have been matched in the previous
						// for-loop.
						String fromStringWithE = "E" + fromObject.toString();
						for (int i = 0; i < dataTypes.length; i++) {
							if (fromStringWithE
									.equals(((EDataType) dataTypes[i])
											.getName())) {
								return dataTypes[i];
							}
						}

						return null;
					}
				}), new EMFUpdateValueStrategy());
		return new Binding[] { binding };
	}

}
