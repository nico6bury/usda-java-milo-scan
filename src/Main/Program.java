package Main;
import View.MainWindow;

public class Program {
	/// The MAIN METHOD for the whole application.
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable(){
			public void run() {
				Root root = new Root();
				MainWindow mainWindow = new MainWindow(root);
				mainWindow.setVisible(true);
			}//end method run()
		});
    }//end main method
}//end class Program
