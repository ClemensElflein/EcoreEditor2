/**
 */
package treeInput.impl;

import org.eclipse.emf.ecore.EClass;
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

	private Object input = null;

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
	public Object getInput() {
		return input;
	}

	@Override
	public void setInput(Object input) {
		this.input = input;
	}

} // TreeInputImpl
