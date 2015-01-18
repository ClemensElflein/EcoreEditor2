/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecp.ecoreeditor.Log;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceSetHelpers.
 * It is a utility class to perform operations on ResourceSet objects.
 */
public class ResourceSetHelpers {

	/**
	 * Save all changes in a ResourceSet
	 *
	 * @param resourceSet the resource set
	 * @return true, if successful
	 */
	public static boolean save(ResourceSet resourceSet) {
		try {
			for (Resource resource : resourceSet.getResources()) {
				resource.save(null);
			}
			return true;
		} catch (IOException e) {
			Log.e(e);
		}
		return false;
	}

	/**
	 * Load resource set with proxies.
	 *
	 * @param resourceURI the resource uri (= File to load)
	 * @param commandStack the command stack
	 * @return the resource set
	 */
	public static ResourceSet loadResourceSetWithProxies(URI resourceURI,
			BasicCommandStack commandStack) {
		// Create a ResourceSet and add the requested Resource
		ResourceSet resourceSet = createResourceSet(commandStack);
		if (addResourceToSet(resourceSet, resourceURI)) {
			return resourceSet;
		}
		return null;
	}

	/**
	 * Creates a ResourceSet with a CommandStack attached to it
	 *
	 * @param commandStack the command stack
	 * @return the resource set
	 */
	private static ResourceSet createResourceSet(CommandStack commandStack) {
		final AdapterFactoryEditingDomain domain = new AdapterFactoryEditingDomain(
				new ComposedAdapterFactory(
						new AdapterFactory[] {
								new CustomReflectiveItemProviderAdapterFactory(),
								new ComposedAdapterFactory(
										ComposedAdapterFactory.Descriptor.Registry.INSTANCE) }),
				commandStack);
		final ResourceSet resourceSet = domain.getResourceSet();
		resourceSet.eAdapters().add(
				new AdapterFactoryEditingDomain.EditingDomainProvider(domain));
		return resourceSet;
	}

	/**
	 * Loads a resource from resourceURI and adds it to the resourceSet.
	 *
	 * @param resourceSet the resource set
	 * @param resourceURI the resource uri
	 * @return true, if successful
	 */
	public static boolean addResourceToSet(ResourceSet resourceSet,
			URI resourceURI) {
		try {
			final Map<Object, Object> loadOptions = new HashMap<Object, Object>();
			loadOptions.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE,
					Boolean.TRUE);

			int rsSizeOld = resourceSet.getResources().size();
			resourceSet.createResource(resourceURI).load(loadOptions);

			// resolve all proxies
			int rsSize = resourceSet.getResources().size();

			EcoreUtil.resolveAll(resourceSet);
			while (rsSize != resourceSet.getResources().size()) {
				EcoreUtil.resolveAll(resourceSet);
				rsSize = resourceSet.getResources().size();
			}
			return true;
		} catch (final IOException e) {
			Log.e(e);
		}
		return false;
	}

	/**
	 * Find all of type in resource set.
	 *
	 * @param object the object
	 * @param type the type
	 * @param includeEcorePackage the include ecore package
	 * @return the list
	 */
	public static List<?> findAllOfTypeInResourceSet(EObject object,
			EClassifier type, boolean includeEcorePackage) {
		return ResourceSetHelpers.findAllOf(
				object.eResource().getResourceSet(), type.getInstanceClass(),
				includeEcorePackage);
	}

	/**
	 * Find all of type in resource set.
	 *
	 * @param <T> the generic type
	 * @param object the object
	 * @param clazz the clazz
	 * @param includeEcorePackage the include ecore package
	 * @return the list
	 */
	public static <T> List<T> findAllOfTypeInResourceSet(EObject object,
			Class<T> clazz, boolean includeEcorePackage) {
		return ResourceSetHelpers
				.findAllOf(object.eResource().getResourceSet(), clazz,
						includeEcorePackage);
	}

	/**
	 * Find all of type in the ResourceSet
	 *
	 * @param <T> the generic type
	 * @param resourceSet the resource set
	 * @param clazz the clazz
	 * @param includeEcorePackage the include ecore package
	 * @return the list
	 */
	public static <T> List<T> findAllOf(ResourceSet resourceSet,
			Class<T> clazz, boolean includeEcorePackage) {
		List<T> result = new ArrayList<T>();

		// Iterate through all EObjects in every Resource in the set and return
		// all Objects of Class clazz.
		if (resourceSet != null) {
			for (Resource resource : resourceSet.getResources()) {
				TreeIterator<EObject> objectIterator = resource
						.getAllContents();
				while (objectIterator.hasNext()) {
					EObject o = objectIterator.next();
					if (o != null && clazz.isInstance(o)) {
						result.add((T) o);
					}
				}
			}
		}

		if (includeEcorePackage) {
			// Find all EDatatypes in the ECore-Package.
			TreeIterator<EObject> objectIterator = EcorePackage.eINSTANCE
					.eAllContents();
			while (objectIterator.hasNext()) {
				EObject o = objectIterator.next();
				if (o != null && clazz.isInstance(o)) {
					result.add((T) o);
				}
			}
		}

		return result;
	}

	/**
	 * Find all of type in EcorePackage.
	 *
	 * @param <T> the generic type
	 * @param clazz the clazz
	 * @return the list
	 */
	public static <T> List<T> findAllInEcorePackage(Class<T> clazz) {
		return findAllOf(null, clazz, true);
	}
}
