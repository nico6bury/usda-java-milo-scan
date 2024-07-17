package IJM;

/**
 * Returns some values for thresholding in various places.
 * This class was made to have consistent color thresholding,
 * using the IJProcess.colorThHSB() method.
 */
public class Constants {
    public static int[] kernel_lower_hsb_thresh() {
        return new int[] {120,0,0};
    }
    public static int[] kernel_upper_hsb_thresh() {
        return new int[] {180,255,255};
    }
    public static String[] kernel_hsb_pass_or_not() {
        return new String[] {"stop","pass","pass"};
    }

    public static int[] endosperm_lower_hsb_thresh() {
        return new int[] {120,0,166};
    }
    public static int[] endosperm_upper_hsb_thresh() {
        return new int[] {180,255,255};
    }
    public static String[] endosperm_hsb_pass_or_not() {
        return new String[] {"stop","pass","pass"};
    }
}//end class Constants
