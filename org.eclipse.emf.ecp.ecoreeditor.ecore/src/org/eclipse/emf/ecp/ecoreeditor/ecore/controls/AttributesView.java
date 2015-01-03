package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import java.awt.Color;
import java.util.HashMap;

import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlJFaceViewerSWTRenderer;
import org.eclipse.emf.ecp.view.spi.model.LabelAlignment;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.swt.AbstractSWTRenderer;
import org.eclipse.emf.ecp.view.spi.swt.layout.GridDescriptionFactory;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridCell;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridDescription;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class AttributesView extends AbstractSWTRenderer<VControl> {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.ecp.view.spi.swt.AbstractSWTRenderer#getGridDescription(SWTGridDescription)
	 */
	@Override
	public final SWTGridDescription getGridDescription(SWTGridDescription gridDescription) {
		return GridDescriptionFactory.INSTANCE.createSimpleGrid(1,1, this);
	}
	
	private final void addColumn(TableViewer viewer, EStructuralFeature feature) {
		TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.LEFT);
		tvc.getColumn().setText(feature.getName());
		tvc.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(String.valueOf(((EObject)cell.getElement()).eGet(feature)));
			}
		});
		tvc.getColumn().setWidth(100);

	}

	@Override
	protected Control renderControl(SWTGridCell cell, Composite parent)
			throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		
		TableViewer viewer = new TableViewer(parent);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		viewer.getControl().setLayoutData(gd);
		viewer.getTable().setHeaderVisible(true);
		
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		
		addColumn(viewer, EcorePackage.eINSTANCE.getENamedElement_Name());
		addColumn(viewer, EcorePackage.eINSTANCE.getETypedElement_Ordered());
		addColumn(viewer, EcorePackage.eINSTANCE.getETypedElement_Unique());
		
		
		EClass eClass = (EClass) getViewModelContext().getDomainModel();
		viewer.setContentProvider(contentProvider);
		viewer.setInput(EMFProperties.list(EcorePackage.Literals.ECLASS__EATTRIBUTES).observe(eClass));
		return parent;
	}

	
}
