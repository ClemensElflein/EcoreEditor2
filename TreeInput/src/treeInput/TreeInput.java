/**
 */
package treeInput;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tree Input</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link treeInput.TreeInput#getTreeRoots <em>Tree Roots</em>}</li>
 * </ul>
 * </p>
 *
 * @see treeInput.TreeInputPackage#getTreeInput()
 * @model
 * @generated
 */
public interface TreeInput extends EObject {
	/**
	 * Returns the value of the '<em><b>Tree Roots</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tree Roots</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tree Roots</em>' reference list.
	 * @see treeInput.TreeInputPackage#getTreeInput_TreeRoots()
	 * @model
	 * @generated
	 */
	EList<EObject> getTreeRoots();

} // TreeInput
