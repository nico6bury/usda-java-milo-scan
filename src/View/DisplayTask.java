package View;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

public class DisplayTask extends SwingWorker<ImageIcon[], Void> {
    protected DisplayTaskCaller caller;

    @Override
    protected ImageIcon[] doInBackground() throws Exception {
        ImageIcon[] finishedIcons = new ImageIcon[3];
        
        return finishedIcons;
    }//end doInBackground()

    @Override
    protected void done() {
        try {
            ImageIcon[] finishedIcons = get();
            ImageIcon plain = null;
            ImageIcon kern = null;
            ImageIcon endo = null;
            if (finishedIcons.length >= 1) {plain = finishedIcons[0];}
            if (finishedIcons.length >= 2) {kern = finishedIcons[1];}
            if (finishedIcons.length >= 3) {endo = finishedIcons[2];}
            caller.giveFinishedImageDisplay(plain, kern, endo);
        } catch (InterruptedException ignore) {}
        catch (java.util.concurrent.ExecutionException e) {
            String why = null;
            Throwable cause = e.getCause();
            if (cause != null) {why = cause.getMessage();}
            else {why = e.getMessage();}
            System.err.println("Error retrieving display result: " + why);
        }
    }
    
    public DisplayTask(DisplayTaskCaller caller) {this.caller = caller;}

    public interface DisplayTaskCaller {
        public void giveFinishedImageDisplay(ImageIcon plain, ImageIcon kern, ImageIcon endo);
    }
}//end DisplayTask
