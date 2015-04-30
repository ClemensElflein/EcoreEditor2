/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * cleme_000 - initial API and implementation
 ******************************************************************************/
package org.eclipse.emfforms.internal.editor.ui;

import java.util.List;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfforms.spi.treemasterdetail.swt.CreateElementCallback;
import org.eclipse.emfforms.spi.treemasterdetail.swt.TreeMasterDetailSWTRenderer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * This Composite renders the Editor's main View.
 * It consists of the Toolbar at the top and the TreeMasterDetail at the bottom of the page.
 */
public class EditorForm extends Composite {

	private final EditorToolBar toolbar;
	private final TreeMasterDetailSWTRenderer treeMasterDetail;

	/**
	 * This Composite renders the Editor's main View.
	 * It consists of the Toolbar at the top and the TreeMasterDetail at the bottom of the page.
	 *
	 * @param parent The Parent
	 * @param editorTitle The Editor's name
	 * @param editorInput The Input to display
	 * @param toolbarActions a List of jface actions to show in the toolbar.
	 */
	public EditorForm(Composite parent, String editorTitle, Object editorInput, List<Action> toolbarActions) {
		super(parent, SWT.NONE);

		setLayout(new FormLayout());

		final FormData toolbarLayoutData = new FormData();
		toolbarLayoutData.left = new FormAttachment(0);
		toolbarLayoutData.right = new FormAttachment(100);
		toolbarLayoutData.top = new FormAttachment(0);
		toolbar = new EditorToolBar(this, SWT.NONE, editorTitle, toolbarActions);
		toolbar.setLayoutData(toolbarLayoutData);

		final FormData treeMasterDetailLayoutData = new FormData();
		treeMasterDetailLayoutData.top = new FormAttachment(toolbar, 5);
		treeMasterDetailLayoutData.left = new FormAttachment(0);
		treeMasterDetailLayoutData.right = new FormAttachment(100);
		treeMasterDetailLayoutData.bottom = new FormAttachment(100);
		treeMasterDetail = new TreeMasterDetailSWTRenderer(this, SWT.NONE, editorInput);
		treeMasterDetail.setLayoutData(treeMasterDetailLayoutData);
	}

	/**
	 * Returns the selection provider for this Editor.
	 * In this case it returns the selectionProvider for the Tree.
	 *
	 * @return The Selection Provider
	 */
	public ISelectionProvider getSelectionProvider() {
		return treeMasterDetail.getSelectionProvider();
	}

	/**
	 * Gets the current selection.
	 *
	 * @return the current selection
	 */
	public Object getCurrentSelection() {
		return treeMasterDetail.getCurrentSelection();
	}

	/**
	 * Gets the editing domain.
	 *
	 * @return the editing domain
	 */
	public EditingDomain getEditingDomain() {
		return treeMasterDetail.getEditingDomain();
	}

	/**
	 * Set the CreateElementCallback. To clear the callback, pass null
	 *
	 * @param createElementCallback The new Callback. null to remove callback.
	 */
	public void setCreateElementCallback(CreateElementCallback createElementCallback) {
		treeMasterDetail.setCreateElementCallback(createElementCallback);
	}
}
