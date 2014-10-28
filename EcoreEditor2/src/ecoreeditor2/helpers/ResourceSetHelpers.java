package ecoreeditor2.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecp.view.model.common.edit.provider.CustomReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.ui.part.FileEditorInput;

import ecoreeditor2.Activator;
import ecoreeditor2.Log;

public class ResourceSetHelpers {

	// Saves all changes in a ResourceSet
	public static boolean save(ResourceSet resourceSet) {
		try {
			for(Resource resource : resourceSet.getResources()) {
				resource.save(null);
			}
			return true;
		} catch (IOException e) {
			Log.e(e);
		}
		return false;
	}
	
	public static ResourceSet loadResourceSetWithProxies(URI resourceURI, BasicCommandStack commandStack) {
		// Create a ResourceSet and add the requested Resource
		ResourceSet resourceSet = createResourceSet(commandStack);
		if(addResourceToSet(resourceURI, resourceSet)) {
			return resourceSet;
		}
		return null;
	}
	
	private static ResourceSet createResourceSet(CommandStack commandStack) {
		final ResourceSet resourceSet = new ResourceSetImpl();

		final AdapterFactoryEditingDomain domain = new AdapterFactoryEditingDomain(
			new ComposedAdapterFactory(
					new AdapterFactory[] {
							new CustomReflectiveItemProviderAdapterFactory(),
							new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE)
							}),
					commandStack, resourceSet);
		resourceSet.eAdapters().add(new AdapterFactoryEditingDomain.EditingDomainProvider(domain));
		return resourceSet;
	}
	
	public static boolean addResourceToSet(URI resourceURI, ResourceSet resourceSet) {
		try {
			final Map<Object, Object> loadOptions = new HashMap<Object, Object>();
			loadOptions
				.put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);

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
}
