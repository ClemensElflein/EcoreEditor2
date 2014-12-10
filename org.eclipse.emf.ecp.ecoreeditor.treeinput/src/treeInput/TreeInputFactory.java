/**
 */
package treeInput;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see treeInput.TreeInputPackage
 * @generated
 */
public interface TreeInputFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TreeInputFactory eINSTANCE = treeInput.impl.TreeInputFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Tree Input</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Tree Input</em>'.
	 * @generated
	 */
	TreeInput createTreeInput();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	TreeInputPackage getTreeInputPackage();

} //TreeInputFactory
