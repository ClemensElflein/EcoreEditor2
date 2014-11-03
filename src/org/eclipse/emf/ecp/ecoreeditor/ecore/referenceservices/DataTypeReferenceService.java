package org.eclipse.emf.ecp.ecoreeditor.ecore.referenceservices;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.edit.spi.ReferenceService;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

public class DataTypeReferenceService implements ReferenceService {

	private ViewModelContext context;

	@Override
	public void instantiate(ViewModelContext context) {
		this.context = context;
	}

	@Override
	public EObject getNewElementFor(EReference eReference) {
		// Check, if a EDataType can be referenced
		if (!(context.getDomainModel() instanceof EAttribute)
				|| !(eReference.getEReferenceType() instanceof EClassifier))
			return null;

		List<EDataType> dataTypes = ResourceSetHelpers
				.findAllOfTypeInResourceSet(context.getDomainModel(),
						EDataType.class, true);

		ListDialog dialog = new ListDialog(Display.getDefault()
				.getActiveShell());
		dialog.setTitle("Select Datatype");
		dialog.setMessage("Select the Datatype for "
				+ context.getDomainModel().eClass().getName());
		dialog.setInput(dataTypes);

		dialog.setContentProvider(new ArrayContentProvider());

		dialog.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EDataType) element).getName();
			}
		});
		int result = dialog.open();
		if (result == Window.OK) {
			return (EObject) dialog.getResult()[0];
		}
		return null;
	}

	@Override
	public EObject getExistingElementFor(EReference eReference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addModelElement(EObject eObject, EReference eReference) {
		// eObject.eSet(EcorePackage.eINSTANCE.getEAttribute_EAttributeType(),
		// eReference);
		context.getDomainModel().eSet(eReference, eObject);
	}

	@Override
	public void openInNewContext(EObject eObject) {
		throw new UnsupportedOperationException();
	}
}
