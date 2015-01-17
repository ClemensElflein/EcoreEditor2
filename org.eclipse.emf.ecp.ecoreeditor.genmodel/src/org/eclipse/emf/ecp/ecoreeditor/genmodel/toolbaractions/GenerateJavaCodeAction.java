package org.eclipse.emf.ecp.ecoreeditor.genmodel.toolbaractions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class GenerateJavaCodeAction implements IToolbarAction {

	@Override
	public String getLabel() {
		return "Create Java Code";
	}

	@Override
	public String getImagePath() {
		return "icons/page_white_cup.png";
	}

	@Override
	public void execute(final Object currentObject) {
		IRunnableWithProgress generateCodeRunnable = new IRunnableWithProgress() {
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Generating Code", IProgressMonitor.UNKNOWN);
			    ResourceSet resourceSet = (ResourceSet) currentObject;
			    GenModel genmodel = (GenModel) resourceSet.getResources().get(0).getContents().get(0);
				
			    
				Generator generator = new Generator();
		        genmodel.setCanGenerate(true);
				generator.setInput(genmodel);
		    	generator.generate(genmodel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, new BasicMonitor.EclipseSubProgress(monitor, 1));
			    generator.generate(genmodel, GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE, new BasicMonitor.EclipseSubProgress(monitor, 1));
			    generator.generate(genmodel, GenBaseGeneratorAdapter.EDITOR_PROJECT_TYPE, new BasicMonitor.EclipseSubProgress(monitor, 1));
			    generator.generate(genmodel, GenBaseGeneratorAdapter.TESTS_PROJECT_TYPE, new BasicMonitor.EclipseSubProgress(monitor, 1));
			    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Code Generation Finished", "The Code generation finished successfully.");
			}
		};
		
		try {
			new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false, generateCodeRunnable);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canExecute(Object object) {
		// We can't execute our Action on Objects other than ResourceSet
		if(!(object instanceof ResourceSet))
			return false;
		// Check, if the ResourceSet contains a Genmodel. If so, we also can't execute our Action
		ResourceSet resourceSet = (ResourceSet) object;
		for(Resource r : resourceSet.getResources()) {
			if(r.getContents().size() > 0 && r.getContents().get(0) instanceof GenModel) {
				return true;
			}
		}
		return false;
	}

}
