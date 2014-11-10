package org.eclipse.emf.ecp.ecoreeditor.helpers;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class ResourceChangedNotification extends NotificationImpl {
	public ResourceChangedNotification(ResourceSet resourceSet) {
		super(0, resourceSet, resourceSet);
	}
}
