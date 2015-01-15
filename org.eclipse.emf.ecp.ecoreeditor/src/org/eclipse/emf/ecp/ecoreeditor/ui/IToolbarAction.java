package org.eclipse.emf.ecp.ecoreeditor.ui;


public interface IToolbarAction {
	public String getLabel();
	public String getImagePath();
	public void execute(Object currentObject);
	public boolean canExecute(Object object);
}
