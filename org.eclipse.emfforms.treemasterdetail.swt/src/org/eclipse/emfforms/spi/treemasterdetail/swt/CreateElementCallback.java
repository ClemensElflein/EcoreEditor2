package org.eclipse.emfforms.spi.treemasterdetail.swt;

public interface CreateElementCallback {
	/**
	 * Gets called before the newElement will be created.
	 * This can be used to modify the element before it will be added to the Model.
	 * 
	 * @param The new element
	 * @return true, if the element shall be created; false to cancel creation
	 */
	public boolean beforeCreateElement(Object newElement);
}
