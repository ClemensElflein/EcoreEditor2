/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal;

import java.net.MalformedURLException;
import java.util.EventObject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.Log;
import org.eclipse.emf.ecp.ecoreeditor.internal.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ecoreeditor.internal.ui.CreateNewChildDialog;
import org.eclipse.emf.ecp.ecoreeditor.internal.ui.MasterDetailRenderer;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.ui.action.EditingDomainActionBarContributor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;


/**
 * The Class EcoreEditor it is the generic part for editing any EObject
 */
public class EcoreEditor extends EditorPart implements IEditingDomainProvider {

	/** The Resource loaded from the provided EditorInput */
	private ResourceSet resourceSet;

	/** The command stack. It is used to mark the editor as dirty as well as undo/redo operations */
	private BasicCommandStack commandStack = new BasicCommandStack();

	/** The root view. It is the main Editor panel. */
	private MasterDetailRenderer rootView;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (ResourceSetHelpers.save(resourceSet)) {
			// Tell the CommandStack, that we have saved the file successfully
			// and inform the Workspace, that the Dirty property has changed.
			commandStack.saveIsDone();
			firePropertyChange(PROP_DIRTY);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		int result = saveAsDialog.open();
		if (result == Window.OK) {
			IPath path = saveAsDialog.getResult();
			setPartName(path.lastSegment());
			resourceSet.getResources().get(0)
					.setURI(URI.createFileURI(path.toOSString()));
			doSave(null);
		}
	}

	/* (non-Javadoc)
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
				.activateContext("org.eclipse.emf.ecp.ecoreeditor.context");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return commandStack.isSaveNeeded();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// Load the resource from the provided input and display the editor
		loadResource();
		parent.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		
		this.rootView = new MasterDetailRenderer(parent, SWT.NONE, resourceSet);
		
		// We need to set the selectionProvider for the editor, so that the EditingDomainActionBarContributor
		// knows the currently selected object to copy/paste
		getEditorSite().setSelectionProvider(rootView.getSelectionProvider());
		
		// The EditingDomainActionBarContributor hooks undo/redo/copy/cut/paste actions to the
		// editor's actionbar and enables all these actions.
		EditingDomainActionBarContributor actionBarProvider = new EditingDomainActionBarContributor();
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
		try {

			resourceSet = ResourceSetHelpers.loadResourceSetWithProxies(
					URI.createURI(fei.getURI().toURL().toExternalForm()),
					commandStack);
		} catch (MalformedURLException e) {
			Log.e(e);
		}
	}

	/* (non-Javadoc)
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
		Object selection = rootView.getCurrentSelection();

		// We only create or delete elements for EObjects
		if(!(selection instanceof EObject)) {
			return;
		}
		
		String commandName = event.getCommand().getId();
		EObject currentSelection = (EObject) selection;

		EditingDomain editingDomain = AdapterFactoryEditingDomain
				.getEditingDomainFor(currentSelection);

		if("org.eclipse.emf.ecp.ecoreeditor.delete".equals(commandName)) {
			editingDomain.getCommandStack().execute(
					RemoveCommand.create(editingDomain, currentSelection));
		} else if("org.eclipse.emf.ecp.ecoreeditor.new".equals(commandName)) {
			createNewElementDialog(editingDomain, currentSelection,
					"Create Child").open();
		} else if("org.eclipse.emf.ecp.ecoreeditor.new.sibling".equals(commandName)) {
			// Get Parent of current Selection and show the dialog for it
			EObject parent = currentSelection.eContainer();
			EditingDomain parentEditingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(parent);
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
		return new CreateNewChildDialog(Display.getCurrent().getActiveShell(), title, selection, rootView.getSelectionProvider());
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.edit.domain.IEditingDomainProvider#getEditingDomain()
	 */
	@Override
	public EditingDomain getEditingDomain() {
		if(rootView == null) {
			return null;
		}
		return rootView.getEditingDomain();
	}
}
