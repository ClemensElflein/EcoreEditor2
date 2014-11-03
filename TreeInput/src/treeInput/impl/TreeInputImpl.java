/**
 */
package treeInput.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import treeInput.TreeInput;
import treeInput.TreeInputPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Tree Input</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class TreeInputImpl extends MinimalEObjectImpl.Container implements
		TreeInput {
	private List<EObject> roots;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeInputImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TreeInputPackage.Literals.TREE_INPUT;
	}

	@Override
	public List<EObject> getTreeRoots() {
		return roots;
	}

	@Override
	public void setTreeRoots(List<EObject> treeRoots) {
		this.roots = treeRoots;
	}

} // TreeInputImpl
