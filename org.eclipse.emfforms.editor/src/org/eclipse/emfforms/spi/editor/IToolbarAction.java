/*******************************************************************************
 * Copyright (c) 2011-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Clemens Elflein - initial API and implementation
 ******************************************************************************/

package org.eclipse.emfforms.spi.editor;

import org.eclipse.jface.action.Action;

/**
 * The Interface IToolbarAction allows the creation of ToolBar actions for the Ecore Editor.
 */
public interface IToolbarAction {

	/**
	 * Returns the action to add to the toolbar.
	 *
	 * @param currentObject the currently edited object of the editor
	 * @return the action
	 */
	Action getAction(Object currentObject);

	/**
	 * @param object the currently edited object of the editor
	 * @return true, if the Action can be executed for the provided input
	 */
	boolean canExecute(Object object);
}
