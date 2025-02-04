package Main;

import java.util.List;

import IJM.IJProcess;
import SimpleResult.SimpleResult;
import Utils.Config;

import java.io.File;

public interface Controller {
	public enum InterfaceMessage {
		ConnectScanner,
		ResetScanner,
		Scan,
		AddFilesToQueue,
		ProcessQueue,
		EmptyQueue,
		EmptyOutput,
	}//end enum InterfaceMessage

	public Object handleMessage(InterfaceMessage m, Object args);
	public void postProcessHandling(SimpleResult<String> outputData);

	public Config getConfig();
	public void setConfig(Config c);
	public List<File> getImageQueue();
	public List<File> getProcessedImages();
	public IJProcess getIJProcess();
}//end interface Controller
