package Utils;

/**
 * This class basically just acts as a struct to hold different labeled properies that should be stored in a human readable config file
 */
public class ConfigStoreH {
    /**
     * Threshold to use for imagej processing
     */
    public int proc_threshold = 160;
    /**
     * lower threshold for classifying avg % area of image
     */
    public double area_threshold_lower = 0.025;
    /**
     * upper threshold for classifying avg % area of image
     */
    public double area_threshold_upper = 0.05;
    /**
     * sigma (radius) to use with unsharp mask filter
     */
    public double unsharp_sigma = 1.5;
    /**
     * mask weight to use with unsharp mask filter
     */
    public double unsharp_weight = 0.5;
    /**
     * If this is true, then the unsharp mask will be skipped
     */
    public boolean unsharp_skip = false;
    /**
     * If this is false, then the unsharp masked image will overwrite the original. If true, a new file will be generated.
     */
    public boolean unsharp_rename = false;
    /**
     * scan area x coordinate of upper left corner of scan area in inches
     */
    public double scan_x1 = 1.05;
    /**
     * scan area y coordinate of upper left corner of scan area in inches
     */
    public double scan_y1 = 8.98;
    /**
     * scan area x coordinate of lower right corner of scan area in inches
     */
    public double scan_x2 = 3.05;
    /**
     * scan area y coordinate of lower right corner of scan area in inches
     */
    public double scan_y2 = 9.98;
    public ConfigStoreH() {}
    public ConfigStoreH(int proc_threshold, double area_threshold_lower, double area_threshold_upper) {
        this.proc_threshold = proc_threshold;
        this.area_threshold_lower = area_threshold_lower;
        this.area_threshold_upper = area_threshold_upper;
    }//end full constructor
}//end class ConfigStoreH
