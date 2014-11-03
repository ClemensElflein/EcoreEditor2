/*******************************************************************************
 * Copyright (c) 2011-2014 EclipseSource Muenchen GmbH and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Alexandra Buzila- initial API and implementation
 ******************************************************************************/

package org.eclipse.emf.ecp.view.model.internal.preview.e3.views;

import java.io.IOException;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecp.ide.editor.view.ViewEditorPart;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.ecp.view.model.internal.preview.Activator;
import org.eclipse.emf.ecp.view.model.preview.common.Preview;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** The {@link ViewPart} containing a rendered version a {@link VView}. */
public class PreviewView extends ViewPart implements ISelectionListener {

	private Preview preView;
	private IPartListener2 partListener;
	private Composite form;
	private EContentAdapter adapter;
	private VView view;
	private boolean updateAutomatic;
	private Composite parent;
	private Action automaticToggleButton, manualRefreshButton;
	private Color headerBgColor;
	private Font titleFont;
	private Color titleColor;
	private ScrolledComposite scrolledComposite;
	private Composite container;
	private Action loadDataButton;
	private EObject sampleData;
	private Action cleanDataButton;

	/** The constructor. */
	public PreviewView() {
		super();
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
		//			.addSelectionListener("org.eclipse.emf.ecp.ui.ModelExplorerView", this); //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		super.dispose();
		getSite().getPage().removePartListener(partListener);
		preView.clear();
		preView.removeAdapter();
		titleColor.dispose();
		titleFont.dispose();
		headerBgColor.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		/* The container composite */
		form = new Composite(parent, SWT.BORDER);
		form.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		form.setLayout(GridLayoutFactory.fillDefaults().create());

		/* The header */
		final Composite headerComposite = new Composite(form, SWT.FILL);
		final GridLayout headerLayout = GridLayoutFactory.fillDefaults().create();
		headerComposite.setLayout(headerLayout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(headerComposite);
		headerBgColor = new Color(parent.getDisplay(), new RGB(220, 240, 247));
		headerComposite.setBackground(headerBgColor);

		final Composite header = getPageHeader(headerComposite);
		final ToolBar toolBar = new ToolBar(header, SWT.FLAT | SWT.RIGHT);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(formData);
		toolBar.layout();
		final ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		addButtonsToFormToolbar(toolBarManager);
		header.layout();

		/* The body */
		scrolledComposite = new ScrolledComposite(form, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setShowFocusedControl(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(scrolledComposite);
		scrolledComposite.setLayout(GridLayoutFactory.fillDefaults().create());
		scrolledComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		container = new Composite(scrolledComposite, SWT.FILL);
		final GridLayout containerLayout = GridLayoutFactory.fillDefaults().create();
		containerLayout.marginLeft = 10;
		containerLayout.marginRight = 10;
		container.setLayout(containerLayout);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(container);
		container.setBackground(scrolledComposite.getBackground());
		scrolledComposite.setContent(container);
		container.addPaintListener(new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				// super.paintControl(e);
				final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinSize(point);
				container.layout(true);
				scrolledComposite.layout(true);

			}
		});
		container.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinSize(point);
				container.layout(true);
				scrolledComposite.layout(true);

			}

			@Override
			public void controlMoved(ControlEvent e) {
				final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinSize(point);
				container.layout(true);
				scrolledComposite.layout(true);
			}
		});

		preView = new Preview(container);

		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();

		for (final IEditorReference reference : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			.getEditorReferences()) {
			final IEditorPart part = reference.getEditor(false);
			if (page.isPartVisible(part)) {
				if (ViewEditorPart.class.isInstance(part)) {
					final ViewEditorPart viewPart = (ViewEditorPart) part;
					setView(viewPart.getView());
					render(view);
				}
			}
		}

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		partListener = new IPartListener2() {

			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				if (ViewEditorPart.class.isInstance(partRef.getPart(true))) {
					final ViewEditorPart part = (ViewEditorPart) partRef.getPart(true);
					if (part.getView() != view) {
						setView(part.getView());
						sampleData = null;
						render(view);
					}
					if (updateAutomatic) {
						preView.registerForViewModelChanges();
					}
				}
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (PreviewView.class.isInstance(partRef.getPart(true))) {

					getSite().getPage().removePartListener(this);
				}

				if (ViewEditorPart.class.isInstance(partRef.getPart(true))) {
					if (updateAutomatic) {
						preView.clear();
					}
					preView.removeView();
					view = null;
				}

			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				final IWorkbenchPart part = partRef.getPart(true);
				if (ViewEditorPart.class.isInstance(part) || PreviewView.class.isInstance(part)) {
					removeAdapters();
				}
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}
		};
		getSite().getPage().addPartListener(partListener);

	}

	/**
	 * @param parent
	 * @return
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
		final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource("icons/preview.png")); //$NON-NLS-1$
		titleImage.setImage(new Image(parent.getDisplay(), imageDescriptor.getImageData()));
		final FormData titleImageData = new FormData();
		final int imageOffset = -titleImage.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		titleImageData.top = new FormAttachment(50, imageOffset);
		titleImageData.left = new FormAttachment(0, 10);
		titleImage.setLayoutData(titleImageData);

		final Label title = new Label(header, SWT.WRAP);
		title.setText("View Editor Preview"); //$NON-NLS-1$
		titleFont = new Font(title.getDisplay(), getDefaultFontName(title), 12, SWT.BOLD);
		title.setFont(titleFont);
		title.setForeground(getTitleColor());
		final FormData titleData = new FormData();
		title.setLayoutData(titleData);
		titleData.left = new FormAttachment(titleImage, 5, SWT.DEFAULT);

		return header;
	}

	/**
	 * @param control
	 * @return
	 */
	private String getDefaultFontName(Label control) {
		return control.getDisplay().getSystemFont().getFontData()[0].getName();
	}

	private Color getTitleColor() {
		if (titleColor == null) {
			titleColor = new Color(form.getDisplay(), new RGB(25, 76, 127));
		}
		return titleColor;
	}

	/**
	 * @param toolBarManager
	 */
	private void addButtonsToFormToolbar(IToolBarManager toolBarManager) {
		// toggle automatic refresh state button

		// automatic refresh
		automaticToggleButton = new Action("", IAction.AS_CHECK_BOX) { //$NON-NLS-1$
			@Override
			public void run() {
				super.run();
				setUpdateAutomatic(isChecked());
				manualRefreshButton.setEnabled(!isChecked());
			}
		};

		final String autoRefreshImagePath = "icons/arrow_rotate_anticlockwise.png";//$NON-NLS-1$
		automaticToggleButton.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource(autoRefreshImagePath)));

		automaticToggleButton.setText("Automatically refresh Preview View"); //$NON-NLS-1$
		automaticToggleButton.setEnabled(true);
		automaticToggleButton.setChecked(false);

		// manual refresh
		manualRefreshButton = new Action() {
			@Override
			public void run() {
				super.run();
				render();
			}
		};
		final String manualRefreshImagePath = "icons/arrow_refresh.png";//$NON-NLS-1$
		manualRefreshButton.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource(manualRefreshImagePath)));

		manualRefreshButton.setText("Refresh Preview View"); //$NON-NLS-1$
		manualRefreshButton.setEnabled(true);

		// load sample data
		loadDataButton = new Action() {
			@Override
			public void run() {
				super.run();
				loadSampleData();
			}
		};
		final String loadDataImagePath = "icons/loadData.png";//$NON-NLS-1$
		loadDataButton.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource(loadDataImagePath)));

		loadDataButton.setText("Load Sample Data"); //$NON-NLS-1$
		loadDataButton.setEnabled(true);

		// clean sampple data
		cleanDataButton = new Action() {
			@Override
			public void run() {
				super.run();
				sampleData = null;
				preView.cleanSampleData();
				render();
			}
		};
		final String cleanDataImagePath = "icons/cleanData.png";//$NON-NLS-1$
		cleanDataButton.setImageDescriptor(ImageDescriptor.createFromURL(Activator.getDefault()
			.getBundle()
			.getResource(cleanDataImagePath)));
		cleanDataButton.setText("Clean Sample Data"); //$NON-NLS-1$
		cleanDataButton.setEnabled(true);

		toolBarManager.add(cleanDataButton);
		toolBarManager.add(loadDataButton);
		toolBarManager.add(new Separator());
		toolBarManager.add(manualRefreshButton);
		toolBarManager.add(automaticToggleButton);

		toolBarManager.update(true);
	}

	/**
	 * 
	 */
	private void loadSampleData() {

		if (view == null || view.getRootEClass() == null) {
			return;
		}
		final FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*.xmi" }); //$NON-NLS-1$
		final String result = dialog.open();
		final ResourceSet rs = new ResourceSetImpl();
		final AdapterFactoryEditingDomain domain = new AdapterFactoryEditingDomain(
			new ComposedAdapterFactory(new AdapterFactory[] {
				new CustomReflectiveItemProviderAdapterFactory(),
				new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE) }),
			new BasicCommandStack(), rs);
		rs.eAdapters().add(
			new AdapterFactoryEditingDomain.EditingDomainProvider(domain));
		final Resource resource = rs.createResource(URI.createFileURI(result));

		try {
			resource.load(null);
		} catch (final IOException ex) {
			// do nothing - exceptions treated below
		}
		if (!resource.getContents().isEmpty()) {
			sampleData = resource.getContents().get(0);
			if (sampleData != null && sampleData.eClass() == view.getRootEClass()) {
				render();
			} else {
				new MessageDialog(
					parent.getShell(),
					"Wrong input type", //$NON-NLS-1$
					null,
					"The loaded file contains an EObject of type " //$NON-NLS-1$
						+ sampleData.eClass().getName()
						+ ".\n\n Please select an input file containig an EObject with the same type as the Root EClass of the view. (" + view.getRootEClass().getName() + ")", //$NON-NLS-1$ //$NON-NLS-2$
					MessageDialog.ERROR, new String[] { "Ok" }, 0).open(); //$NON-NLS-1$
				sampleData = null;
			}
		} else {
			sampleData = null;
			new MessageDialog(
				parent.getShell(), "Wrong input file content", //$NON-NLS-1$
				null, "The loaded file is incorrectly formatted.", //$NON-NLS-1$
				MessageDialog.ERROR, new String[] { "Ok" }, 0).open(); //$NON-NLS-1$
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		final IStructuredSelection ss = (IStructuredSelection) selection;
		final Object firstElement = ss.getFirstElement();
		if (!VView.class.isInstance(firstElement)) {
			return;
		}
		setView((VView) firstElement);
		render(view);
	}

	private void render(VView view) {
		if (adapter != null) {
			// remove adapter
			removeAdapters();

		}
		adapter = new EContentAdapter() {

			/**
			 * {@inheritDoc}
			 * 
			 * @see org.eclipse.emf.ecore.util.EContentAdapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
			 */
			@Override
			public void notifyChanged(Notification notification) {
				if (form.isDisposed()) {
					return;
				}
				super.notifyChanged(notification);
				if (notification.isTouch()) {
					return;
				}

				// TODO needed?
				if (EStructuralFeature.class.cast(notification.getFeature()).isTransient()) {
					return;
				}

				final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinSize(point);
				scrolledComposite.layout(true);
			}
		};

		view.eAdapters().add(adapter);
		preView.registerForViewModelChanges();
		preView.render(view, sampleData);
		final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolledComposite.setMinSize(point);
		scrolledComposite.layout(true);
	}

	private void setView(VView view) {
		if (this.view != view) {
			removeAdapters();
			this.view = view;
		}
	}

	private void removeAdapters() {
		if (view != null && adapter != null) {
			for (final Adapter a : view.eAdapters()) {
				if (a.equals(adapter)) {
					view.eAdapters().remove(adapter);
					adapter = null;
					break;
				}
			}
		}
		preView.removeAdapter();
	}

	/**
	 * @return the updateAutomatic
	 */
	public boolean isUpdateAutomatic() {
		return updateAutomatic;
	}

	/**
	 * @param updateAutomatic the updateAutomatic to set
	 */
	private void setUpdateAutomatic(boolean updateAutomatic) {
		this.updateAutomatic = updateAutomatic;
		if (preView != null) {
			preView.setUpdateAutomatic(updateAutomatic);
			final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			scrolledComposite.setMinSize(point);
			scrolledComposite.layout(true);
		}
	}

	private void render() {
		if (preView != null) {
			if (view != null) {
				render(view);
				final Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				scrolledComposite.setMinSize(point);
				scrolledComposite.layout(true);
				parent.layout();
			} else {
				preView.clear();
			}

		}
	}
}