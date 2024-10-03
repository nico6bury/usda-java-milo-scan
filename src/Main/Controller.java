package Main;

import Utils.ConfigScribe;
import Utils.ConfigStoreC;
import Utils.ConfigStoreH;
import java.util.List;
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

	public ConfigScribe getConfigScribe();
	public ConfigStoreH getConfigStoreH();
	public void setConfigStoreH(ConfigStoreH c);
	public ConfigStoreC getConfigStoreC();
	public List<File> getImageQueue();
	public List<File> getProcessedImages();
}//end interface Controller
