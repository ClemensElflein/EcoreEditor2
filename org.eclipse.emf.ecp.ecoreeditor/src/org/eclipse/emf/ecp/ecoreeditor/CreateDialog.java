package org.eclipse.emf.ecp.ecoreeditor;

import java.util.Collection;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.ecore.util.FeatureMapUtil.Validator;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CreateDialog extends Dialog {

	private EObject newObject;
	
	public CreateDialog(Shell parent, EClass toCreate) {
		this(parent, EcoreFactory.eINSTANCE.create(toCreate));
	}
	
	public CreateDialog(Shell parent, EObject createdInstance) {
		super(parent);
		newObject = createdInstance;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		try {
			ECPSWTViewRenderer.INSTANCE.render(parent, newObject);
		} catch (ECPRendererException e) {
		}
		return parent;
	}
	
	@Override
	protected void okPressed() {
		Diagnostic result = Diagnostician.INSTANCE.validate(newObject);
		if(result.getSeverity() == Diagnostic.OK) {
			super.okPressed();	
		} else {
			MessageDialog.open(MessageDialog.ERROR, getParentShell(), "Error", "Please verify, that all fields are valid!", SWT.NONE);
		}
	}

	public EObject getCreatedInstance() {
		return newObject;
	}
}
