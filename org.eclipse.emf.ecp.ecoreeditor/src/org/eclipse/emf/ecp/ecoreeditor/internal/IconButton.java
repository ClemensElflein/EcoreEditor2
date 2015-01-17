package org.eclipse.emf.ecp.ecoreeditor.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class IconButton extends Button {

	public enum Icon {
		ADD("\uf055"),
		DELETE("\uf056"),
		UP("\uf0aa"),
		DOWN("\uf0ab");
		
		private final String text;
		
		private Icon(String text) {
			this.text = text;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	public IconButton(Composite parent, Icon icon) {
		super(parent, SWT.PUSH);
		setFont(new Font(Display.getCurrent(), "FontAwesome", 13, SWT.NORMAL));
		setText(icon.toString());
	}
	
	@Override
	protected void checkSubclass() {
		//NOOP; We really want a subclass here
	}
}
