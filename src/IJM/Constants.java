package IJM;

/**
 * Returns some values for thresholding in various places.
 * This class was made to have consistent color thresholding,
 * using the IJProcess.colorThHSB() method.
 * Each set of thresholds has a corresponding enabled flag which indicates whether the
 * threshold should currently be in use.
 */
public class Constants {
	/**
	 * This enum is used for color thresholding and corresponds to the labels
	 * used in the Color Thresholding GUI for imagej's color thresholder.
	 * If pass is indicated, it means that the threshold is within the lower and upper thresholds.
	 * If stop is indicated, it means that the threshold is outside the lower and upper thresholds.
	 */
	public enum PassOrNot {
		Pass,
		Stop,
	}//end enum PassOrNot

	public static boolean kernel_hsb_thresh_enabled = true;
	public static int[] kernel_lower_hsb_thresh = new int[] {120,0,0};
	public static int[] kernel_upper_hsb_thresh = new int[] {180,255,255};
	public static PassOrNot[] kernel_hsb_pass_or_not = new PassOrNot[] {PassOrNot.Stop,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean cells_hsb_thresh_enabled = true;
	public static int[] cells_lower_hsb_thresh = new int[] {149,0,0};
	public static int[] cells_upper_hsb_thresh = new int[] {158,255,255};
	public static PassOrNot[] cells_hsb_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};
	
	public static boolean chalk_endosperm_hsb_thresh_enabled = false;
	public static int[] chalk_endosperm_lower_hsb_thresh = new int[] {120,0,166};
	public static int[] chalk_endosperm_upper_hsb_thresh = new int[] {180,255,255};
	public static PassOrNot[] chalk_endosperm_hsb_pass_or_not = new PassOrNot[] {PassOrNot.Stop,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean chalk_endosperm_lab_thresh_enabled = false;
	public static int[] chalk_endosperm_lower_lab_thresh = new int[] {181,0,100};
	public static int[] chalk_endosperm_upper_lab_thresh = new int[] {255,255,255};
	public static PassOrNot[] chalk_endosperm_lab_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean chalk_endosperm_yuv_thresh_enabled = false;
	public static int[] chalk_endosperm_lower_yuv_thresh = new int[] {166,120,0};
	public static int[] chalk_endosperm_upper_yuv_thresh = new int[] {255,255,255};
	public static PassOrNot[] chalk_endosperm_yuv_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean chalk_endosperm_rgb_thresh_enabled = true;
	public static int[] chalk_endosperm_lower_rgb_thresh = new int[] {170,170,170};
	public static int[] chalk_endosperm_upper_rgb_thresh = new int[] {255,255,255};
	public static PassOrNot[] chalk_endosperm_rgb_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean vitreous_endosperm_rgb_thresh_enabled = true;
	public static int[] vitreous_endosperm_lower_rgb_thresh = new int[] {0,75,0};
	public static int[] vitreous_endosperm_upper_rgb_thresh = new int[] {147,144,115};
	public static PassOrNot[] vitreous_endosperm_rgb_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean germ_endosperm_rgb_pre_thresh_enabled = true;
	public static int[] germ_endosperm_lower_rgb_pre_thresh = new int[] {150,140,0};
	public static int[] germ_endosperm_upper_rgb_pre_thresh = new int[] {255,255,255};
	public static PassOrNot[] germ_endosperm_rgb__pre_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean germ_endosperm_rgb_post_thresh_enabled = true;
	public static int[] germ_endosperm_lower_rgb_post_thresh = new int[] {160,1,1};
	public static int[] germ_endosperm_upper_rgb_post_thresh = new int[] {195,195,150};
	public static PassOrNot[] germ_endosperm_rgb__post_pass_or_not = new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass};

	public static boolean chalk_endosperm_gray_thresh_enabled = false;
	public static int chalk_endosperm_lower_gray_thresh = 180;
	public static int chalk_endosperm_upper_gray_thresh = 255;
	public static PassOrNot chalk_endosperm_gray_pass_or_not = PassOrNot.Pass;
}//end class Constants
