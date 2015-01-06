package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import java.util.HashMap;

import javax.swing.text.Style;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.ecoreeditor.IconButton;
import org.eclipse.emf.ecp.ecoreeditor.IconButton.Icon;
import org.eclipse.emf.ecp.ecoreeditor.Log;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlJFaceViewerSWTRenderer;
import org.eclipse.emf.ecp.view.spi.model.LabelAlignment;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.swt.AbstractSWTRenderer;
import org.eclipse.emf.ecp.view.spi.swt.layout.GridDescriptionFactory;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridCell;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridDescription;
import org.eclipse.emf.ecp.view.template.style.mandatory.model.VTMandatoryStyleProperty;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.dnd.EditingDomainViewerDropAdapter;
import org.eclipse.emf.edit.ui.dnd.LocalTransfer;
import org.eclipse.emf.edit.ui.dnd.ViewerDragAdapter;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class EEnumLiteralsControl extends AbstractSWTRenderer<VControl> {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.view.spi.swt.AbstractSWTRenderer#getGridDescription(SWTGridDescription)
	 */
	@Override
	public final SWTGridDescription getGridDescription(SWTGridDescription gridDescription) {
		return GridDescriptionFactory.INSTANCE.createSimpleGrid(2,3, this);
	}
	
	private final void addColumn(TableViewer viewer, EStructuralFeature feature, EditingSupport editingSupport) {
		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.LEFT);
		tvc.getColumn().setText(feature.getName());
		tvc.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(String.valueOf(((EObject)cell.getElement()).eGet(feature)));
			}
		});
		tvc.getColumn().setWidth(100);
		
		if(editingSupport != null) {
			tvc.setEditingSupport(editingSupport);
		}
	}

	@Override
	protected Control renderControl(SWTGridCell cell, Composite parent)
			throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		int col = cell.getColumn();
		int row = cell.getRow();
		
		switch(col) {
		case 0:
			createLabel(parent);
			break;
		case 1:
			new Composite(parent, SWT.NONE);
			break;
		case 2:
			createControl(parent);
			break;
			
		}
		return parent;
	}
	
	protected final Control createLabel(final Composite parent)
			throws NoPropertyDescriptorFoundExeption {
			Label label = null;
			labelRender: if (getVElement().getLabelAlignment() == LabelAlignment.LEFT) {
				if (!getVElement().getDomainModelReference().getIterator().hasNext()) {
					break labelRender;
				}
				final Setting setting = getVElement().getDomainModelReference().getIterator().next();
				if (setting == null) {
					break labelRender;
				}
				
				label = new Label(parent, SWT.NONE);
				label.setData(CUSTOM_VARIANT, "org_eclipse_emf_ecp_control_label"); //$NON-NLS-1$
				label.setBackground(parent.getBackground());

				final String labelText = getControlName();
				label.setText(labelText);
			}
			return label;
		}

	private String getControlName() {
		return "Literals";
	}

	private void createControl(Composite container) {
		EObject eObject= getViewModelContext().getDomainModel();
		
		Composite parent = new Composite(container, SWT.NONE);
		// Create a RowLayout.
		// The first row will contain our buttons, the second one the literals
		GridLayout parentLayout = new GridLayout();
		parentLayout.numColumns = 1;
		parent.setLayout(parentLayout);
		
		// Make parent fill the container grid column
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite buttonRow = new Composite(parent, SWT.RIGHT_TO_LEFT);
		RowLayout buttonLayout = new RowLayout();
		buttonLayout.fill = true;
		buttonRow.setLayout(buttonLayout);
		buttonRow.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		
		// Create the buttons in the ButtonRow
		new IconButton(buttonRow, Icon.ADD);
		new IconButton(buttonRow, Icon.DELETE);
		new IconButton(buttonRow, Icon.UP);
		new IconButton(buttonRow, Icon.DOWN);

		
		// Create the TableViewer and all of its columns
		TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		
		addColumn(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Literal(), new GenericEditingSupport(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Literal()));
		addColumn(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Value(), new GenericEditingSupport(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Value(), new Converter(String.class, int.class) {
			@Override
			public Object convert(Object fromObject) {
				try {
					return Integer.valueOf((String)fromObject);
				} catch (NumberFormatException ex) {
					return null;
				}
			}
		}));
		
		// Drag and Drop
		/*final int dndOperations = DND.DROP_MOVE;
		final Transfer[] transfers = new Transfer[] { LocalTransfer
				.getInstance() };
		viewer.addDragSupport(dndOperations, transfers,
				new ViewerDragAdapter(viewer));
		viewer.addDropSupport(dndOperations, transfers, new ViewerDropAdapter(viewer) {
			
			@Override
			public void drop(DropTargetEvent event) {
				
				int dropLocation = determineLocation(event);
				Object target = determineTarget(event);
				
				EList<EEnumLiteral> literals = (EList<EEnumLiteral>) eObject.eGet(EcorePackage.Literals.EENUM__ELITERALS);
				if(dropLocation == 1) {
					// Move the data in front of the target
					for(int i = 0; i < literals.size(); i++) {
						if(literals.get(i) == target) {
							EEnumLiteral literal = (EEnumLiteral) ((StructuredSelection)event.data).getFirstElement();
							if(target != literal) {
								MoveCommand.create(AdapterFactoryEditingDomain.getEditingDomainFor(eObject), eObject, EcorePackage.Literals.EENUM__ELITERALS, literal, i-1).execute();
							}
						}
					}
				} else if(dropLocation == 2 || dropLocation == 3) {
					// Move the data in after the target
					for(int i = 0; i < literals.size(); i++) {
						if(literals.get(i) == target) {
							MoveCommand.create(AdapterFactoryEditingDomain.getEditingDomainFor(eObject), eObject, EcorePackage.Literals.EENUM__ELITERALS, (EEnumLiteral) ((StructuredSelection)event.data).getFirstElement(), i).execute();						
						}
					}
				}
				viewer.refresh();
				super.drop(event);
			}
			
			@Override
			public boolean validateDrop(Object target, int operation,
					TransferData transferType) {
				return true;
			}
			
			@Override
			public boolean performDrop(Object data) {
				return false;
			}
		});*/
		
		
		viewer.setContentProvider(contentProvider);
		viewer.setInput(EMFProperties.list(EcorePackage.Literals.EENUM__ELITERALS).observe(eObject));
	
	}

	private static class GenericEditingSupport extends EditingSupport {

		private final TableViewer viewer;
		private final CellEditor editor;
		private final EStructuralFeature feature;
		private final Converter converter;
	
		public GenericEditingSupport(TableViewer viewer, EStructuralFeature feature) {
			this(viewer, feature, null);
		}
		
		public GenericEditingSupport(TableViewer viewer, EStructuralFeature feature, Converter converter) {
		    super(viewer);
		    this.viewer = viewer;
		    this.feature = feature;
		    this.editor = new TextCellEditor(viewer.getTable());
		    
		    if(converter != null && (!converter.getFromType().equals(String.class) || !converter.getToType().equals(feature.getEType().getInstanceClass()))) {
		    	throw new IllegalArgumentException("The Converter has to convert from String to "+feature.getEType().getInstanceClass().getSimpleName());
		    }
		    
		    this.converter = converter;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			return ((EObject)element).eGet(feature).toString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			if(converter == null) {
				((EObject)element).eSet(feature, value);
			} else {
				Object convertedValue = converter.convert(value);
				
				// If the Converter can't convert the String to the target value, unset the feature.
				// If the feature can't be unset, ignore the change.
				if(convertedValue == null) {
					if(feature.isUnsettable()) {
						((EObject)element).eUnset(feature);
					}
				} else {
					((EObject)element).eSet(feature, converter.convert(value));
				}
			}
			viewer.update(element, null);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}
	}
}
