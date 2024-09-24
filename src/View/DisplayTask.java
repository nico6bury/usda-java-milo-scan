package View;

import java.awt.Rectangle;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import IJM.IJProcess;
import IJM.RoiGrid;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

public class DisplayTask extends SwingWorker<ImageIcon[], Void> {
	protected DisplayTaskCaller caller;
	protected File imageMatch;
	protected int imgWidth = 256;
	protected int imgHeight = 256;

	@Override
	protected ImageIcon[] doInBackground() throws Exception {
		ImageIcon[] finishedIcons = new ImageIcon[3];

		// get the base image
		ImagePlus img = IJ.openImage(imageMatch.getAbsolutePath());
		img.getProcessor().flipHorizontal();
		RoiGrid kernGrid = (new IJProcess()).getRoiGrid(img);
		
		// figure out some bounds to zoom in on kernels
		if (kernGrid.rrrs.length > 0) {
			int lowest_x = Integer.MAX_VALUE;
			int highest_x = Integer.MIN_VALUE;
			int lowest_y = Integer.MAX_VALUE;
			int highest_y = Integer.MIN_VALUE;
			for(int i = 0; i < kernGrid.rrrs.length; i++) {
				for(int ii = 0; ii < kernGrid.rrrs[i].length; ii++) {
					Rectangle r = kernGrid.rrrs[i][ii].roi.getBounds();
					if (r.x < lowest_x) {lowest_x = r.x;}
					if (r.y < lowest_y) {lowest_y = r.y;}
					if (r.x + r.width > highest_x) {highest_x = r.x + r.width;}
					if (r.y + r.height > highest_y) {highest_y = r.y + r.height;}
				}//end looping within rows
			}//end looping over rows

			lowest_x = Math.max(0, lowest_x - 75);
			lowest_y = Math.max(0, lowest_y - 100);

			Roi shrunk_dims = new Roi(lowest_x, lowest_y, highest_x, highest_y);
			img = img.crop(new Roi[] {shrunk_dims})[0];
		}//end if we ought to try and zoom into the kernels

		System.out.println("Before scaling image:");
		System.out.println("Image to scale: width,height: " + img.getWidth() + ", " + img.getHeight());
		System.out.println("Label within which to scale: width,height: " + imgWidth + ", " + imgHeight);
		
		// scale image to fit the label
		int newImgWidth = img.getWidth();
		int newImgHeight = img.getHeight();
		if (img.getWidth() > imgWidth) {
			newImgWidth = (int)((double)imgWidth * .9);
			newImgHeight = newImgWidth * img.getHeight() / img.getWidth();
		}
		if (img.getHeight() > imgHeight) {
			newImgHeight = (int)((double)imgHeight * .9);
			newImgWidth = img.getWidth() * newImgHeight / img.getHeight();
		}

		ImageProcessor ip = img.getProcessor();
		// ip.scale(.25, .5);
		ip.setInterpolationMethod(ImageProcessor.BICUBIC);
		ip = ip.resize(newImgWidth, newImgHeight);
		img.setProcessor(ip);
		
		System.out.println("After scaling image:");
		System.out.println("Image to scale: width,height: " + img.getWidth() + ", " + img.getHeight());
		System.out.println("Label within which to scale: width,height: " + imgWidth + ", " + imgHeight);

		// get the other image variants
		ImagePlus kern_img = img.duplicate();
		IJProcess.colorThHSB(
			kern_img,
			IJM.Constants.kernel_lower_hsb_thresh,
			IJM.Constants.kernel_upper_hsb_thresh,
			IJM.Constants.kernel_hsb_pass_or_not
		);
		ImagePlus endo_img = img.duplicate();
		// IJProcess.colorThHSB(
		//     endo_img,
		//     IJM.Constants.endosperm_lower_hsb_thresh,
		//     IJM.Constants.endosperm_upper_hsb_thresh,
		//     IJM.Constants.endosperm_hsb_pass_or_not
		// );
		IJProcess.colorThYUV(
			endo_img,
			IJM.Constants.chalk_endosperm_lower_yuv_thresh,
			IJM.Constants.chalk_endosperm_upper_yuv_thresh,
			IJM.Constants.chalk_endosperm_yuv_pass_or_not
		);
		// IJProcess.colorThGrayscale(
		//     endo_img,
		//     IJM.Constants.chalk_endosperm_lower_gray_thresh,
		//     IJM.Constants.chalk_endosperm_upper_gray_thresh,
		//     IJM.Constants.chalk_endosperm_gray_pass_or_not
		// );
		// Get ImageIcon for each image, scaled down
		finishedIcons[0] = new ImageIcon(img.getImage());
		finishedIcons[1] = new ImageIcon(kern_img.getImage());
		finishedIcons[2] = new ImageIcon(endo_img.getImage());
		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.close();
		}
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
	
	public DisplayTask(DisplayTaskCaller caller, File imageMatch)
	{this.caller = caller; this.imageMatch = imageMatch;}

	public interface DisplayTaskCaller {
		public void giveFinishedImageDisplay(ImageIcon plain, ImageIcon kern, ImageIcon endo);
	}
}//end DisplayTask
