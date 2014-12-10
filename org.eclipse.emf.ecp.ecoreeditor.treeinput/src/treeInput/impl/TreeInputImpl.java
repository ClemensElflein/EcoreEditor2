/**
 */
package treeInput.impl;

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

	private Object input = null;
	private TreeInput.TreeEditCallback treeEditCallback = null;
	private TreeController treeController = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TreeInputImpl() {
		super();
		setTreeEditCallback(new TreeEditCallback() {

			@Override
			public void onSelectionChanged(EObject newSelection) {
				// nop
			}
		});
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

	@Override
	public TreeEditCallback getTreeEditCallback() {
		return this.treeEditCallback;
	}

	@Override
	public void setTreeEditCallback(TreeEditCallback callback) {
		this.treeEditCallback = callback;
	}

	@Override
	public TreeController getController() {
		return this.treeController;
	}

	@Override
	public void setTreeController(TreeController controller) {
		this.treeController = controller;
	}

} // TreeInputImpl
