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
package org.eclipse.emfforms.internal.editor.genmodel.toolbaractions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.util.GenModelUtil;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfforms.spi.treemasterdetail.swt.IToolbarAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.osgi.framework.FrameworkUtil;

/**
 *
 * The ToolbarAction allowing the User to generate Java code for the currently visible Genmodel.
 *
 */
public class GenerateJavaCodeAction implements IToolbarAction {

	/**
	 *
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emfforms.spi.treemasterdetail.swt.IToolbarAction#getAction(java.lang.Object)
	 */
	@Override
	public Action getAction(Object currentObject) {
		return new CreateJavaCodeAction(currentObject);
	}

	@Override
	public boolean canExecute(Object object) {
		// We can't execute our Action on Objects other than ResourceSet
		if (!(object instanceof ResourceSet)) {
			return false;
		}
		// Check, if the ResourceSet contains a Genmodel. If so, we also can't execute our Action
		final ResourceSet resourceSet = (ResourceSet) object;
		for (final Resource r : resourceSet.getResources()) {
			if (r.getContents().size() > 0 && r.getContents().get(0) instanceof GenModel) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ToolbarAction to generate Java Code. It also provides the DropDown menu to create
	 * each type separately (Model, Edit, Editor, Tests).
	 */
	private static class CreateJavaCodeAction extends Action {
		private final Object[] types;
		private final Object currentObject;

		public CreateJavaCodeAction(String text, Object[] types, Object currentObject) {
			super(text);
			this.types = types;
			this.currentObject = currentObject;
		}

		public CreateJavaCodeAction(Object currentObject) {
			super("Generate All", SWT.DROP_DOWN);

			this.currentObject = currentObject;

			types = new Object[] {
				GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
				GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE,
				GenBaseGeneratorAdapter.EDITOR_PROJECT_TYPE,
				GenBaseGeneratorAdapter.TESTS_PROJECT_TYPE
			};

			setMenuCreator(new GenmodelDropdownCreator());

			setImageDescriptor(ImageDescriptor.createFromURL(FrameworkUtil.getBundle(
				this.getClass()).getResource("icons/page_white_cup.png")));
		}

		/**
		 * {@inheritDoc}
		 *
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			final IRunnableWithProgress generateCodeRunnable = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Generating Code", IProgressMonitor.UNKNOWN);
					final ResourceSet resourceSet = (ResourceSet) currentObject;
					final GenModel genmodel = (GenModel) resourceSet.getResources().get(0).getContents().get(0);

					genmodel.reconcile();
					genmodel.setCanGenerate(true);

					final Generator generator = GenModelUtil.createGenerator(genmodel);

					for (final Object type : types) {
						generator.generate(genmodel, type,
							new BasicMonitor.EclipseSubProgress(monitor, 1));
					}
				}
			};

			try {
				new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false, generateCodeRunnable);
			} catch (final InvocationTargetException e) {
				e.printStackTrace();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * IMenuCreator to create each type separately.
		 */
		private class GenmodelDropdownCreator implements IMenuCreator {
			private Menu dropDown;

			/**
			 * {@inheritDoc}
			 *
			 * @see org.eclipse.jface.action.IMenuCreator#dispose()
			 */
			@Override
			public void dispose() {
				if (dropDown != null) {
					dropDown.dispose();
				}
			}

			/**
			 * {@inheritDoc}
			 *
			 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
			 */
			@Override
			public Menu getMenu(Control parent) {
				dispose();

				dropDown = new Menu(parent);
				final Action generateModel = new CreateJavaCodeAction("Generate Model + Edit",
					new Object[] {
						GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
						GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE
					}, currentObject);

				final Action generateEditor = new CreateJavaCodeAction("Generate Editor",
					new Object[] {
						GenBaseGeneratorAdapter.EDITOR_PROJECT_TYPE
					}, currentObject);

				final Action generateTests = new CreateJavaCodeAction("Generate Tests",
					new Object[] {
						GenBaseGeneratorAdapter.TESTS_PROJECT_TYPE
					}, currentObject);

				new ActionContributionItem(generateModel).fill(dropDown, 0);
				new ActionContributionItem(generateEditor).fill(dropDown, 1);
				new ActionContributionItem(generateTests).fill(dropDown, 2);

				return dropDown;
			}

			/**
			 * {@inheritDoc}
			 *
			 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
			 */
			@Override
			public Menu getMenu(Menu parent) {
				return null;
			}

		}
	}
}
