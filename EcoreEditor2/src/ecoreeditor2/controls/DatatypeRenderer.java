package ecoreeditor2.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlSWTRenderer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class DatatypeRenderer extends SimpleControlSWTRenderer {

	@Override
	protected String getUnsetText() {
		return "TODO >> UNSET";
	}

	@Override
	protected Control createControl(Composite parent) {
		ListViewer listViewer = new ListViewer(parent);
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setInput(getDataTypes());
		getViewModelContext().getDomainModel().eSet(EcorePackage.eINSTANCE.getETypedElement_EType(), 
				EcorePackage.eINSTANCE.getEBoolean());
		
		parent.pack();
		return parent;
	}
	
	private List<EDataType> getDataTypes() {
		List<EDataType> dataTypes = new ArrayList<EDataType>();
		
		// Find all EDatatypes in the root of the DomainModel
		EObject root = getViewModelContext().getDomainModel();
		while(root.eContainer() != null)
			root = root.eContainer();
		
		TreeIterator<EObject> contents = root.eAllContents();
		while(contents.hasNext()){
			EObject c = contents.next();
			if(c instanceof EDataType) {
				dataTypes.add((EDataType) c);
			}
		}
		
		// Find all EDatatypes in the ECore-Package.
		List<EClassifier> classifiers = EcorePackage.eINSTANCE.getEClassifiers();
		for(EClassifier c : classifiers) {
			if(c instanceof EDataType) {
				dataTypes.add((EDataType) c);
			}
		}
		return dataTypes;
	}

}
