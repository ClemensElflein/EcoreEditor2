package org.eclipse.emf.ecp.ecoreeditor.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecp.ecoreeditor.internal.EcoreEditor;
import org.eclipse.emf.ecp.ecoreeditor.internal.Log;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShortcutHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public ShortcutHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		Log.i("Shortcut Received!!");
		if (editor instanceof EcoreEditor) {
			EcoreEditor eEditor = (EcoreEditor) editor;
			eEditor.processEvent(event);
		}

		return null;
	}
}
