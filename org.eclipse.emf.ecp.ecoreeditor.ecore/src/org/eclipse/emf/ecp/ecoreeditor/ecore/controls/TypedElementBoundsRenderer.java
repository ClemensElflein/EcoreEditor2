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
package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.spi.core.swt.AbstractControlSWTRenderer;
import org.eclipse.emf.ecp.view.spi.model.LabelAlignment;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.swt.layout.GridDescriptionFactory;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridCell;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridDescription;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 *
 * The Renderer allowing the user to test the bounds with two Combos.
 *
 */
public class TypedElementBoundsRenderer extends AbstractControlSWTRenderer<VControl> {
	private SWTGridDescription rendererGridDescription;

	@Override
	public final SWTGridDescription getGridDescription(SWTGridDescription gridDescription) {
		if (rendererGridDescription == null) {
			rendererGridDescription = GridDescriptionFactory.INSTANCE.createSimpleGrid(1,
				getVElement().getLabelAlignment() == LabelAlignment.NONE ? 2 : 3, this);
		}
		return rendererGridDescription;
	}

	@Override
	protected final Control renderControl(SWTGridCell gridCell, Composite parent)
		throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		int controlIndex = gridCell.getColumn();
		if (getVElement().getLabelAlignment() == LabelAlignment.NONE) {
			controlIndex++;
		}
		switch (controlIndex) {
		case 0:
			return createBoundsLabel(parent);
		case 1:
			return createValidationIcon(parent);
		case 2:
			return createControl(parent);
		default:
			throw new IllegalArgumentException(
				String
					.format(
						"The provided SWTGridCell (%1$s) cannot be used by this (%2$s) renderer.", gridCell.toString(), toString())); //$NON-NLS-1$
		}
	}

	private Control createBoundsLabel(Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setData(CUSTOM_VARIANT, "org_eclipse_emf_ecp_control_label"); //$NON-NLS-1$
		label.setText("Bounds");
		return label;
	}

	private Control createControl(Composite parent) {
		final ETypedElement domainObject = (ETypedElement) getViewModelContext().getDomainModel();
		final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(domainObject);

		final Composite main = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(main);
		GridDataFactory.fillDefaults().grab(true, false)
		.align(SWT.FILL, SWT.BEGINNING).applyTo(main);

		final Spinner lowerBound = new Spinner(main, SWT.BORDER);
		lowerBound.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, true));
		lowerBound.setSelection(domainObject.getLowerBound());
		lowerBound.setMaximum(Integer.MAX_VALUE);
		final Spinner upperBound = new Spinner(main, SWT.BORDER);
		upperBound.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, true));
		upperBound.setMinimum(-1);
		upperBound.setSelection(domainObject.getUpperBound());
		upperBound.setEnabled(domainObject.getUpperBound() != -1);
		upperBound.setMaximum(Integer.MAX_VALUE);

		final Button unbounded = new Button(main, SWT.CHECK);
		unbounded.setText("unbounded");
		unbounded.setSelection(false);
		unbounded.setSelection(domainObject.getUpperBound() == -1);

		lowerBound.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int i = ((Spinner) e.getSource()).getSelection();
				editingDomain.getCommandStack().execute(
					new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND, i));
				if (upperBound.getSelection() < i && upperBound.getSelection() >= 0) {
					upperBound.setSelection(i);
					editingDomain.getCommandStack().execute(
						new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND,
							i));
				}
				applyValidation();
			}
		});

		upperBound.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final int i = ((Spinner) e.getSource()).getSelection();
				editingDomain.getCommandStack().execute(
					new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND, i));
				if (lowerBound.getSelection() > i && i >= 0) {
					lowerBound.setSelection(i);
					editingDomain.getCommandStack().execute(
						new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__LOWER_BOUND,
							i));
				}
				unbounded.setSelection(i == -1);
				applyValidation();
			}
		});

		unbounded.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (unbounded.getSelection()) {
					upperBound.setSelection(-1);
					editingDomain.getCommandStack().execute(
						new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND,
							-1));
				} else {
					upperBound.setSelection(lowerBound.getSelection());
					editingDomain.getCommandStack().execute(
						new SetCommand(editingDomain, domainObject, EcorePackage.Literals.ETYPED_ELEMENT__UPPER_BOUND,
							lowerBound.getSelection()));
				}
				upperBound.setEnabled(!unbounded.getSelection());
				applyValidation();
			}
		});
		applyValidation();
		return main;
	}

	private void setValidationColor(Control control, Color validationColor) {
		control.setBackground(validationColor);
	}

	@Override
	protected final void applyValidation() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				applyInnerValidation();
			}
		});
	}

	private void applyInnerValidation() {
		Label validationIcon;
		Control editControl;
		switch (getControls().size()) {
		case 2:
			validationIcon = Label.class.cast(getControls().get(
				new SWTGridCell(0, 0, TypedElementBoundsRenderer.this)));
			editControl = getControls().get(new SWTGridCell(0, 1, TypedElementBoundsRenderer.this));
			break;
		case 3:
			validationIcon = Label.class.cast(getControls().get(
				new SWTGridCell(0, 1, TypedElementBoundsRenderer.this)));
			editControl = getControls().get(new SWTGridCell(0, 2, TypedElementBoundsRenderer.this));
			break;
		default: // TODO log error ;
			return;
		}
		// triggered due to another validation rule before this control is rendered
		if (validationIcon == null || editControl == null) {
			return;
		}
		// validation rule triggered after the control was disposed
		if (validationIcon.isDisposed()) {
			return;
		}
		// no diagnostic set
		if (getVElement().getDiagnostic() == null) {
			return;
		}

		validationIcon.setImage(getValidationIcon(getVElement().getDiagnostic().getHighestSeverity()));
		validationIcon.setToolTipText(getVElement().getDiagnostic().getMessage());

		setValidationColor(editControl, getValidationBackgroundColor(getVElement().getDiagnostic()
			.getHighestSeverity()));
	}
}
