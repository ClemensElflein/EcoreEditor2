/**
 */
package treeInput.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import treeInput.TreeInput;
import treeInput.TreeInputFactory;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Tree Input</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class TreeInputTest extends TestCase {

	/**
	 * The fixture for this Tree Input test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TreeInput fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TreeInputTest.class);
	}

	/**
	 * Constructs a new Tree Input test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TreeInputTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Tree Input test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(TreeInput fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Tree Input test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TreeInput getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(TreeInputFactory.eINSTANCE.createTreeInput());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

} //TreeInputTest
