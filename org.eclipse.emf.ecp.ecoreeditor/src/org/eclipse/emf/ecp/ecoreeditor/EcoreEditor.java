package org.eclipse.emf.ecp.ecoreeditor;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.common.ChildrenDescriptorCollector;
import org.eclipse.emf.ecp.ecoreeditor.actions.CreateChildActionWithAccelerator;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTView;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

	private ECPSWTView rootView;

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
			rootView = ECPSWTViewRenderer.INSTANCE.render(parent, treeInput);
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

		createNewElementDialog(editingDomain, currentSelection).open();

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

	private PopupDialog createNewElementDialog(EditingDomain editingDomain,
			EObject selection) {
		final ChildrenDescriptorCollector childrenDescriptorCollector = new ChildrenDescriptorCollector();
		PopupDialog diag = new PopupDialog(Display.getDefault()
				.getActiveShell(), PopupDialog.INFOPOPUP_SHELLSTYLE, true,
				false, false, false, "Add Element", "") {
			@Override
			protected Control createContents(Composite parent) {
				final PopupDialog currentDialog = this;
				final List<Action> actions = fillContextMenu(
						childrenDescriptorCollector.getDescriptors(selection),
						editingDomain, selection);

				parent.setLayout(new FillLayout());

				TableViewer list = new TableViewer(parent);
				list.setContentProvider(new ArrayContentProvider());
				list.setLabelProvider(new LabelProvider() {
					@Override
					public String getText(Object element) {
						Action action = (Action) element;
						StringBuilder builder = new StringBuilder(action
								.getText());
						if (action.getAccelerator() > 0) {
							builder.append(" [");
							builder.append(Character.toUpperCase((char) action
									.getAccelerator()));
							builder.append("]");
						}
						return builder.toString();
					}

					@Override
					public Image getImage(Object element) {
						return ((Action) element).getImageDescriptor()
								.createImage();
					}
				});
				list.setInput(actions.toArray());
				list.addOpenListener(new IOpenListener() {

					@Override
					public void open(OpenEvent event) {
						Action action = (Action) ((StructuredSelection) event
								.getSelection()).getFirstElement();
						action.run();
						currentDialog.close();
					}
				});
				list.getControl().addKeyListener(new KeyListener() {

					@Override
					public void keyReleased(KeyEvent e) {
						// NOP
					}

					@Override
					public void keyPressed(KeyEvent e) {
						for (Action a : actions) {
							if (a.getAccelerator() == e.keyCode) {
								a.run();
								currentDialog.close();
								break;
							}
						}
					}
				});
				return parent;
			}
		};
		return diag;
	}

	private List<Action> fillContextMenu(Collection<?> descriptors,
			final EditingDomain domain, final EObject eObject) {

		List<Action> result = new ArrayList<Action>();

		for (final Object descriptor : descriptors) {

			final CommandParameter cp = (CommandParameter) descriptor;
			if (!CommandParameter.class.isInstance(descriptor)) {
				continue;
			}
			if (cp.getEReference() == null) {
				continue;
			}
			if (!cp.getEReference().isMany()
					&& eObject.eIsSet(cp.getEStructuralFeature())) {
				continue;
			} else if (cp.getEReference().isMany()
					&& cp.getEReference().getUpperBound() != -1
					&& cp.getEReference().getUpperBound() <= ((List<?>) eObject
							.eGet(cp.getEReference())).size()) {
				continue;
			}

			result.add(new CreateChildActionWithAccelerator(domain,
					new StructuredSelection(eObject), descriptor) {
				@Override
				public void run() {
					super.run();

					final EReference reference = ((CommandParameter) descriptor)
							.getEReference();
					// if (!reference.isContainment()) {
					// domain.getCommandStack().execute(
					// AddCommand.create(domain, eObject.eContainer(), null,
					// cp.getEValue()));
					// }

					domain.getCommandStack().execute(
							AddCommand.create(domain, eObject, reference,
									cp.getEValue()));
				}
			});
		}
		return result;
	}
}
