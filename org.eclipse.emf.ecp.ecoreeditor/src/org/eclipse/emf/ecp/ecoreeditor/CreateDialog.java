package org.eclipse.emf.ecp.ecoreeditor;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.emf.ecp.ui.view.swt.ECPSWTViewRenderer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
		
		Composite wrapper = new Composite(parent, SWT.NONE);
		FillLayout wrapperLayout = new FillLayout();
		wrapperLayout.marginHeight = 10;
		wrapperLayout.marginWidth = 10;
		wrapper.setLayout(wrapperLayout);
		
		Composite emfFormsParent = new Composite(wrapper, SWT.NONE);
		emfFormsParent.setLayout(new GridLayout());
		
		try {
			ECPSWTViewRenderer.INSTANCE.render(emfFormsParent, newObject);
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
			// Get the error count and create an appropriate Error message:
			int errorCount = result.getChildren().size();
			String errorMessage = String.format("%d %s occured while analyzing your inputs. The following errors were found:\r\n", errorCount, errorCount == 1 ? "error" : "errors");
			int messageCount = 1;
			for(Diagnostic d : result.getChildren()) {
				errorMessage += String.format("\r\n%d. %s", messageCount++,d.getMessage());
			}
			
			MessageDialog.open(MessageDialog.ERROR, getParentShell(), "Error", errorMessage, SWT.NONE);
		}
	}

	public EObject getCreatedInstance() {
		return newObject;
	}
}
