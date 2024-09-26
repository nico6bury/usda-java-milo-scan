package Main;

import Utils.ConfigScribe;
import Utils.ConfigStoreC;
import Utils.ConfigStoreH;

public interface Controller {
	public enum InterfaceMessage {
		ConnectScanner,
		ResetScanner,
		Scan,
		AddFilesToQueue,
		ProcessQueue,
		EmptyQueue,
	}//end enum InterfaceMessage

	public Object handleMessage(InterfaceMessage m, Object args);

	public ConfigScribe getConfigScribe();
	public ConfigStoreH getConfigStoreH();
	public void setConfigStoreH(ConfigStoreH c);
	public ConfigStoreC getConfigStoreC();
}//end interface Controller
