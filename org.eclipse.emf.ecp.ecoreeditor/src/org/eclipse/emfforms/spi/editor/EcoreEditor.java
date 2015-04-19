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

package org.eclipse.emfforms.spi.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.emfforms.internal.editor.ui.CreateNewChildDialog;
import org.eclipse.emfforms.internal.editor.ui.MasterDetailRenderer;
import org.eclipse.emfforms.spi.editor.helpers.ResourceSetHelpers;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The Class EcoreEditor it is the generic part for editing any EObject.
 */
public class EcoreEditor extends EditorPart implements IEditingDomainProvider {

	/**
	 * The EcoreResourceChangeListener listens for changes in currently opened Ecore files and reports
	 * them to the EcoreEditor.
	 */
	private final class EcoreResourceChangeListener implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			final Collection<Resource> changedResources = new ArrayList<Resource>();
			final Collection<Resource> removedResources = new ArrayList<Resource>();
			final IResourceDelta delta = event.getDelta();

			try {
				delta.accept(new IResourceDeltaVisitor() {

					@Override
					public boolean visit(final IResourceDelta delta)
					{
						if (delta.getResource().getType() == IResource.FILE
							&& (delta.getKind() == IResourceDelta.REMOVED ||
							delta.getKind() == IResourceDelta.CHANGED))
						{
							final Resource resource = resourceSet.getResource(
								URI.createPlatformResourceURI(delta.getFullPath().toString(), true), false);
							if (resource != null)
							{
								if (delta.getKind() == IResourceDelta.REMOVED)
								{
									removedResources.add(resource);
								}
								else
								{
									changedResources.add(resource);
								}
							}
							return false;
						}

						return true;
					}
				});
			} catch (final CoreException ex) {
			}

