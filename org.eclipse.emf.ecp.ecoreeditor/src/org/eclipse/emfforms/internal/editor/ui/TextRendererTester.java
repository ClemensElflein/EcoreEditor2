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
package org.eclipse.emfforms.internal.editor.ui;

import org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester;

/**
 * Tester for the TextRenderer.
 */
public class TextRendererTester extends SimpleControlRendererTester {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#isSingleValue()
	 */
	@Override
	protected boolean isSingleValue() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#getPriority()
	 */
	@Override
	protected int getPriority() {
		return 10;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecp.view.model.common.SimpleControlRendererTester#getSupportedClassType()
	 */
	@Override
	protected Class<?> getSupportedClassType() {
		return String.class;
	}
}
