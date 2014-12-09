package org.eclipse.emf.ecp.ecoreeditor.ecore.controls;

import org.eclipse.core.databinding.Binding;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecp.view.spi.core.swt.SimpleControlJFaceViewerSWTRenderer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class AttributesView extends SimpleControlJFaceViewerSWTRenderer {

	@Override
	protected Binding[] createBindings(Viewer viewer, Setting setting) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Viewer createJFaceViewer(Composite parent, Setting setting) {
		TableViewer viewer = new TableViewer(parent);
		return viewer;
	}

	@Override
	protected String getUnsetText() {
		return "unset";
	}

}
