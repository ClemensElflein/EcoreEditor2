package org.eclipse.emf.ecp.ecoreeditor.ui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.common.ChildrenDescriptorCollector;
import org.eclipse.emf.ecp.ecoreeditor.Activator;
import org.eclipse.emf.ecp.ecoreeditor.actions.CreateChildAction;
import org.eclipse.emf.ecp.ecoreeditor.treeinput.TreeInput;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.swt.layout.GridDescriptionFactory;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridCell;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridDescription;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.edit.ui.provider.DiagnosticDecorator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

public class MasterDetailRenderer extends Composite {

	private final Object input;
	private final EditingDomain editingDomain;
	
	private TreeViewer treeViewer = null;
	private Composite detailPanel = null;
	private Composite detailContainer = null;
	
	private static Map<String, Object> context = new LinkedHashMap<String, Object>();
	
	static {
		context.put("detail", true);
	}
	
	
	public MasterDetailRenderer(Composite parent, int style, Object input) {
		super(parent, style);
		this.input = input;
		this.editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(input);
		renderControl();
	}
	
	protected Control renderControl() {
		// Create the Form with two panels
		FormLayout parentLayout = new FormLayout();
		this.setLayout(parentLayout);
		this.setBackground(new Color(Display.getCurrent(), 255,0,0));;
		
		createTree(this);
		createDetailPanel(this);
		
		initializeTree();
		
		return this;
	}
	
	private void initializeTree() {
		final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(new AdapterFactory[] {
				new CustomReflectiveItemProviderAdapterFactory(),
				new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE) });

		final AdapterFactoryContentProvider adapterFactoryContentProvider = new AdapterFactoryContentProvider(
			adapterFactory);

		treeViewer.setContentProvider(adapterFactoryContentProvider);
		treeViewer.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(adapterFactory), new DiagnosticDecorator(editingDomain, treeViewer)));
		
		treeViewer.setInput(input);
	}
	

	private Control createTree(final Composite parent) {
		treeViewer = new TreeViewer(parent);
		treeViewer.getControl().setBackground(new Color(Display.getCurrent(), 0, 255, 0));
		
		// Add selection listener to show the detail page
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				createDetailPanel(parent);
				Object selectedObject = ((StructuredSelection)event.getSelection()).getFirstElement();
				if(selectedObject instanceof EObject) {
					EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor((EObject)selectedObject);
					
					try {
						ECPSWTViewRenderer.INSTANCE.render(detailPanel, (EObject)selectedObject, context);
						detailPanel.layout(true, true);
					} catch (ECPRendererException e) {
					}
					
					fillContextMenu(treeViewer, editingDomain);
				}
			}
		});
		
		
		
		// Put the TreeViewer on the left hand side of the form
		FormData treeFormData = new FormData(300, SWT.DEFAULT);
		treeFormData.bottom = new FormAttachment(100, 0);
		treeFormData.top = new FormAttachment(0,0);
		treeViewer.getControl().setLayoutData(treeFormData);
		
		return treeViewer.getControl();
	}
	
	private Control createDetailPanel(Composite parent) {
		if(detailPanel != null) {
			detailPanel.dispose();
		}
		
		detailPanel = new Composite(parent, SWT.NONE);
		detailPanel.setBackground(new Color(Display.getCurrent(), 0, 255, 255));
		detailPanel.setLayout(new GridLayout());
		
		// Put the Details panel right to the tree and fix it to the right side of the form
		FormData detailFormData = new FormData();
		detailFormData.left = new FormAttachment(treeViewer.getControl(), 5);
		detailFormData.top  = new FormAttachment(0, 0);
		detailFormData.bottom = new FormAttachment(100,0);
		detailFormData.right = new FormAttachment(100, 0);
		detailPanel.setLayoutData(detailFormData);
		
		parent.layout(true, true);
		return detailPanel;
	}
	

	private void fillContextMenu(final TreeViewer treeViewer, final EditingDomain editingDomain) {
		final ChildrenDescriptorCollector childrenDescriptorCollector = new ChildrenDescriptorCollector();
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (treeViewer.getSelection().isEmpty()) {
					return;
				}
				if (treeViewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

					if (selection.size() == 1) {
						final EObject eObject = (EObject) selection.getFirstElement();
						final EditingDomain domain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
						if (domain == null) {
							return;
						}
						final Collection<?> descriptors = childrenDescriptorCollector.getDescriptors(eObject);
						fillContextMenu(manager, descriptors, editingDomain, eObject);
					}
					manager.add(new Separator());
					addDeleteActionToContextMenu(editingDomain, menuMgr, selection);
				}
			}
		});
		final Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
	}
	
	/**
	 * @param manager The menu manager responsible for the context menu
	 * @param descriptors The menu items to be added
	 * @param domain The editing domain of the current EObject
	 * @param eObject The model element
	 */
	private void fillContextMenu(IMenuManager manager, Collection<?> descriptors, final EditingDomain domain,
		final EObject eObject) {
		for (final Object descriptor : descriptors) {

			final CommandParameter cp = (CommandParameter) descriptor;
			if (!CommandParameter.class.isInstance(descriptor)) {
				continue;
			}
			if (cp.getEReference() == null) {
				continue;
			}
			if (!cp.getEReference().isMany() && eObject.eIsSet(cp.getEStructuralFeature())) {
				continue;
			} else if (cp.getEReference().isMany() && cp.getEReference().getUpperBound() != -1
				&& cp.getEReference().getUpperBound() <= ((List<?>) eObject.eGet(cp.getEReference())).size()) {
				continue;
			}

			manager.add(new CreateChildAction(domain, new StructuredSelection(eObject), descriptor) {
				@Override
				public void run() {
					super.run();

					final EReference reference = ((CommandParameter) descriptor).getEReference();
					// if (!reference.isContainment()) {
					// domain.getCommandStack().execute(
					// AddCommand.create(domain, eObject.eContainer(), null, cp.getEValue()));
					// }

					domain.getCommandStack().execute(
						AddCommand.create(domain, eObject, reference, cp.getEValue()));
				}
			});
		}

	}

	/**
	 * @param editingDomain
	 * @param manager
	 * @param selection
	 */
	private void addDeleteActionToContextMenu(final EditingDomain editingDomain, final IMenuManager manager,
		final IStructuredSelection selection) {

		// Create the RemovEommand and check, if it can be executed.
		// If it can't, don't create a menu item
		final Command removeCommand = RemoveCommand.create(editingDomain, selection.toList());
		
		if(!removeCommand.canExecute())
			return;
		
		final Action deleteAction = new Action() {
			@Override
			public void run() {
				super.run();
				editingDomain.getCommandStack().execute(removeCommand);
				treeViewer.setSelection(new StructuredSelection(input));
			}
		};
		
		final String deleteImagePath = "icons/delete.png";//$NON-NLS-1$
		deleteAction.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource(deleteImagePath)));
		deleteAction.setText("Delete"); //$NON-NLS-1$
		manager.add(deleteAction);
	}
	
}
