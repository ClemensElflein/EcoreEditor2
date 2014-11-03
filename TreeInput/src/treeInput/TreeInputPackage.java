/**
 */
package treeInput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see treeInput.TreeInputFactory
 * @model kind="package"
 * @generated
 */
public interface TreeInputPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "treeInput";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.example.org/treeInput";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "treeInput";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TreeInputPackage eINSTANCE = treeInput.impl.TreeInputPackageImpl.init();

	/**
	 * The meta object id for the '{@link treeInput.impl.TreeInputImpl <em>Tree Input</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see treeInput.impl.TreeInputImpl
	 * @see treeInput.impl.TreeInputPackageImpl#getTreeInput()
	 * @generated
	 */
	int TREE_INPUT = 0;

	/**
	 * The number of structural features of the '<em>Tree Input</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_INPUT_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Tree Input</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TREE_INPUT_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link treeInput.TreeInput <em>Tree Input</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tree Input</em>'.
	 * @see treeInput.TreeInput
	 * @generated
	 */
	EClass getTreeInput();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TreeInputFactory getTreeInputFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link treeInput.impl.TreeInputImpl <em>Tree Input</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see treeInput.impl.TreeInputImpl
		 * @see treeInput.impl.TreeInputPackageImpl#getTreeInput()
		 * @generated
		 */
		EClass TREE_INPUT = eINSTANCE.getTreeInput();

	}

} //TreeInputPackage
