package org.eclipse.emf.ecp.ecoreeditor;

import java.net.MalformedURLException;
import java.util.EventObject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceChangedListener;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceChangedNotification;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import treeInput.TreeInput;
import treeInput.TreeInputFactory;
import treeInput.TreeInputPackage;

public class EcoreEditor extends EditorPart {

	// The Resource loaded from the provided EditorInput
	private ResourceSet resourceSet;

	// Use a simple CommandStack that can undo and redo nothing.
	private BasicCommandStack commandStack = new BasicCommandStack();

	// Save the TreeInput object, which is passed to the TreeMasterDetail
	private TreeInput treeInput;

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

		EList<EObject> eObjects = new BasicEList<EObject>();

		for (Resource resource : resourceSet.getResources()) {
			eObjects.add(resource.getContents().get(0));
		}

		treeInput.eSet(TreeInputPackage.eINSTANCE.getTreeInput_TreeRoots(),
				eObjects);
		Log.e(resourceSet.toString());
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

			resourceSet.eAdapters().add(new ResourceChangedListener(treeInput));
			resourceSet.eNotify(new ResourceChangedNotification(resourceSet));
		} catch (MalformedURLException e) {
			Log.e(e);
		}
	}

	@Override
	public void setFocus() {
		// NOP
	}
}
