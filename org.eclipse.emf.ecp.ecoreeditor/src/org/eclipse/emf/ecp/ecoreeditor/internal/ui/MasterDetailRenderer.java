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

package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

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
import org.eclipse.emf.ecp.common.spi.ChildrenDescriptorCollector;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;
import org.eclipse.emf.ecp.ecoreeditor.internal.Activator;
import org.eclipse.emf.ecp.ecoreeditor.internal.CreateDialog;
import org.eclipse.emf.ecp.ecoreeditor.internal.helpers.EcoreHelpers;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
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
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.osgi.framework.FrameworkUtil;

/**
 * The Class MasterDetailRenderer.
 * It is the base renderer for the editor.
 *
 * It takes any object as input and renders a tree on the left-hand side.
 * When selecting an item in the tree (that is an EObject) EMF-Forms is used to render the detail pane on the right-hand
 * side
 *
 * MasterDetailRenderer implements IEditingDomainProvider to allow Undo/Redo/Copy/Cut/Paste actions to be performed
 * externally.
 *
 * MasterDetailRenderer provides an ISelectionProvider to get the currently selected items in the tree
 *
 */
public class MasterDetailRenderer extends Composite implements IEditingDomainProvider {

	/** The Toolbar action extensionpoint ID. */
	private static final String ITOOLBAR_ACTIONS_ID = "org.eclipse.emf.ecp.ecoreeditor.toolbarActions";

	/** The input. */
	private final Object input;

	/** The editing domain. */
	private final EditingDomain editingDomain;

	/** The tree viewer. */
	private TreeViewer treeViewer;

	/** The vertical sash. */
	private Sash verticalSash;

	/** The header panel. */
	private Composite headerPanel;

	/** The detail scrollable composite. */
	private ScrolledComposite detailScrollableComposite;

	/** The detail panel. */
	private Composite detailPanel;

	/**
	 * The context. It is used in the same way as in TreeMasterDetail.
	 * It allows custom viewmodels for the detail panel
	 */
	private static Map<String, Object> context = new LinkedHashMap<String, Object>();

	static {
		context.put("detail", true);
	}

	/**
	 * Instantiates a new master detail renderer.
	 *
	 * @param parent the parent
	 * @param style the style
	 * @param input the input
	 */
	public MasterDetailRenderer(Composite parent, int style, Object input) {
		super(parent, style);
		this.input = input;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(input);
		renderControl();
	}

	/**
	 * Render the control.
	 *
	 * @return the control
	 */
	protected Control renderControl() {
		// Create the Form with two panels and a header
		final FormLayout parentLayout = new FormLayout();
		setLayout(parentLayout);

		// First create the header with the toolbar, then the separator.
		createHeader(this);
		createSash(this);

		// Attach the tree and the detail container to the Sash
		createTree(this);
		createDetailScrollableComposite(this);

		// Set the Label and Content providers, set the input and select the default
		initializeTree();

		updateDetailPanel();

		return this;
	}