			handleResourceChange(changedResources, removedResources);
		}
	}

	/** The Resource loaded from the provided EditorInput. */
	private ResourceSet resourceSet;

	/** The command stack. It is used to mark the editor as dirty as well as undo/redo operations */
	private final BasicCommandStack commandStack = new BasicCommandStack();

	/** The root view. It is the main Editor panel. */
	private MasterDetailRenderer rootView;

	/**
	 * True, if there were changes in the filesystem while the editor was in the background and the changes could not be
	 * applied to current view.
	 */
	private boolean filesChangedWithConflict;

	private final IPartListener partListener = new IPartListener() {
		@Override
		public void partOpened(IWorkbenchPart part) {
		}

		@Override
		public void partDeactivated(IWorkbenchPart part) {
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		@Override
		public void partActivated(IWorkbenchPart part) {
			if (part == EcoreEditor.this && isDirty() && filesChangedWithConflict && discardChanges()) {
				for (final Resource r : resourceSet.getResources()) {
					r.unload();
					try {
						r.load(null);
					} catch (final IOException e) {
					}
				}
			}
		}
	};

	private final IResourceChangeListener resourceChangeListener = new EcoreResourceChangeListener();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// Remove the Listener, so that we won't get a changed notification for our own save operation
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
		if (ResourceSetHelpers.save(resourceSet)) {
			// Tell the CommandStack, that we have saved the file successfully
			// and inform the Workspace, that the Dirty property has changed.
			commandStack.saveIsDone();
			firePropertyChange(PROP_DIRTY);
			filesChangedWithConflict = false;
		}
		// Add the listener again, so that we get notifications for future changes
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	/**
	 * Handles filesystem changes.
	 *
	 * @param changedResources A List of changed Resources
	 * @param removedResources A List of removed Resources
	 */
	protected void handleResourceChange(Collection<Resource> changedResources, Collection<Resource> removedResources) {
		if (!isDirty()) {
			resourceSet.getResources().removeAll(removedResources);

			for (final Resource changed : changedResources) {
				changed.unload();
				try {
					changed.load(null);
				} catch (final IOException ex) {
				}
			}
		} else {
			filesChangedWithConflict = true;
		}
	}

	/**
	 * @return
	 */
	private boolean discardChanges() {
		return MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), "File Changed",
			"The currently opened files were changed. Do you want to discard the changes and reload the file?");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		final SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		final int result = saveAsDialog.open();
		if (result == Window.OK) {
			final IPath path = saveAsDialog.getResult();
			setPartName(path.lastSegment());
			resourceSet.getResources().get(0)
			.setURI(URI.createFileURI(path.toOSString()));
			doSave(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
		throws PartInitException {
		setSite(site);
		setInput(input);

		// Set the Title for this Editor to the Name of the Input (= Filename)
		setPartName(input.getName());

		// As soon as the resource changed, we inform the Workspace, that it is
		// now dirty
		commandStack.addCommandStackListener(new CommandStackListener() {
			@Override
			public void commandStackChanged(EventObject event) {
				EcoreEditor.this.firePropertyChange(PROP_DIRTY);
			}
		});

		// Activate our context, so that our key-bindings are more important than
		// the default ones!
		((IContextService) site.getService(IContextService.class))
		.activateContext("org.eclipse.emfforms.spi.editor.context");

		site.getPage().addPartListener(partListener);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return commandStack.isSaveNeeded();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// Load the resource from the provided input and display the editor
		loadResource();
		parent.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		parent.setBackgroundMode(SWT.INHERIT_FORCE);

		rootView = new MasterDetailRenderer(parent, SWT.NONE, resourceSet);

		// We need to set the selectionProvider for the editor, so that the EditingDomainActionBarContributor
		// knows the currently selected object to copy/paste
		getEditorSite().setSelectionProvider(rootView.getSelectionProvider());

		// The EditingDomainActionBarContributor hooks undo/redo/copy/cut/paste actions to the
		// editor's actionbar and enables all these actions.
		final EditingDomainActionBarContributor actionBarProvider = new EditingDomainActionBarContributor();
		actionBarProvider.init(getEditorSite().getActionBars());
		actionBarProvider.setActiveEditor(this);
		actionBarProvider.activate();
	}

	/**
	 * Loads the Resource from the EditorInput and sets the EcoreEditor.resource
	 * field.
	 */
	private void loadResource() {
		final FileEditorInput fei = (FileEditorInput) getEditorInput();

		resourceSet = ResourceSetHelpers.loadResourceSetWithProxies(
			URI.createPlatformResourceURI(fei.getFile().getFullPath().toOSString(), false),
			commandStack);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// NOOP
	}

	/**
	 * Receives ExecutionEvents from ShortcutHandler and different actions
	 * accordingly.
	 *
	 * @param event the event
	 */
	public void processEvent(ExecutionEvent event) {
		final Object selection = rootView.getCurrentSelection();

		// We only create or delete elements for EObjects
		if (!(selection instanceof EObject)) {
			return;
		}

		final String commandName = event.getCommand().getId();
		final EObject currentSelection = (EObject) selection;

		final EditingDomain editingDomain = AdapterFactoryEditingDomain
			.getEditingDomainFor(currentSelection);

		if ("org.eclipse.emfforms.spi.editor.delete".equals(commandName)) {
			editingDomain.getCommandStack().execute(
				RemoveCommand.create(editingDomain, currentSelection));
		} else if ("org.eclipse.emfforms.spi.editor.new".equals(commandName)) {
			createNewElementDialog(editingDomain, currentSelection,
				"Create Child").open();
		} else if ("org.eclipse.emfforms.spi.editor.new.sibling".equals(commandName)) {
			// Get Parent of current Selection and show the dialog for it
			final EObject parent = currentSelection.eContainer();
			final EditingDomain parentEditingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(parent);
			createNewElementDialog(parentEditingDomain, parent, "Create Sibling").open();
		}
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
		final EObject selection, final String title) {
		return new CreateNewChildDialog(Display.getCurrent().getActiveShell(), title, selection,
			rootView.getSelectionProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EditingDomain getEditingDomain() {
		if (rootView == null) {
			return null;
		}
		return rootView.getEditingDomain();
	}
}
