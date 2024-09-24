package Main;

public interface Controller {
	public enum InterfaceMessage {
		ConnectScanner,
		ScanAndAddQueue,
		AddFilesToQueue,
		ProcessQueue,
		EmptyQueue,
	}//end enum InterfaceMessage

	public void HandleMessage(InterfaceMessage m, Object args);
}//end interface Controller
