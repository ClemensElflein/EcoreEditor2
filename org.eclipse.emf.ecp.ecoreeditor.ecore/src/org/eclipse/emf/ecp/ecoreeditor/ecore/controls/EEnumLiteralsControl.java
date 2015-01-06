package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import java.util.HashMap;
import java.util.List;

import javax.swing.text.Style;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.ecoreeditor.CreateDialog;
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
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.MoveCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
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
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

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
	
	private final void addColumn(TableViewer viewer, String columnName, EStructuralFeature feature, EditingSupport editingSupport) {
		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.LEFT);
		tvc.getColumn().setText(columnName);
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
		EEnum eObject= (EEnum) getViewModelContext().getDomainModel();
		EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
		
		
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
		Button btnAdd = new IconButton(buttonRow, Icon.ADD);
		Button btnDelete = new IconButton(buttonRow, Icon.DELETE);
		Button btnUp = new IconButton(buttonRow, Icon.UP);
		Button btnDown = new IconButton(buttonRow, Icon.DOWN);

		
		// Create the TableViewer and all of its columns
		TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		
		addColumn(viewer, "Name", EcorePackage.eINSTANCE.getENamedElement_Name(), new GenericEditingSupport(viewer, EcorePackage.eINSTANCE.getENamedElement_Name()));
		addColumn(viewer, "Literal", EcorePackage.eINSTANCE.getEEnumLiteral_Literal(), new GenericEditingSupport(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Literal()));
		addColumn(viewer, "Value", EcorePackage.eINSTANCE.getEEnumLiteral_Value(), new GenericEditingSupport(viewer, EcorePackage.eINSTANCE.getEEnumLiteral_Value(), new Converter(String.class, int.class) {
			@Override
			public Object convert(Object fromObject) {
				try {
					return Integer.valueOf((String)fromObject);
				} catch (NumberFormatException ex) {
					return null;
				}
			}
		}));
		
		
		viewer.setContentProvider(contentProvider);
		viewer.setInput(EMFProperties.list(EcorePackage.Literals.EENUM__ELITERALS).observe(eObject));
		
		
		// Handle the button clicks and perform events accordingly
		Listener buttonListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.widget == btnUp) {
					// Get the currently selected Item and move it up
					EEnumLiteral selectedItem = (EEnumLiteral) ((StructuredSelection)viewer.getSelection()).getFirstElement();
					int currentPosition = eObject.getELiterals().indexOf(selectedItem);
					if(currentPosition > 0) {
						Command moveCommand = new MoveCommand(editingDomain, eObject.getELiterals(), currentPosition, currentPosition - 1);
						if(moveCommand.canExecute()) {
							editingDomain.getCommandStack().execute(moveCommand);
						}
					}
				} else if(event.widget == btnDown) {
					// Get the currently selected Item and move it down
					EEnumLiteral selectedItem = (EEnumLiteral) ((StructuredSelection)viewer.getSelection()).getFirstElement();
					int currentPosition = eObject.getELiterals().indexOf(selectedItem);
					if(currentPosition < eObject.getELiterals().size() - 1) {
						Command moveCommand = new MoveCommand(editingDomain, eObject.getELiterals(), currentPosition, currentPosition + 1);
						if(moveCommand.canExecute()) {
							editingDomain.getCommandStack().execute(moveCommand);
						}
					}
				} else if(event.widget == btnDelete) {
					// We support removing multiple elements, just enable multiselect in the TableViewer
					// but this would lead to confusion when moving elements
					List<?> selectedItems = ((StructuredSelection)viewer.getSelection()).toList();
					Command removeCommand = new RemoveCommand(editingDomain, eObject.getELiterals(), selectedItems);
					if(removeCommand.canExecute()) {
						editingDomain.getCommandStack().execute(removeCommand);
					}
				} else if(event.widget == btnAdd) {
					CreateDialog dialog = new CreateDialog(Display.getCurrent().getActiveShell(), EcorePackage.Literals.EENUM_LITERAL);
					int result = dialog.open();
					if(result == Window.OK) {
						Command addCommand = AddCommand.create(editingDomain, eObject, EcorePackage.Literals.EENUM__ELITERALS, dialog.getCreatedInstance());
						editingDomain.getCommandStack().execute(addCommand);
					}
				}
			}
		};
		
		btnUp.addListener(SWT.Selection, buttonListener);
		btnDown.addListener(SWT.Selection, buttonListener);
		btnDelete.addListener(SWT.Selection, buttonListener);
		btnAdd.addListener(SWT.Selection, buttonListener);
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
