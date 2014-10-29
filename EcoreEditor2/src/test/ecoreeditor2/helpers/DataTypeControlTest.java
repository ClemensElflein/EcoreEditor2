package test.ecoreeditor2.helpers;

import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.ui.view.ECPRendererException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import ecoreeditor2.controls.DataTypeControl;

public class DataTypeControlTest {

	private Shell shell;
	private EClass object;
	private DataTypeControl control;

	@Before
	public void init() throws ECPRendererException {
		shell = new Shell(Display.getDefault());

		object = EcoreFactory.eINSTANCE.createEClass();
		object.getEStructuralFeatures().add(
				EcorePackage.eINSTANCE.getETypedElement_EType());
	}

	@Test
	public void testDatabindingWorks() {
		fail("Not implemented yet");
	}
}
