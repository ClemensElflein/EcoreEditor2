package org.eclipse.emf.ecp.ecoreeditor;

import java.net.MalformedURLException;
import java.util.EventObject;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import treeInput.TreeInput;
import treeInput.TreeInputFactory;

public class EcoreEditor extends EditorPart {

	// The Resource loaded from the provided EditorInput
	private ResourceSet resourceSet;

	// Use a simple CommandStack that can undo and redo nothing.
	private BasicCommandStack commandStack = new BasicCommandStack();

	// Save the TreeInput object, which is passed to the TreeMasterDetail
	private TreeInput treeInput;

	private IMenuManager menuManager;

	public EcoreEditor() {
		treeInput = TreeInputFactory.eINSTANCE.createTreeInput();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (ResourceSetHelpers.save(resourceSet)) {
			// Tell the CommandStack, that we have saved the file successfully
			// and inform the Workspace, that the Dirty property has changed.
			commandStack.saveIsDone();
			firePropertyChange(PROP_DIRTY);
		}
	}

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

		// Activate our context, so that our keybindings are more important than
		// the default ones!
		((IContextService) site.getService(IContextService.class))
				.activateContext("org.eclipse.emf.ecp.ecoreeditor.context");

		IMenuService mSvc = (IMenuService) site.getService(IMenuService.class);
		menuManager = site.getActionBars().getMenuManager();

	}

	@Override
	public boolean isDirty() {
		return commandStack.isSaveNeeded();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		loadResource();

		treeInput.setInput(resourceSet);

		try {
			ECPSWTViewRenderer.INSTANCE.render(parent, treeInput);
		} catch (final ECPRendererException ex) {
			Activator
					.getDefault()
					.getLog()
					.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex
							.getMessage(), ex));
		}
	}

	/*
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

	@Override
	public void setFocus() {
		// NOP
	}

	// Receives ExecutionEvents from ShortcutHandler and different actions
	// accordingly.
	public void processEvent(ExecutionEvent event) {
		String commandName = event.getCommand().getId();
		EObject currentSelection = treeInput.getTreeEditCallback()
				.getCurrentSelection();

		if (currentSelection == null) {
			return;
		}

		final EditingDomain editingDomain = AdapterFactoryEditingDomain
				.getEditingDomainFor(currentSelection);
		MenuManager asdf = new MenuManager();
		asdf.add(new Action("test") {
		});
		menuManager.add(asdf);
		asdf.setVisible(true);

		switch (commandName) {
		case "org.eclipse.emf.ecp.ecoreeditor.delete":
			Log.e(currentSelection.toString());

			editingDomain.getCommandStack().execute(
					RemoveCommand.create(editingDomain, currentSelection));

			// treeViewer.setSelection(new StructuredSelection(
			// getViewModelContext().getDomainModel()));
			break;
		}
	}
}
