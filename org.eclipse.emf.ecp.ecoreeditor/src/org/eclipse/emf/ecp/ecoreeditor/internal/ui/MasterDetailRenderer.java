package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.viewer.ColumnViewerInformationControlToolTipSupport;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.common.ChildrenDescriptorCollector;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;
import org.eclipse.emf.ecp.ecoreeditor.internal.Activator;
import org.eclipse.emf.ecp.ecoreeditor.internal.actions.CreateChildAction;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
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
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.osgi.framework.FrameworkUtil;

public class MasterDetailRenderer extends Composite {

	private final static String ITOOLBAR_ACTIONS_ID = "org.eclipse.emf.ecp.ecoreeditor.toolbarActions";
	
	private final Object input;
	private final EditingDomain editingDomain;
	
	private TreeViewer treeViewer = null;
	private Composite headerPanel = null;
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
		
		createHeader(this);
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
		new ColumnViewerInformationControlToolTipSupport(treeViewer, new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, treeViewer));
		treeViewer.setAutoExpandLevel(3);
		treeViewer.setInput(input);
	}
	
	protected void createHeader(Composite parent) {
		final Composite headerComposite = new Composite(parent, SWT.NONE);
		final GridLayout headerLayout = GridLayoutFactory.fillDefaults().create();
		headerComposite.setLayout(headerLayout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(headerComposite);
		headerComposite.setBackground(new Color(parent.getDisplay(), 207, 222, 238));

		final Composite header = getPageHeader(headerComposite);
		
		final ToolBar toolBar = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(formData);
		toolBar.layout();
		final ToolBarManager toolBarManager = new ToolBarManager(toolBar);

		/* Add actions to header */
		readToolbarActions(toolBarManager);
		
		toolBarManager.update(true);
		header.layout();

		this.headerPanel = headerComposite;
		// Put the Header on the top
		FormData headerFormData = new FormData(SWT.DEFAULT, 30);
		headerFormData.right = new FormAttachment(100, 0);
		headerFormData.top = new FormAttachment(0,0);
		headerFormData.left= new FormAttachment(0,0);
		
		this.headerPanel.setLayoutData(headerFormData);
	}

	
	private Composite getPageHeader(Composite parent) {
		final Composite header = new Composite(parent, SWT.FILL);
		final FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		header.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(header);

		header.setBackground(parent.getBackground());

		final Label titleImage = new Label(header, SWT.FILL);
		final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource("icons/view.png")); //$NON-NLS-1$
		titleImage.setImage(new Image(parent.getDisplay(), imageDescriptor.getImageData()));
		final FormData titleImageData = new FormData();
		final int imageOffset = -titleImage.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		titleImageData.top = new FormAttachment(50, imageOffset);
		titleImageData.left = new FormAttachment(0, 10);
		titleImage.setLayoutData(titleImageData);

		final Label title = new Label(header, SWT.WRAP);
		
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(title.getFont()).setHeight(12).setStyle(SWT.BOLD);
		
		Font boldFont = boldDescriptor.createFont(title.getDisplay());
		title.setForeground(new Color(parent.getDisplay(), 25, 76, 127));
		title.setFont( boldFont );
		
		title.setText("Ecore Editor"); //$NON-NLS-1$

		final FormData titleData = new FormData();
		title.setLayoutData(titleData);
		titleData.left = new FormAttachment(titleImage, 5, SWT.DEFAULT);

		return header;

	}

	private Control createTree(final Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER);
		
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
		treeFormData.bottom = new FormAttachment(100, -5);
		treeFormData.left = new FormAttachment(0, 5);
		treeFormData.top = new FormAttachment(headerPanel, 5);
		treeViewer.getControl().setLayoutData(treeFormData);
		
		return treeViewer.getControl();
	}
	
	private Control createDetailPanel(Composite parent) {
		if(detailPanel != null) {
			detailPanel.dispose();
		}
		
		detailPanel = new Composite(parent, SWT.BORDER);
		detailPanel.setLayout(new GridLayout());
		
		// Put the Details panel right to the tree and fix it to the right side of the form
		FormData detailFormData = new FormData();
		detailFormData.left = new FormAttachment(treeViewer.getControl(), 5);
		detailFormData.top  = new FormAttachment(headerPanel, 5);
		detailFormData.bottom = new FormAttachment(100,-5);
		detailFormData.right = new FormAttachment(100, -5);
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

	public Object getCurrentSelection() {
		if(!(treeViewer.getSelection() instanceof StructuredSelection))
			return null;
		return ((StructuredSelection)treeViewer.getSelection()).getFirstElement();
	}

	public void setSelection(StructuredSelection structuredSelection) {
		treeViewer.setSelection(structuredSelection);
	}
	
	private void readToolbarActions(ToolBarManager toolbar) {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry == null) {
			return;
		}
		
	    IConfigurationElement[] config =
	            registry.getConfigurationElementsFor(ITOOLBAR_ACTIONS_ID);
        try {
          for (IConfigurationElement e : config) {
            final Object o =
                e.createExecutableExtension("toolbarAction");
            if (o instanceof IToolbarAction) {
            	final IToolbarAction action = (IToolbarAction)o;
            	if(!action.canExecute(input)) {
            		continue;
            	}
            	
            	final Action newAction = new Action() {
					@Override
					public void run() {
						super.run();
						action.execute(input);
					}
				};

				newAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(action.getClass())
					.getResource(action.getImagePath())));
				newAction.setText(action.getLabel());
				toolbar.add(newAction);
            }
          }
        } catch (CoreException ex) {
          ex.printStackTrace();
        }
	}
}
