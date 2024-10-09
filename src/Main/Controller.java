package Main;

import Utils.ConfigScribe;
import Utils.ConfigStoreC;
import Utils.ConfigStoreH;
import Utils.Result;

import java.util.List;

import IJM.IJProcess;

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
	public void postProcessHandling(Result<String> outputData);

	public ConfigScribe getConfigScribe();
	public ConfigStoreH getConfigStoreH();
	public void setConfigStoreH(ConfigStoreH c);
	public ConfigStoreC getConfigStoreC();
	public List<File> getImageQueue();
	public List<File> getProcessedImages();
	public IJProcess getIJProcess();
}//end interface Controller
