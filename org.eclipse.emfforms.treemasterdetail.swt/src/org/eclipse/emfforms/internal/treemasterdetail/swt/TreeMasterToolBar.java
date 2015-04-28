package org.eclipse.emfforms.internal.treemasterdetail.swt;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

public class TreeMasterToolBar extends Composite {

	private ToolBarManager toolBarManager;
	
	public TreeMasterToolBar(Composite parent, int style, String titleText, List<Action> toolbarActions) {
		super(parent, style);

		this.setBackground(new Color(parent.getDisplay(), 207, 222, 238));

		final FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		this.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(this);

		// Create the Icon on the Left
		final Label titleImage = new Label(this, SWT.FILL);
		final ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(Activator.getDefault().getBundle()
			.getResource("icons/view.png"));
		titleImage.setImage(new Image(parent.getDisplay(), imageDescriptor.getImageData()));
		
		final FormData titleImageData = new FormData();
		final int imageOffset = -titleImage.computeSize(SWT.DEFAULT, SWT.DEFAULT).y / 2;
		titleImageData.top = new FormAttachment(50, imageOffset);
		titleImageData.left = new FormAttachment(0, 10);
		titleImage.setLayoutData(titleImageData);

		// Create the label for the Title Text
		final Label title = new Label(this, SWT.WRAP);
		final FontDescriptor boldDescriptor = FontDescriptor.createFrom(title.getFont()).setHeight(12)
			.setStyle(SWT.BOLD);
		final Font boldFont = boldDescriptor.createFont(title.getDisplay());
		title.setForeground(new Color(parent.getDisplay(), 25, 76, 127));
		title.setFont(boldFont);
		title.setText(titleText);
		final FormData titleData = new FormData();
		title.setLayoutData(titleData);
		titleData.left = new FormAttachment(titleImage, 5, SWT.DEFAULT);
		
		
		// Create the toolbar and add it to the header
		final ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
		final FormData formData = new FormData();
		formData.right = new FormAttachment(100, 0);
		toolBar.setLayoutData(formData);
		toolBar.layout();
		toolBarManager = new ToolBarManager(toolBar);

		// Add the provided actions
		if(toolbarActions != null) {
			for(Action a : toolbarActions) {
				toolBarManager.add(a);
			}
		}
		
		toolBarManager.update(true);
		this.layout();
	}
	

}
