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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.eclipse.emf.ecp.view.spi.swt.AbstractSWTRenderer;
import org.eclipse.emf.ecp.view.spi.swt.layout.GridDescriptionFactory;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridCell;
import org.eclipse.emf.ecp.view.spi.swt.layout.SWTGridDescription;
import org.eclipse.emf.ecp.view.treemasterdetail.model.VTreeMasterDetail;
import org.eclipse.emfforms.internal.treemasterdetail.swt.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * This class wraps the TreeMasterDetailSWTRenderer in an EMF Forms renderer.
 */
@SuppressWarnings("restriction")
public class TreeMasterDetailRenderer extends AbstractSWTRenderer<VTreeMasterDetail> {

	private SWTGridDescription rendererGridDescription;

	@Override
	public SWTGridDescription getGridDescription(
		SWTGridDescription gridDescription) {
		if (rendererGridDescription == null) {
			rendererGridDescription = GridDescriptionFactory.INSTANCE.createSimpleGrid(1, 1, this);
		}
		return rendererGridDescription;
	}

	@Override
	protected Control renderControl(SWTGridCell cell, Composite parent)
		throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		return new TreeMasterDetailSWTRenderer(parent, SWT.NONE, getViewModelContext().getDomainModel().eResource(),
			readMasterDetailActions());
	}

	/**
	 * Returns a list of all {@link MasterDetailAction MasterDetailActions} which shall be displayed in the context menu
	 * of the master treeviewer.
	 *
	 * @return the actions
	 */
	protected List<MasterDetailAction> readMasterDetailActions() {
		final List<MasterDetailAction> commands = new ArrayList<MasterDetailAction>();
		final IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
		if (extensionRegistry == null) {
			return commands;
		}

		final IConfigurationElement[] controls = extensionRegistry
			.getConfigurationElementsFor("org.eclipse.emfforms.treemasterdetail.swt.masterDetailActions"); //$NON-NLS-1$
		for (final IConfigurationElement e : controls) {
			try {
				final String location = e.getAttribute("location"); //$NON-NLS-1$
				if (!location.equals("menu")) { //$NON-NLS-1$
					continue;
				}
				final String label = e.getAttribute("label"); //$NON-NLS-1$
				final String imagePath = e.getAttribute("imagePath"); //$NON-NLS-1$
				final MasterDetailAction command = (MasterDetailAction) e.createExecutableExtension("command"); //$NON-NLS-1$
				command.setLabel(label);
				command.setImagePath(imagePath);

				commands.add(command);

			} catch (final CoreException ex) {
				Activator.getDefault().getLog().log(
					new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						ex.getMessage(), ex));
			}
		}

		return commands;

	}

}
