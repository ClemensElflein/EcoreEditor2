/**
 */
package treeInput;

import java.util.List;

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
	public List<EObject> getTreeRoots();

	public void setTreeRoots(List<EObject> treeRoots);
} // TreeInput
