package View;

import java.awt.Rectangle;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import IJM.IJProcess;
import IJM.ProcConfig;
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
		ProcConfig pc = new ProcConfig();
		pc.read_config();
		// get the base image
		ImagePlus img = IJ.openImage(imageMatch.getAbsolutePath());
		img.getProcessor().flipHorizontal();
		RoiGrid kernGrid = (new IJProcess()).getRoiGrid(img, null, pc);
		ImagePlus bak = img.duplicate();

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

		if (img.getWidth() == 0 && img.getHeight() == 0) {
			System.out.println("Image width and height 0 after attempting to zoom. Resorting to backup.");
			img = bak;
		}//end if we need to return to our backup because we screwed up the original while trying to crop it.

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
		IJProcess.colorTh(
			kern_img,
			new int[] {pc.kernel_thresh_s1_min, pc.kernel_thresh_s2_min, pc.kernel_thresh_s3_min},
			new int[] {pc.kernel_thresh_s1_max, pc.kernel_thresh_s2_max, pc.kernel_thresh_s3_max},
			new boolean[] {pc.kernel_thresh_s1_pass, pc.kernel_thresh_s2_pass, pc.kernel_thresh_s3_pass},
			pc.kernel_thresh_flip,
			pc.kernel_thresh_use_hsb
		);
		ImagePlus chkImg = img.duplicate();
		// IJProcess.colorThHSB(
		//     endo_img,
		//     IJM.Constants.endosperm_lower_hsb_thresh,
		//     IJM.Constants.endosperm_upper_hsb_thresh,
		//     IJM.Constants.endosperm_hsb_pass_or_not
		// );
		IJProcess.colorTh(
			chkImg,
			new int[] {pc.chalk_thresh_s1_min, pc.chalk_thresh_s2_min, pc.chalk_thresh_s3_min},
			new int[] {pc.chalk_thresh_s1_max, pc.chalk_thresh_s2_max, pc.chalk_thresh_s3_max},
			new boolean[] {pc.chalk_thresh_s1_pass, pc.chalk_thresh_s2_pass, pc.chalk_thresh_s3_pass},
			pc.chalk_thresh_flip,
			pc.chalk_thresh_use_hsb
		);
		// IJProcess.colorThGrayscale(
		//     endo_img,
		//     IJM.Constants.chalk_endosperm_lower_gray_thresh,
		//     IJM.Constants.chalk_endosperm_upper_gray_thresh,
		//     IJM.Constants.chalk_endosperm_gray_pass_or_not
		// );
		blackToWhite(img);
		blackToWhite(kern_img);
		blackToWhite(chkImg);
		// Get ImageIcon for each image, scaled down
		finishedIcons[0] = new ImageIcon(img.getImage());
		finishedIcons[1] = new ImageIcon(kern_img.getImage());
		finishedIcons[2] = new ImageIcon(chkImg.getImage());
		RoiManager rm = RoiManager.getInstance();
		if (rm != null) {
			rm.close();
		}
		return finishedIcons;
	}//end doInBackground()

	/**
	 * Replaces pixels that are (0,0,0) with (255,255,255)
	 * @param img
	 */
	protected void blackToWhite(ImagePlus img) {
		ImageProcessor prc = img.getProcessor();
		for (int x = 0; x < prc.getWidth(); x++) {
			for (int y = 0; y < prc.getHeight(); y++) {
				int[] rgb = prc.getPixel(x,y,null);
				int r = rgb[0];
				int g = rgb[1];
				int b = rgb[2];
				if (r == 0 && g == 0 && b == 0) {
					prc.putPixel(x, y, new int[] {255,255,255});
				}//end if we found a completely black pixel
			}//end looping through y values
		}//end looping through x values
	}//end blackToWhite(img)

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
