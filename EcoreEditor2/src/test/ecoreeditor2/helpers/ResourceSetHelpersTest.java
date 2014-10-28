package test.ecoreeditor2.helpers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Test;

import ecoreeditor2.Log;
import ecoreeditor2.helpers.ResourceSetHelpers;

public class ResourceSetHelpersTest {

	private static final String bowlingURI = "platform:/plugin/org.eclipse.emf.emfstore.examplemodel/model/bowling.ecore";
	
	@Test
	public void testFindAllOfTypeInResourceSet() {
		
		ResourceSet resourceSet = ResourceSetHelpers.loadResourceSetWithProxies(URI.createURI(bowlingURI), new BasicCommandStack());

		assertNotNull(resourceSet);
		
		List<EDataType> dataTypes = ResourceSetHelpers.findAllOf(resourceSet, EDataType.class, false);
		
		String[] expected = new String[] {
				"TournamentType",
				"Gender",
				"XMLDate"
		};
		
		checkResults(dataTypes, expected);
		
		List<EClass> classes = ResourceSetHelpers.findAllOf(resourceSet, EClass.class, false);
		
		expected = new String[] {
				"League",
				"Player",
				"Tournament",
				"Matchup",
				"Game",
				"PlayerToPointsMap",
				"Referee",
				"RefereeToGamesMap",
				"Area",
				"Fan",
				"Merchandise"
		};
		
		checkResults(classes, expected);
		
		List<EClassifier> classifiers = ResourceSetHelpers.findAllOf(resourceSet, EClassifier.class, false);
		
		expected = new String[] {
				"League",
				"Player",
				"Tournament",
				"Matchup",
				"Game",
				"PlayerToPointsMap",
				"Referee",
				"RefereeToGamesMap",
				"Area",
				"Fan",
				"Merchandise",
				"TournamentType",
				"Gender",
				"XMLDate"
		};
		
		checkResults(classifiers, expected);
	}

	private static <T> void checkResults(List<T> result, String[] expected) {
		List<String> expectedList = new ArrayList<String>(Arrays.asList(expected));
		
		for(T o : result) {
			if(!(o instanceof EClassifier)) {
				fail(o.getClass().getSimpleName() + " is no EClassifier");
			}
			if(expectedList.contains(((EClassifier)o).getName())) {
				expectedList.remove(((EClassifier)o).getName());
			} else {
				fail(((EClassifier)o).getName() + " was not expected but found!");
			}
		}
		
		if(!expectedList.isEmpty()) {
			fail(expectedList.toString() + " were expected but NOT found!");
		}
	}
}
