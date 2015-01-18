/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecp.common.ChildrenDescriptorCollector;
import org.eclipse.emf.ecp.ecoreeditor.internal.CreateDialog;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The Class CreateNewChildDialog.
 * It shows the small dialog asking the user to select the type of the newly created element
 */
public class CreateNewChildDialog extends Dialog {

	/** The title of the dialog. */
	private String title;
	
	/** The selection provider. It is used to select the created elements afterwards. */
	private ISelectionProvider selectionProvider;
	
	/** The parent. It is used to obtain which children can be created. */
	private EObject parent;
	
	/**
	 * Instantiates a new creates the new child dialog.
	 *
	 * @param parentShell the parent shell
	 * @param title the title
	 * @param parent the parent
	 * @param selectionProvider the selection provider
	 */
	public CreateNewChildDialog(Shell parentShell, String title, EObject parent, ISelectionProvider selectionProvider) {
		super(parentShell);
		this.title = title;
		this.parent = parent;
		this.selectionProvider = selectionProvider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#setShellStyle(int)
	 */
	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(SWT.TITLE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
	 */
	@Override
	protected Button createButton(Composite parent, int id,
			String label, boolean defaultButton) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.marginHeight = 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parentComposite) {
		final ChildrenDescriptorCollector childrenDescriptorCollector = new ChildrenDescriptorCollector();
		final EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(parent);
		
		
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Dialog currentDialog = this;
		final List<Action> actions = getNewChildActions(
				childrenDescriptorCollector.getDescriptors(parent),
				editingDomain, parent);

		TableViewer list = new TableViewer(parentComposite);
		list.setContentProvider(new ArrayContentProvider());
		list.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Action action = (Action) element;
				StringBuilder builder = new StringBuilder(action
						.getText());
				if (action.getAccelerator() > 0) {
					builder.append(" [");
					builder.append(Character.toUpperCase((char) action
							.getAccelerator()));
					builder.append("]");
				}
				return builder.toString();
			}

			@Override
			public Image getImage(Object element) {
				return ((Action) element).getImageDescriptor()
						.createImage();
			}
		});
		list.setInput(actions.toArray());
		list.addOpenListener(new IOpenListener() {

			@Override
			public void open(OpenEvent event) {
				Action action = (Action) ((StructuredSelection) event
						.getSelection()).getFirstElement();
				action.run();
				currentDialog.close();
			}
		});
		list.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// NOP
			}

			@Override
			public void keyPressed(KeyEvent e) {
				for (Action a : actions) {
					if (a.getAccelerator() == e.keyCode) {
						a.run();
						currentDialog.close();
						break;
					}
				}
			}
		});
		return parentComposite;
	}
	
	/**
	 * Creates all new child actions.
	 *
	 * @param descriptors the descriptors
	 * @param domain the domain
	 * @param eObject the e object
	 * @return the list
	 */
	private List<Action> getNewChildActions(Collection<?> descriptors,
			final EditingDomain domain, final EObject eObject) {

		List<Action> result = new ArrayList<Action>();

		for (final Object descriptor : descriptors) {

			final CommandParameter cp = (CommandParameter) descriptor;
			if (!CommandParameter.class.isInstance(descriptor)) {
				continue;
			}
			if (cp.getEReference() == null) {
				continue;
			}
			if (!cp.getEReference().isMany()
					&& eObject.eIsSet(cp.getEStructuralFeature())) {
				continue;
			} else if (cp.getEReference().isMany()
					&& cp.getEReference().getUpperBound() != -1
					&& cp.getEReference().getUpperBound() <= ((List<?>) eObject
							.eGet(cp.getEReference())).size()) {
				continue;
			}

			result.add(new CreateChildActionWithAccelerator(domain,
					new StructuredSelection(eObject), descriptor) {
				@Override
				public void run() {
					super.run();

					final EReference reference = ((CommandParameter) descriptor)
							.getEReference();

					EObject newElement = cp.getEValue();

					int result = new CreateDialog(Display.getCurrent().getActiveShell(), newElement).open();
					
					if(result == Window.OK) {
						domain.getCommandStack().execute(
								AddCommand.create(domain, eObject, reference,
										newElement));
	
						// Select the newly added element, if possible
						selectionProvider.setSelection(new StructuredSelection(newElement));
					}
				}
			});
		}
		return result;
	}

}
