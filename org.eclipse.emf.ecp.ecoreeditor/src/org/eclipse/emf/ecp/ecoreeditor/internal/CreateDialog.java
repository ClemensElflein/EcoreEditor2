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

package org.eclipse.emf.ecp.ecoreeditor.internal;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class CreateDialog allows initializing newly created EObjects.
 * It also can be used to create an EObject and initialize it directly.
 */
public class CreateDialog extends Dialog {

	/** The new object. */
	private final EObject newObject;

	/**
	 * Instantiates a new dialog.
	 *
	 * @param parent the parent
	 * @param toCreate the EClass which should be created
	 */
	public CreateDialog(Shell parent, EClass toCreate) {
		this(parent, EcoreFactory.eINSTANCE.create(toCreate));
	}

	/**
	 * Instantiates a new dialog.
	 *
	 * @param parent the parent
	 * @param createdInstance an EObject to initialize
	 */
	public CreateDialog(Shell parent, EObject createdInstance) {
		super(parent);
		newObject = createdInstance;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create new " + newObject.eClass().getName());
		newShell.setMinimumSize(300, 150);
		newShell.setBackground(new Color(newShell.getDisplay(), 255, 255, 255));
		newShell.setBackgroundMode(SWT.INHERIT_FORCE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		final GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(parentData);

		final ScrolledComposite wrapper = new ScrolledComposite(parent, SWT.V_SCROLL);
		wrapper.setExpandHorizontal(true);
		wrapper.setExpandVertical(true);
		final FillLayout wrapperLayout = new FillLayout();
		wrapperLayout.marginHeight = 10;
		wrapperLayout.marginWidth = 10;
		wrapper.setLayout(wrapperLayout);
		wrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final Composite emfFormsParent = new Composite(wrapper, SWT.NONE);
		wrapper.setContent(emfFormsParent);
		emfFormsParent.setLayout(new GridLayout());

		try {
			ECPSWTViewRenderer.INSTANCE.render(emfFormsParent, newObject);
		} catch (final ECPRendererException e) {
		}

		wrapper.setMinSize(wrapper.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		getParentShell().forceFocus();
		final Diagnostic result = Diagnostician.INSTANCE.validate(newObject);
		if (result.getSeverity() == Diagnostic.OK) {
			super.okPressed();
		} else {
			// Get the error count and create an appropriate Error message:
			final int errorCount = result.getChildren().size();
			String errorMessage = String.format(
				"%d %s occured while analyzing your inputs. The following errors were found:\r\n", errorCount,
				errorCount == 1 ? "error" : "errors");
			int messageCount = 1;
			for (final Diagnostic d : result.getChildren()) {
				errorMessage += String.format("\r\n%d. %s", messageCount++, d.getMessage());
			}

			MessageDialog.open(MessageDialog.ERROR, getParentShell(), "Error", errorMessage, SWT.NONE);
		}
	}

	/**
	 * Gets the created instance or the updated one, if it was passed in the constructor.
	 * All fields are initialized with user inputs
	 *
	 * @return the created instance
	 */
	public EObject getCreatedInstance() {
		return newObject;
	}
}
