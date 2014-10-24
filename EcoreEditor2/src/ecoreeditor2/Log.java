package ecoreeditor2;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/*
 * Utility class for simplifying Logging
 */
public class Log {
	
	public static void e(Throwable e) {
		Log.e(e.getMessage());
	}
	
	
	public static void i(String log) {
		Activator.getDefault().getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, log));
	}
	public static void e(String log) {
		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, log));
	}
	public static void w(String log) {
		Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, log));
	}
}
