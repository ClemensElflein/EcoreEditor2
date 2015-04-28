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

package org.eclipse.emfforms.internal.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfforms.internal.editor.ui.CreateNewChildDialog;
import org.eclipse.emfforms.spi.editor.GenericEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The ShorcutHandler receives the shortcuts defined in plugin.xml and passes them to the editor.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShortcutHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof GenericEditor) {
			final GenericEditor eEditor = (GenericEditor) editor;
			final StructuredSelection sSelection = (StructuredSelection) HandlerUtil.getCurrentSelection(event);
			if (sSelection == null) {
				return null;
			}

			final Object selection = sSelection.getFirstElement();

			// We only create or delete elements for EObjects
			if (!(selection instanceof EObject)) {
				return null;
			}

			final String commandName = event.getCommand().getId();
			final EObject currentSelection = (EObject) selection;

			final EditingDomain editingDomain = AdapterFactoryEditingDomain
				.getEditingDomainFor(currentSelection);

			if ("org.eclipse.emfforms.editor.delete".equals(commandName)) {
				editingDomain.getCommandStack().execute(
					RemoveCommand.create(editingDomain, currentSelection));
			} else if ("org.eclipse.emfforms.editor.new".equals(commandName)) {
				createNewElementDialog(editingDomain, eEditor.getEditorSite().getSelectionProvider(), currentSelection,
					"Create Child").open();
			} else if ("org.eclipse.emfforms.editor.new.sibling".equals(commandName)) {
				// Get Parent of current Selection and show the dialog for it
				final EObject parent = currentSelection.eContainer();
				final EditingDomain parentEditingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(parent);
				createNewElementDialog(parentEditingDomain, eEditor.getEditorSite().getSelectionProvider(), parent,
					"Create Sibling").open();
			}
		}

		return null;
	}

	/**
	 * Creates the new element dialog.
	 *
	 * @param editingDomain the editing domain
	 * @param selection the selection
	 * @param title the title
	 * @return the dialog
	 */
	private Dialog createNewElementDialog(final EditingDomain editingDomain,
		final ISelectionProvider selectionProvider,
		final EObject selection, final String title) {

		return new CreateNewChildDialog(Display.getCurrent().getActiveShell(), title, selection,
			selectionProvider);
	}
}
