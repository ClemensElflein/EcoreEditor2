/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Clemens Elflein - initial implementation
 ******************************************************************************/
package org.eclipse.emfforms.spi.treemasterdetail.swt;

/**
 * This interface allows intercepting the TreeMasterDetailSWTRenderer's create function.
 * The callback can modify the newly created element as well as cancel the action.
 */
public interface CreateElementCallback {
	/**
	 * Gets called before the newElement will be created.
	 * This can be used to modify the element before it will be added to the Model.
	 *
	 * @param newElement The new element
	 * @return true, if the element shall be created; false to cancel creation
	 */
	boolean beforeCreateElement(Object newElement);
}
