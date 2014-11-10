/**
 */
package treeInput.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import treeInput.TreeInput;
import treeInput.TreeInputPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tree Input</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link treeInput.impl.TreeInputImpl#getTreeRoots <em>Tree Roots</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TreeInputImpl extends MinimalEObjectImpl.Container implements TreeInput {
	/**
	 * The cached value of the '{@link #getTreeRoots() <em>Tree Roots</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTreeRoots()
	 * @generated
	 * @ordered
	 */
	protected EList<EObject> treeRoots;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TreeInputImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return TreeInputPackage.Literals.TREE_INPUT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EObject> getTreeRoots() {
		if (treeRoots == null) {
			treeRoots = new EObjectResolvingEList<EObject>(EObject.class, this, TreeInputPackage.TREE_INPUT__TREE_ROOTS);
		}
		return treeRoots;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case TreeInputPackage.TREE_INPUT__TREE_ROOTS:
				return getTreeRoots();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case TreeInputPackage.TREE_INPUT__TREE_ROOTS:
				getTreeRoots().clear();
				getTreeRoots().addAll((Collection<? extends EObject>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case TreeInputPackage.TREE_INPUT__TREE_ROOTS:
				getTreeRoots().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case TreeInputPackage.TREE_INPUT__TREE_ROOTS:
				return treeRoots != null && !treeRoots.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //TreeInputImpl
