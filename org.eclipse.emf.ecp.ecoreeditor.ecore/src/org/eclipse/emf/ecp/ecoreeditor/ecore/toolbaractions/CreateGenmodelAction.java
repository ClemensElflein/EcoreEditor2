package org.eclipse.emf.ecp.ecoreeditor.ecore.toolbaractions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecp.ecoreeditor.IToolbarAction;

public class CreateGenmodelAction implements IToolbarAction {

	@Override
	public String getLabel() {
		return "Create Genmodel for code generation";
	}

	@Override
	public String getImagePath() {
		return "icons/page_white_code.png";
	}

	@Override
	public void execute(Object currentObject) {
	    // We know, that the first resource is an EPackage.
		// Create a genmodel in the same location
		
		ResourceSet resourceSet = (ResourceSet) currentObject;
		Resource ecoreResource = resourceSet.getResources().get(0);

        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setComplianceLevel(GenJDKLevel.JDK70_LITERAL);
        genModel.initialize(Collections.singleton((EPackage)ecoreResource.getContents().get(0)));
        genModel.setModelName(((EPackage)ecoreResource.getContents().get(0)).getName());
        
        try {
            URI genModelURI = URI.createFileURI(ecoreResource.getURI().toFileString()+".genmodel");
            final XMIResourceImpl genModelResource = new XMIResourceImpl(genModelURI);
            genModelResource.getContents().add(genModel);
            genModelResource.setEncoding("utf-8");
            genModelResource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
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
				return false;
			}
		}
		return true;
	}

}
