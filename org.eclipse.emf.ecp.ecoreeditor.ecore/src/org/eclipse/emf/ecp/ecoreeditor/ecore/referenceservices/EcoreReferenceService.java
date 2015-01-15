package org.eclipse.emf.ecp.ecoreeditor.ecore.referenceservices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.ecp.ecoreeditor.helpers.ResourceSetHelpers;
import org.eclipse.emf.ecp.edit.spi.ReferenceService;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

public class EcoreReferenceService implements ReferenceService {

	private ViewModelContext context;

	@Override
	public void instantiate(ViewModelContext context) {
		this.context = context;
	}

	@Override
	public EObject getNewElementFor(EReference eReference) {
		return null;
	}

	private EObject getExistingSuperTypeFor(EReference eReference) {
		List<EClass> classes = ResourceSetHelpers.findAllOfTypeInResourceSet(
				context.getDomainModel(), EClass.class, false);

		// Substract already present SuperTypes from the List
		// The cast is fine, as we know that the eReference must be manyValued.
		classes.removeAll((List<?>) context.getDomainModel().eGet(eReference));

		return select(
				classes,
				"Select SuperType",
				"Select a SuperType to add to "
						+ ((ENamedElement) context.getDomainModel()).getName());
	}

	private EObject getExistingDataTypeFor(EReference eReference) {
		List<EDataType> dataTypes = ResourceSetHelpers
				.findAllOfTypeInResourceSet(context.getDomainModel(),
						EDataType.class, true);
		return select(dataTypes, "Select Datatype", "Select the Datatype for "
				+ ((ENamedElement) context.getDomainModel()).getName());
	}

	// Let the user select an item from a List using a dialog
	private EObject select(List elements, String title, String message) {
		ListDialog dialog = new ListDialog(Display.getDefault()
				.getActiveShell());
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setInput(elements);

		dialog.setContentProvider(new ArrayContentProvider());

		dialog.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ENamedElement) element).getName();
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
		// Check, if the target is EDataType
		if (context.getDomainModel() instanceof EAttribute
				&& eReference.getEReferenceType() instanceof EClassifier) {
			return getExistingDataTypeFor(eReference);
		}
		if (eReference.equals(EcorePackage.eINSTANCE.getEClass_ESuperTypes())) {
			return getExistingSuperTypeFor(eReference);
		}
		if (eReference.equals(EcorePackage.eINSTANCE.getEReference_EOpposite())) {
			return getExistingOppositeFor(eReference);
		}
		return getExistingGenericType(eReference);
	}

	private EObject getExistingOppositeFor(EReference eReference) {
		EReference editReference = (EReference) context.getDomainModel();

		List<EReference> allReferences = ResourceSetHelpers
				.findAllOfTypeInResourceSet(context.getDomainModel(),
						EReference.class, false);

		// Remove the DomainModel from the List, as it can't be its own opposite
		allReferences.remove(context.getDomainModel());

		// Remove all references which do not reference our target type
		// If the reference type is null, allow all references and set the type
		// on selection later on.
		if (editReference.getEReferenceType() != null) {
			Iterator<EReference> iterator = allReferences.iterator();
			while (iterator.hasNext()) {
				EReference ref = iterator.next();
				if (!editReference.getEReferenceType().equals(
						ref.getEContainingClass()))
					iterator.remove();
			}
		}

		return select(allReferences, "Select EOpposite",
				"Select the opposite EReference");
	}

	private EObject getExistingGenericType(EReference eReference) {
		List<?> classes = ResourceSetHelpers
				.findAllOfTypeInResourceSet(context.getDomainModel(),
						eReference.getEReferenceType(), false);

		return select(classes, "Select " + eReference.getName(), "Select a "
				+ eReference.getEType().getName());

	}

	@Override
	public void dispose() {
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void addModelElement(EObject eObject, EReference eReference) {
		EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(context.getDomainModel());
		// eObject.eSet(EcorePackage.eINSTANCE.getEAttribute_EAttributeType(),
		// eReference);

		// If we set the opposite and the current eReference does not have any
		// type set,
		// we can also set the type of the current eReference.

		if (eReference.equals(EcorePackage.eINSTANCE.getEReference_EOpposite())) {
			
			EReference editReference = (EReference) context.getDomainModel();
			EReference selectedReference = (EReference) eObject; 
			// Set the opposite for the other reference as well
			editingDomain.getCommandStack().execute(SetCommand.create(AdapterFactoryEditingDomain.getEditingDomainFor(selectedReference), selectedReference, EcorePackage.Literals.EREFERENCE__EOPPOSITE, editReference));
			
			if (editReference.getEReferenceType() == null) {
				editingDomain.getCommandStack().execute(
						SetCommand.create(editingDomain, editReference, EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, selectedReference.getEContainingClass())
				);
			}
			editingDomain.getCommandStack().execute(
					SetCommand.create(editingDomain, editReference, EcorePackage.Literals.EREFERENCE__EOPPOSITE, eObject)
			);
			
			
			return;
		}

		if (!eReference.isMany()) {
			context.getDomainModel().eSet(eReference, eObject);
		} else {
			@SuppressWarnings("unchecked")
			// This cast is OK as we know, that the eReference is many-valued.
			List<Object> objects = (List<Object>) context.getDomainModel()
					.eGet(eReference);
			List<Object> newValues = new ArrayList<Object>(objects);
			newValues.add(eObject);
			context.getDomainModel().eSet(eReference, newValues);
		}
	}

	@Override
	public void openInNewContext(EObject eObject) {
		
	}
}
