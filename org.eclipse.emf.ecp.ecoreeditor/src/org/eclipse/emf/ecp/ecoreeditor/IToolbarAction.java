/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor;


/**
 * The Interface IToolbarAction allows the creation of ToolBar actions for the Ecore Editor
 */
public interface IToolbarAction {
	
	/**
	 * Gets the label of the action.
	 * It will be used as mouseover text
	 *
	 * @return the label
	 */
	public String getLabel();
	
	/**
	 * Gets the image path of the action
	 * It will be used as icon in the toolbar
	 *
	 * @return the image path
	 */
	public String getImagePath();
	
	/**
	 * Execute the action
	 *
	 * @param currentObject the currently edited object of the editor
	 */
	public void execute(Object currentObject);
	
	/**
	 * @param object the currently edited object of the editor
	 * @return true, if the Action can be executed for the provided input
	 */
	public boolean canExecute(Object object);
}
