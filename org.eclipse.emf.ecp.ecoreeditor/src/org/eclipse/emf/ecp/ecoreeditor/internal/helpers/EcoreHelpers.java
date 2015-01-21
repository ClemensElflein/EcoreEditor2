package org.eclipse.emf.ecp.ecoreeditor.internal.helpers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EcorePackage;

public class EcoreHelpers {
	  public static boolean isGenericFeature(Object feature)
	  {
	    return feature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES ||
	      feature == EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS ||
	      feature == EcorePackage.Literals.EOPERATION__EGENERIC_EXCEPTIONS ||
	      feature == EcorePackage.Literals.EOPERATION__ETYPE_PARAMETERS ||
	      feature == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE;
	  }
	  
	  public static boolean isGenericElement(Object element) {
		  return EcorePackage.Literals.EGENERIC_TYPE.isInstance(element) ||
				  EcorePackage.Literals.ETYPE_PARAMETER.isInstance(element);
	  }
	  
	  public static Object[] filterGenericElements(Object[] elements) {
		  List<Object> elementList = new LinkedList<Object>();
		  for(Object e : elements) {
			  if(!isGenericElement(e)) {
				  elementList.add(e);
			  }
		  }
		  return elementList.toArray();
	  }
}
