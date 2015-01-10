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
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ecoreeditor.treeinput.TreeInput;
import org.eclipse.emf.ecp.ecoreeditor.treeinput.TreeInputFactory;
import org.eclipse.emf.ecp.ecoreeditor.ui.MasterDetailRenderer;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.model.VViewFactory;
import org.eclipse.emf.ecp.view.spi.model.util.ViewModelUtil;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.internal.quickaccess.ViewProvider;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;


public class EcoreEditor extends EditorPart {

	// The Resource loaded from the provided EditorInput
	private ResourceSet resourceSet;

	// Use a simple CommandStack that can undo and redo nothing.
	private BasicCommandStack commandStack = new BasicCommandStack();

	private IMenuManager menuManager;

	private MasterDetailRenderer rootView;

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

		// Load fontawesome
		/*Log.i("Now loading Fontawesome 2!");
		Log.i(FileLocator.find(Activator.getDefault().getBundle(), new Path("fonts/fontawesome-webfont.ttf"), null).toString());
		
		Display.getCurrent().loadFont(FileLocator.find(Activator.getDefault().getBundle(), new Path("fonts/fontawesome-webfont.ttf"), null).toString());
		*/
		
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
		this.rootView = new MasterDetailRenderer(parent, SWT.NONE, resourceSet);
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
		
	}
}
