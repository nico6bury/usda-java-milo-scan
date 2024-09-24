package Main;

import View.MainWindow;

public class Root implements Controller {
	/// The main window, holding the gui for the whole application.
	private MainWindow mainWindow = new MainWindow(this);

	public Root() {}
	public Root(MainWindow mainWindow) {this.mainWindow = mainWindow;}

	@Override
	public void HandleMessage(InterfaceMessage m, Object args) {
		System.out.println(mainWindow.getAlignmentX());
		System.out.println(m);
	}//end method HandleMessage()

}//end class Root
