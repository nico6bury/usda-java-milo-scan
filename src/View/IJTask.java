package View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import IJM.IJProcess;
import SimpleResult.SimpleResult;

public class IJTask extends SwingWorker<SimpleResult<String>,Void> {

	protected List<File> imageQueue = new ArrayList<File>();
	protected IJProcess ijProcess = new IJProcess();
	protected IJTaskCaller caller = null;

	/**
	 * Make sure to set imageQueue and ijProcess props before calling this.
	 * If you want async handling, set caller as well.
	 */
	@Override
	protected SimpleResult<String> doInBackground() throws Exception {
		SimpleResult<String> processResult = ijProcess.runMacro(imageQueue);
		return processResult;
	}//end doInBackground()

	@Override
	protected void done() {
		try {
			SimpleResult<String> processResult = get();
			if (caller != null) {
				caller.postProcessHandling(processResult);
			}//end if we have reference to caller
		} catch (InterruptedException ignore) {} 
		catch (java.util.concurrent.ExecutionException e) {
			String why = null;
			Throwable cause = e.getCause();
			if (cause != null) {why = cause.getMessage();}
			else {why = e.getMessage();}
			System.err.println("Error retrieving process result:  " + why);
		}
	}

	public IJTask(List<File> imageQueue, IJProcess ijProcess, IJTaskCaller caller) {
		this.imageQueue = imageQueue;
		this.ijProcess = ijProcess;
		this.caller = caller;
	}//end 2-arg constructor

	public interface IJTaskCaller {
		public void postProcessHandling(SimpleResult<String> result);
	}
}//end class IJTask
