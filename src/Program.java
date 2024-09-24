import View.MainWindow;

public class Program {
    private static MainWindow mainWindow = new MainWindow();
	public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable(){
			public void run() {
				mainWindow.setVisible(true);
			}
		});
    }
}
