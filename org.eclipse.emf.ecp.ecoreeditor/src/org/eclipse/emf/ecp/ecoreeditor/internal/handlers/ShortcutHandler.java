/*
 * @author Clemens Elflein
 */
package org.eclipse.emf.ecp.ecoreeditor.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecp.ecoreeditor.Log;
import org.eclipse.emf.ecp.ecoreeditor.internal.EcoreEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * The ShorcutHandler receives the shortcuts defined in plugin.xml and passes them to the editor
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShortcutHandler extends AbstractHandler {
	
	public ShortcutHandler() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof EcoreEditor) {
			EcoreEditor eEditor = (EcoreEditor) editor;
			eEditor.processEvent(event);
		}

		return null;
	}
}
