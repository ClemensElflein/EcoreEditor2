package org.eclipse.emf.ecp.ecoreeditor.helpers;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecp.ecoreeditor.Log;

import treeInput.TreeInput;
import treeInput.TreeInputPackage;

public class ResourceChangedListener extends AdapterImpl {

	private TreeInput treeInput;

	public ResourceChangedListener(TreeInput treeInput) {
		super();
		this.treeInput = treeInput;
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (msg instanceof ResourceChangedNotification) {
			Log.e("Added Resource");
			ResourceSet resourceSet = (ResourceSet) msg.getNewValue();
			EList<EObject> eRoots = new BasicEList<EObject>();
			for (Resource r : resourceSet.getResources()) {
				eRoots.addAll(r.getContents());
			}
			this.treeInput
					.eSet(TreeInputPackage.eINSTANCE.getTreeInput_TreeRoots(),
							eRoots);
		}
	}
}
