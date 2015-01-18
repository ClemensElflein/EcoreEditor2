package org.eclipse.emf.ecp.ecoreeditor.internal;

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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CreateDialog extends Dialog {

	private EObject newObject;
	
	public CreateDialog(Shell parent, EClass toCreate) {
		this(parent, EcoreFactory.eINSTANCE.create(toCreate));
	}
	
	public CreateDialog(Shell parent, EObject createdInstance) {
		super(parent);
		newObject = createdInstance;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create new "+newObject.eClass().getName());
		newShell.setMinimumSize(300, 150);
		newShell.setBackground(new Color(newShell.getDisplay(), 255, 255, 255));
		newShell.setBackgroundMode(SWT.INHERIT_FORCE);
	}
	
	
	

	@Override
	protected Control createDialogArea(Composite parent) {
		
	    GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
	    parent.setLayout(new GridLayout(1, true));
	    parent.setLayoutData(parentData);
		
		ScrolledComposite wrapper = new ScrolledComposite(parent, SWT.V_SCROLL);
		wrapper.setExpandHorizontal(true);
		wrapper.setExpandVertical(true);
		FillLayout wrapperLayout = new FillLayout();
		wrapperLayout.marginHeight = 10;
		wrapperLayout.marginWidth = 10;
		wrapper.setLayout(wrapperLayout);
		wrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
		Composite emfFormsParent = new Composite(wrapper, SWT.NONE);
		wrapper.setContent(emfFormsParent);
		emfFormsParent.setLayout(new GridLayout());
		
		try {
			ECPSWTViewRenderer.INSTANCE.render(emfFormsParent, newObject);
		} catch (ECPRendererException e) {
		}
		
		wrapper.setMinSize(wrapper.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
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