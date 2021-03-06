// REUSED CLASS
/**
 * Copyright (c) 2002-2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM - Initial API and implementation
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.ecoreeditor.internal.CreateDialog;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.action.StaticSelectionCommandAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * A child creation action is implemented by creating a {@link CreateChildCommand}.
 */
public class CreateChildAction extends StaticSelectionCommandAction
{
	/**
	 * This describes the child to be created.
	 */
	private final CommandParameter descriptor;
	private final EditingDomain editingDomain;
	private final ISelectionProvider selectionProvider;
	private final EObject parent;

	/**
	 * This constructs an instance of an action that uses the given editing domain to create a child
	 * specified by <code>descriptor</code> for the single object in the <code>selection</code>.
	 *
	 * @since 2.4.0
	 */
	public CreateChildAction(EObject parent, EditingDomain editingDomain, ISelectionProvider selectionProvider,
		CommandParameter descriptor)
	{
		super(editingDomain);
		this.parent = parent;
		this.descriptor = descriptor;
		this.selectionProvider = selectionProvider;
		configureAction(selectionProvider.getSelection());
		this.editingDomain = editingDomain;
	}

	/**
	 * This creates the command for {@link StaticSelectionCommandAction#createActionCommand}.
	 */
	@Override
	protected Command createActionCommand(EditingDomain editingDomain, Collection<?> collection)
	{
		if (collection.size() == 1)
		{
			final Object owner = collection.iterator().next();
			return CreateChildCommand.create(editingDomain, owner,
				descriptor, collection);
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	public void run() {
		final EReference reference = descriptor.getEReference();

		final EObject newObject = descriptor.getEValue();
		// Add the element, so that it is contained in the ResourceSet.

		System.out.println(reference.eContainer().toString());

		final Command addCommand = AddCommand.create(editingDomain, parent, reference,
			newObject);
		editingDomain.getCommandStack().execute(addCommand);

		final VView view = ViewProviderHelper.getView(newObject, null);
		final boolean isViewEmpty = view.getChildren().isEmpty();

		int result = Window.OK;

		if (!isViewEmpty) {
			final CreateDialog diag = new CreateDialog(Display.getCurrent().getActiveShell(), newObject);
			result = diag.open();
		}

		if (result == Window.OK) {
			// Select the newly added element. It is already added
			if (selectionProvider instanceof Viewer) {
				((Viewer) selectionProvider).refresh();
			}
			selectionProvider.setSelection(new StructuredSelection(newObject));
		} else {
			// Remove the newly added element by undoing the AddCommand
			addCommand.undo();
		}
	}
}
