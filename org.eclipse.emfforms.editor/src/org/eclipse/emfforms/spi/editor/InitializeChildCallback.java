/*******************************************************************************
 * Copyright (c) 2011-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * cleme_000 - initial API and implementation
 ******************************************************************************/
package org.eclipse.emfforms.spi.editor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;
import org.eclipse.emfforms.internal.editor.ui.CreateDialog;
import org.eclipse.emfforms.spi.treemasterdetail.swt.CreateElementCallback;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * This CreateElementCallback display a CreateDialog to the user that allows setting initial Values for the newly
 * created element.
 */
public class InitializeChildCallback implements CreateElementCallback {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emfforms.spi.treemasterdetail.swt.CreateElementCallback#beforeCreateElement(java.lang.Object)
	 */
	@Override
	public boolean beforeCreateElement(Object newElement) {
		// We won't disturb creation of non EObjects
		if (!(newElement instanceof EObject)) {
			return true;
		}

		final VView view = ViewProviderHelper.getView((EObject) newElement, null);
		final boolean isViewEmpty = view.getChildren().isEmpty();

		int result = Window.OK;

		if (!isViewEmpty) {
			final CreateDialog diag = new CreateDialog(Display.getCurrent().getActiveShell(), (EObject) newElement);
			result = diag.open();
		}

		return result == Window.OK;
	}

}
