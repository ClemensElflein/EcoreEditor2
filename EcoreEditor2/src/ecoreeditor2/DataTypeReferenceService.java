package ecoreeditor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecp.common.EMFUtils;
import org.eclipse.emf.ecp.edit.spi.ReferenceService;
import org.eclipse.emf.ecp.internal.edit.ECPControlHelper;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.DefaultReferenceService;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.emf.ecp.view.internal.swt.Activator;
import org.eclipse.emf.ecp.view.internal.swt.Messages;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain.EditingDomainProvider;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import ecoreeditor2.helpers.ResourceSetHelpers;

public class DataTypeReferenceService implements ReferenceService {

	private ViewModelContext context;
	
	@Override
	public void instantiate(ViewModelContext context) {
		this.context = context;
	}

	@Override
	public EObject getNewElementFor(EReference eReference) {
		// Check, if a EDataType can be referenced
		if(!(context.getDomainModel() instanceof EAttribute) || !(eReference.getEReferenceType() instanceof EClassifier))
			return null;
		
		List<EDataType> dataTypes = ResourceSetHelpers.findAllOfTypeInResourceSet(context.getDomainModel(), EDataType.class, true);
		
		ListDialog dialog = new ListDialog(Display.getDefault().getActiveShell());
		dialog.setTitle("Select Datatype");
		dialog.setMessage("Select the Datatype for "+context.getDomainModel().eClass().getName());
		dialog.setInput(dataTypes);

		dialog.setContentProvider(new ArrayContentProvider());

		dialog.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EDataType)element).getName();
			}
		});
		int result = dialog.open();
		if(result == Window.OK) {
			return (EObject)dialog.getResult()[0];
		}
		return null;
	}

	@Override
	public EObject getExistingElementFor(EReference eReference) {
		// TODO Auto-generated method stub
		Log.e("getExitingElementFor");
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
		//eObject.eSet(EcorePackage.eINSTANCE.getEAttribute_EAttributeType(), eReference);
		Log.e(eObject.getClass().getSimpleName());
		Log.e(eReference.getClass().getSimpleName());
		context.getDomainModel().eSet(eReference, eObject);
	}

	@Override
	public void openInNewContext(EObject eObject) {
		throw new UnsupportedOperationException();
	}
}