package ecoreeditor2;

import java.net.MalformedURLException;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import ecoreeditor2.helpers.ResourceSetHelpers;

public class EcoreEditor extends EditorPart {

	// The Resource loaded from the provided EditorInput
	private ResourceSet resourceSet;
	
	// Use a simple CommandStack that can undo and redo nothing.
	private BasicCommandStack commandStack = new BasicCommandStack();
	
	public EcoreEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if(ResourceSetHelpers.save(resourceSet)) {
			// Tell the CommandStack, that we have saved the file successfully
			// and inform the Workspace, that the Dirty property has changed.
			commandStack.saveIsDone();
			firePropertyChange(PROP_DIRTY);
		}
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
		// Set the Title for this Editor to the Name of the Input (= Filename)
		setPartName(input.getName());
		
		// As soon as the resource changed, we inform the Workspace, that it is now dirty
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
		// We don't allow the SaveAs operation at this time.
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		loadResource();
		
		List<EPackage> ePackages = new LinkedList<EPackage>();
		
		for(Resource resource : resourceSet.getResources()) {
			ePackages.add((EPackage) resource.getContents().get(0));
		}

		Log.i(ePackages.size() + " Packages found!");
		
		try {
			ECPSWTViewRenderer.INSTANCE.render(parent, ePackages.get(1));
			
		} catch (final ECPRendererException ex) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex));
		}
	}
	
	/*
	 * Loads the Resource from the EditorInput and sets the EcoreEditor.resource field.
	 */
	private void loadResource() {
		final FileEditorInput fei = (FileEditorInput) getEditorInput();
		try {
			resourceSet = ResourceSetHelpers.loadResourceSetWithProxies(URI.createURI(fei.getURI().toURL().toExternalForm()), commandStack);
		} catch (MalformedURLException e) {
			Log.e(e);
		}
	}

	@Override
	public void setFocus() {
		// NOP
	}
}
