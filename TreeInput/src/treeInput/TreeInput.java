/**
 */
package treeInput;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Tree Input</b></em>'. <!-- end-user-doc -->
 *
 *
 * @see treeInput.TreeInputPackage#getTreeInput()
 * @model
 * @generated
 */
public interface TreeInput extends EObject {

	public Object getInput();

	public void setInput(Object input);

	public TreeEditCallback getTreeEditCallback();

	public void setTreeEditCallback(TreeEditCallback callback);

	public abstract static class TreeEditCallback {

		private EObject currentSelection = null;

		public final void internalOnSelectionChanged(EObject newSelection) {
			currentSelection = newSelection;
			this.onSelectionChanged(newSelection);
		}

		public final EObject getCurrentSelection() {
			return currentSelection;
		}

		public abstract void onSelectionChanged(EObject newSelection);
	}
} // TreeInput