	/**
	 * Creates the header with the toolbar and its containing actions.
	 *
	 * @param parent the parent
	 */
	protected void createHeader(Composite parent) {
		final Composite headerComposite = new Composite(parent, SWT.NONE);
		final GridLayout headerLayout = GridLayoutFactory.fillDefaults().create();
		headerComposite.setLayout(headerLayout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(headerComposite);
		headerComposite.setBackground(new Color(parent.getDisplay(), 207, 222, 238));

		final Composite header = getPageHeader(headerComposite);

		// Create the toolbar and add it to the header
		final ToolBar toolBar = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(formData);
		toolBar.layout();
		final ToolBarManager toolBarManager = new ToolBarManager(toolBar);

		/* Add actions to header */
		addStaticActions(toolBarManager);
		readToolbarActions(toolBarManager);

		toolBarManager.update(true);
		header.layout();

		headerPanel = headerComposite;
		// Put the Header on the top and make it 30px high
		final FormData headerFormData = new FormData(SWT.DEFAULT, 30);
		headerFormData.right = new FormAttachment(100, 0);
		headerFormData.top = new FormAttachment(0, 0);
		headerFormData.left = new FormAttachment(0, 0);

		headerPanel.setLayoutData(headerFormData);
	}

	/**
	 * Gets the page header.
	 * This is the part of the toolbar with the icon and title
	 *
	 * @param parent the parent
	 * @return the page header
	 */
	private Composite getPageHeader(Composite parent) {
		final Composite header = new Composite(parent, SWT.FILL);
		final FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		header.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(header);

		header.setBackground(parent.getBackground());

		final Label titleImage = new Label(header, SWT.FILL);
		final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(Activator.getDefault().getBundle()
			.getResource("icons/view.png"));
		titleImage.setImage(new Image(parent.getDisplay(), imageDescriptor.getImageData()));
		final FormData titleImageData = new FormData();
		final int imageOffset = -titleImage.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		titleImageData.top = new FormAttachment(50, imageOffset);
		titleImageData.left = new FormAttachment(0, 10);
		titleImage.setLayoutData(titleImageData);

		final Label title = new Label(header, SWT.WRAP);

		final FontDescriptor boldDescriptor = FontDescriptor.createFrom(title.getFont()).setHeight(12)
			.setStyle(SWT.BOLD);

		final Font boldFont = boldDescriptor.createFont(title.getDisplay());
		title.setForeground(new Color(parent.getDisplay(), 25, 76, 127));
		title.setFont(boldFont);

		title.setText("Ecore Editor"); //$NON-NLS-1$

		final FormData titleData = new FormData();
		title.setLayoutData(titleData);
		titleData.left = new FormAttachment(titleImage, 5, SWT.DEFAULT);

		return header;

	}

	/**
	 * Creates the sash for separation of the tree and the detail pane.
	 *
	 * @param parent the parent
	 */
	private void createSash(final Composite parent) {
		final Sash sash = new Sash(parent, SWT.VERTICAL);

		// Make the left panel 300px wide and put it below the header
		final FormData sashFormData = new FormData();
		sashFormData.bottom = new FormAttachment(100, -5);
		sashFormData.left = new FormAttachment(0, 300);
		sashFormData.top = new FormAttachment(headerPanel, 5);
		sash.setLayoutData(sashFormData);

		// As soon as the sash is moved, layout the parent to reflect the changes
		sash.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event e) {
				sash.setLocation(e.x, e.y);

				final FormData sashFormData = new FormData();
				sashFormData.bottom = new FormAttachment(100, -5);
				sashFormData.left = new FormAttachment(0, e.x);
				sashFormData.top = new FormAttachment(headerPanel, 5);
				sash.setLayoutData(sashFormData);
				parent.layout(true);
			}
		});

		verticalSash = sash;
	}

	/**
	 * Creates the tree.
	 *
	 * @param parent the parent
	 * @return the control
	 */
	private Control createTree(final Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER);

		// Add selection listener to show the detail page
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateDetailPanel();
			}
		});

		// Allow DnD in the tree
		addDragAndDropSupport(treeViewer, editingDomain);

		// Put the TreeViewer on the left hand side of the form
		final FormData treeFormData = new FormData();
		treeFormData.bottom = new FormAttachment(100, -5);
		treeFormData.left = new FormAttachment(0, 5);
		treeFormData.right = new FormAttachment(verticalSash, -2);
		treeFormData.top = new FormAttachment(headerPanel, 5);
		treeViewer.getControl().setLayoutData(treeFormData);

		return treeViewer.getControl();
	}

	private void updateDetailPanel() {
		// Create a new detail panel in the scrollable composite. Disposes any old panels.
		createDetailPanel();

		// Get the selected object, if it is an EObject, render the details using EMF Forms
		final Object selectedObject = treeViewer.getSelection() != null ? ((StructuredSelection) treeViewer
			.getSelection()).getFirstElement() : null;
			if (selectedObject instanceof EObject) {
				try {
					ECPSWTViewRenderer.INSTANCE.render(detailPanel, (EObject) selectedObject, context);
					detailPanel.layout(true, true);
				} catch (final ECPRendererException e) {
				}
				// After rendering the Forms, compute the size of the form. So the scroll container knows when to scroll
				detailScrollableComposite.setMinSize(detailPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

				// Set the context menu for creation of new elements
				fillContextMenu(treeViewer, editingDomain);
			} else {
				final Label hint = new Label(detailPanel, SWT.CENTER);
				final FontDescriptor boldDescriptor = FontDescriptor.createFrom(hint.getFont()).setHeight(18)
					.setStyle(SWT.BOLD);
				final Font boldFont = boldDescriptor.createFont(hint.getDisplay());
				hint.setFont(boldFont);
				hint.setForeground(new Color(hint.getDisplay(), 190, 190, 190));
				hint.setText("Select a node in the tree to edit it");
				final GridData hintLayoutData = new GridData();
				hintLayoutData.grabExcessVerticalSpace = true;
				hintLayoutData.grabExcessHorizontalSpace = true;
				hintLayoutData.horizontalAlignment = SWT.CENTER;
				hintLayoutData.verticalAlignment = SWT.CENTER;
				hint.setLayoutData(hintLayoutData);

				detailPanel.pack();
				detailPanel.layout(true, true);

				detailScrollableComposite.setMinSize(detailPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
	}

	/**
	 * Creates the detail scrollable composite.
	 *
	 * @param parent the parent
	 */
	private void createDetailScrollableComposite(Composite parent) {
		detailScrollableComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		detailScrollableComposite.setExpandHorizontal(true);
		detailScrollableComposite.setExpandVertical(true);
		// Put the Details panel right to the tree and fix it to the right side
		// of the form
		final FormData detailFormData = new FormData();
		detailFormData.left = new FormAttachment(verticalSash, 2);
		detailFormData.top = new FormAttachment(headerPanel, 5);
		detailFormData.bottom = new FormAttachment(100, -5);
		detailFormData.right = new FormAttachment(100, -5);
		detailScrollableComposite.setLayoutData(detailFormData);

	}

	/**
	 * Creates the detail panel.
	 *
	 * @return the control
	 */
	private Control createDetailPanel() {
		// Dispose old panels to avoid memory leaks
		if (detailPanel != null) {
			detailPanel.dispose();
		}

		detailPanel = new Composite(detailScrollableComposite, SWT.BORDER);
		detailPanel.setLayout(new GridLayout());
		detailScrollableComposite.setContent(detailPanel);

		detailScrollableComposite.layout(true, true);
		return detailPanel;
	}

	/**
	 * Initialize the treeViewer.
	 */
	private void initializeTree() {
		final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(new AdapterFactory[] {
			new CustomReflectiveItemProviderAdapterFactory(),
			new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE) });

		final AdapterFactoryContentProvider adapterFactoryContentProvider = new AdapterFactoryContentProvider(
			adapterFactory) {

			@Override
			public boolean hasChildren(Object object) {
				return getChildren(object).length > 0;
			}

			@Override
			public Object[] getChildren(Object object) {
				// Filter all generic children
				return EcoreHelpers.filterGenericElements(super.getChildren(object));
			}
		};

		treeViewer.setContentProvider(adapterFactoryContentProvider);
		treeViewer.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(adapterFactory),
			new DiagnosticDecorator(editingDomain, treeViewer)));
		new ColumnViewerInformationControlToolTipSupport(treeViewer,
			new DiagnosticDecorator.EditingDomainLocationListener(editingDomain, treeViewer));
		treeViewer.setAutoExpandLevel(3);
		treeViewer.setInput(input);

		// Scan the input for the first EObject and select it
		final EObject initialSelection = findInitialSelection(adapterFactoryContentProvider, input);
		if (initialSelection != null) {
			treeViewer.setSelection(new StructuredSelection(initialSelection), true);
		}
	}

	/**
	 * Find initial selection.
	 * Recursively finds the first EObject in the input.
	 *
	 * @param contentProvider the content provider
	 * @param input the input
	 * @return the EObject to select by default
	 */
	private EObject findInitialSelection(AdapterFactoryContentProvider contentProvider, Object input) {
		if (input instanceof EObject) {
			return (EObject) input;
		}
		for (final Object child : contentProvider.getChildren(input)) {
			final EObject childSelector = findInitialSelection(contentProvider, child);
			if (childSelector != null) {
				return childSelector;
			}
		}
		return null;
	}

	/**
	 * Fill context menu.
	 * Fills the context menu. Adds create actions for all possible children and a delete action.
	 *
	 * @param treeViewer the tree viewer
	 * @param editingDomain the editing domain
	 */
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

					if (selection.size() == 1 && selection.getFirstElement() instanceof EObject) {
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
	 * Fill context menu.
	 *
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
			if (EcoreHelpers.isGenericFeature(cp.getFeature())) {
				// This ensures, that we won't show any generic features anymore
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

					final EReference reference = ((CommandParameter) descriptor).getEReference();

					final CreateDialog diag = new CreateDialog(Display.getCurrent().getActiveShell(), cp.getEValue()
						.eClass());

					final int result = diag.open();

					if (result == Window.OK) {
						final EObject newElement = diag.getCreatedInstance();
						domain.getCommandStack().execute(AddCommand.create(domain, eObject, reference, newElement));

						// Select the newly added element as soon as the AddCommand was executed
						treeViewer.refresh();
						treeViewer.setSelection(new StructuredSelection(newElement));
					}
				}
			});
		}

	}

	/**
	 * Adds the delete action to context menu.
	 *
	 * @param editingDomain the editing domain
	 * @param manager the manager
	 * @param selection the selection
	 */
	private void addDeleteActionToContextMenu(final EditingDomain editingDomain, final IMenuManager manager,
		final IStructuredSelection selection) {

		// Create the RemovEommand and check, if it can be executed.
		// If it can't, don't create a menu item
		final Command removeCommand = RemoveCommand.create(editingDomain, selection.toList());

		if (!removeCommand.canExecute()) {
			return;
		}

		final Action deleteAction = new Action() {
			@Override
			public void run() {
				super.run();
				editingDomain.getCommandStack().execute(removeCommand);
				treeViewer.setSelection(new StructuredSelection(input));
			}
		};

		final String deleteImagePath = "icons/delete.png";//$NON-NLS-1$
		deleteAction.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault().getBundle()
			.getResource(deleteImagePath)));
		deleteAction.setText("Delete"); //$NON-NLS-1$
		manager.add(deleteAction);
	}

	/**
	 * Gets the current selection.
	 *
	 * @return the current selection
	 */
	public Object getCurrentSelection() {
		if (!(treeViewer.getSelection() instanceof StructuredSelection)) {
			return null;
		}
		return ((StructuredSelection) treeViewer.getSelection()).getFirstElement();
	}

	/**
	 * Sets the selection.
	 *
	 * @param structuredSelection the new selection
	 */
	public void setSelection(StructuredSelection structuredSelection) {
		treeViewer.setSelection(structuredSelection);
	}

	/**
	 * Add actions that are always present (e.g. add model elements, delete model elements)
	 *
	 * @param toolbar the toolbar to add the actions to
	 */
	private void addStaticActions(ToolBarManager toolbar) {
		// Add Element Action
		final Action addElementAction = new Action() {
			@Override
			public void run() {
				super.run();
				final Object selection = getCurrentSelection();

				if (!(selection instanceof EObject)) {
					return;
				}

				final EObject eSelection = (EObject) selection;

				new CreateNewChildDialog(Display.getCurrent().getActiveShell(), "Create Child", eSelection, treeViewer)
				.open();
			}
		};
		addElementAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(this.getClass())
			.getResource("icons/add.png")));
		addElementAction.setText("Add Element");
		toolbar.add(addElementAction);

		// Delete Element Action
		final Action deleteElementAction = new Action() {
			@Override
			public void run() {
				super.run();

				final Object selection = getCurrentSelection();

				if (!(selection instanceof EObject)) {
					return;
				}

				final EObject eSelection = (EObject) selection;

				editingDomain.getCommandStack().execute(
					RemoveCommand.create(editingDomain, eSelection));
			}
		};
		deleteElementAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(this.getClass())
			.getResource("icons/delete.png")));
		deleteElementAction.setText("Delete Selected Element");
		toolbar.add(deleteElementAction);

	}

	/**
	 * Read toolbar actions from all extensions.
	 *
	 * @param toolbar the toolbar to add the actions to
	 */
	private void readToolbarActions(ToolBarManager toolbar) {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry == null) {
			return;
		}

		final IConfigurationElement[] config = registry.getConfigurationElementsFor(ITOOLBAR_ACTIONS_ID);
		try {
			for (final IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("toolbarAction");
				if (o instanceof IToolbarAction) {
					final IToolbarAction action = (IToolbarAction) o;
					if (!action.canExecute(input)) {
						continue;
					}

					final Action newAction = new Action() {
						@Override
						public void run() {
							super.run();
							action.execute(input);
						}
					};

					newAction.setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(
						action.getClass()).getResource(action.getImagePath())));
					newAction.setText(action.getLabel());
					toolbar.add(newAction);
				}
			}
		} catch (final CoreException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Adds the drag and drop support to the treeViewer.
	 *
	 * @param treeViewer the tree viewer
	 * @param editingDomain the editing domain
	 */
	private void addDragAndDropSupport(final TreeViewer treeViewer,
		EditingDomain editingDomain) {

		final int dndOperations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		final Transfer[] transfers = new Transfer[] { LocalTransfer.getInstance() };
		treeViewer.addDragSupport(dndOperations, transfers, new ViewerDragAdapter(treeViewer));
		final EditingDomainViewerDropAdapter editingDomainViewerDropAdapter = new EditingDomainViewerDropAdapter(
			editingDomain, treeViewer);
		treeViewer.addDropSupport(dndOperations, transfers, editingDomainViewerDropAdapter);
	}

	/**
	 * Gets the selection provider.
	 *
	 * @return the selection provider
	 */
	public ISelectionProvider getSelectionProvider() {
		return treeViewer;
	}

	/**
	 * Gets the editing domain.
	 *
	 * @return the editing domain
	 */
	@Override
	public EditingDomain getEditingDomain() {
		return editingDomain;
	}
}
