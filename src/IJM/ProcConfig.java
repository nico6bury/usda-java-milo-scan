package IJM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ConfigScribe.ConfigScribe;
import ConfigScribe.ConfigStore;
import SimpleResult.SimpleResult;

public class ProcConfig implements ConfigStore {

    @Override
    public String getConfigFilename() {
        return "milo-proc.conf";
    }//end getConfigFilename()

    @Override
    public List<String> getConfigHeader() {
        List<String> head = new ArrayList<String>();
        head.add("This is the configuration file for the USDA Milo Scan Program that");
        head.add("handles image processing settings. This program was created by Nicholas");
        head.add("Sixbury, based off the flour scan project created by the efforts of");
        head.add("Nicholas Sixbury, Daniel Brabec, and Bill Rust, as part of work for the");
        head.add("USDA-ARS in Manhattan, KS. The source code is available at ");
        head.add("https://github.com/nico6bury/usda-java-milo-scan/");
        head.add("");
        head.add("In this file, lines not parsed as variable serialization are automatically ignored.");
        head.add("Because of this, any line starting with \'#\' will automatically be interpretted as");
        head.add("a comment. Any comments will be untouched in the config file, so feel free to add");
        head.add("your own comments.");
        head.add("");
        head.add("If this config file is ever deleted, then it should be re-generated on program");
        head.add("startup. All parameters will be set to default, and all default comments will be");
        head.add("added to the new config file, including this header comment.");
        head.add("");
        head.add("These settings can significantly change the numbers produced with image processing,");
        head.add("so please be very careful when altering these settings.");
        head.add("");
        head.add("");
        head.add("A few notes on using these settings:");
        head.add("s1, s2, and s3 denote either R, G, and B or H, S, and B, depending on whether");
        head.add("you are using RGB or HSB color thresholds. The use_hsb setting for each threshold");
        head.add("switches between those two color spaces.");
        head.add("When pass is set to true for a channel, values outside the min and max are thresholded out.");
        head.add("When pass is set to false for a channel, values inside of the min and max are thresholded out.");
        head.add("The flip setting is similar to the pass setting, but it works for the whole threshold instead of");
        head.add("a single channel.");
        head.add("The particles options setting allows setting options like include holes or exlcude edges.");
        head.add("It uses the same abreviations as the ImageJ macro language particle analysis calls, ");
        head.add("so look to the macro recorder if you need help figuring out the proper formatting.");
        head.add("The options you specify are added after display when calling particle analysis.");
        head.add("This is done to avoid messing with kernel threshold output.");
        return head;
    }//end getConfigHeader()

    /**
     * Use the AppData location for the config file. If we can't find or create
     * the appropriate directory, then we default to returning null, which tells
     * the Config Scribe to use the default location.
     */
    @Override
    public File getDirectoryLocation() {
        File dirLoc = new File(System.getProperty("user.home"), "AppData\\Local\\ARS-SPIERU");
        if (!dirLoc.exists()) {
            if (dirLoc.mkdir()) {
                return dirLoc;
            } else {return null;}
        } else {return dirLoc;}
    }//end getDirectoryLocation()

    public SimpleResult<String> write_config() {
        return ConfigScribe.writeConfig(this);
    }//end write_config()

    public SimpleResult<String> read_config() {
        return ConfigScribe.readConfig(this);
    }//end read_config()

    /**
     * Controls distTolerance value in RoiGrid.groupRoiRows
     */
    public int distTolerance = 4;
    public int distToleranceCOMMENT = 4;

    public boolean kernel_thresh_use_hsb = true;
    public String kernel_thresh_use_hsbCOMMENT = "Default: true";
    public int kernel_thresh_s1_min = 120;
    public String kernel_thresh_s1_minCOMMENT = "Default: 120";
    public int kernel_thresh_s1_max = 180;
    public String kernel_thresh_s1_maxCOMMENT = "Default: 180";
    public int kernel_thresh_s2_min = 30;
    public String kernel_thresh_s2_minCOMMENT = "Default: 30";
    public int kernel_thresh_s2_max = 255;
    public String kernel_thresh_s2_maxCOMMENT = "Default: 255";
    public int kernel_thresh_s3_min = 0;
    public String kernel_thresh_s3_minCOMMENT = "Default: 0";
    public int kernel_thresh_s3_max = 255;
    public String kernel_thresh_s3_maxCOMMENT = "Default: 255";
    public boolean kernel_thresh_s1_pass = true;
    public String kernel_thresh_s1_passCOMMENT = "Default: true";
    public boolean kernel_thresh_s2_pass = true;
    public String kernel_thresh_s2_passCOMMENT = "Default: true";
    public boolean kernel_thresh_s3_pass = true;
    public String kernel_thresh_s3_passCOMMENT = "Default: true";
    public boolean kernel_thresh_flip = true;
    public String kernel_thresh_flipCOMMENT = "Default: true";

    public int kernel_particles_size_min = 400;
    public String kernel_particles_size_minCOMMENT = "Default: 400";
    public int kernel_particles_size_max = 12500;
    public String kernel_particles_size_maxCOMMENT = "Default: 12500";
    public double kernel_particles_circ_min = 0.03;
    public String kernel_particles_circ_minCOMMENT = "Default: 0.03";
    public double kernel_particles_circ_max = 1.0;
    public String kernel_particles_circ_maxCOMMENT = "Default: 1.0";
    // after display
    public String kernel_particles_options = "include";
    public String kernel_particles_optionsCOMMENT = "Default: include";

    public boolean cells_thresh_use_hsb = true;
    public String cells_thresh_use_hsbCOMMENT = "Default: true";
    public int cells_thresh_s1_min = 149;
    public String cells_thresh_s1_minCOMMENT = "Default: 149";
    public int cells_thresh_s1_max = 157;
    public String cells_thresh_s1_maxCOMMENT = "Default: 157";
    public int cells_thresh_s2_min = 0;
    public String cells_thresh_s2_minCOMMENT = "Default: 0";
    public int cells_thresh_s2_max = 255;
    public String cells_thresh_s2_maxCOMMENT = "Default: 255";
    public int cells_thresh_s3_min = 0;
    public String cells_thresh_s3_minCOMMENT = "Default: 0";
    public int cells_thresh_s3_max = 255;
    public String cells_thresh_s3_maxCOMMENT = "Default: 255";
    public boolean cells_thresh_s1_pass = true;
    public String cells_thresh_s1_passCOMMENT = "Default: true";
    public boolean cells_thresh_s2_pass = true;
    public String cells_thresh_s2_passCOMMENT = "Default: true";
    public boolean cells_thresh_s3_pass = true;
    public String cells_thresh_s3_passCOMMENT = "Default: true";
    public boolean cells_thresh_flip = false;
    public String cells_thresh_flipCOMMENT = "Default: false";

    public int cells_particles_size_min = 6500;
    public String cells_particles_size_minCOMMENT = "Default: 6500";
    public int cells_particles_size_max = 12500;
    public String cells_particles_size_maxCOMMENT = "Default: 12500";
    // right after size, don't even have display
    public String cells_particles_options = "exclude include";
    public String cells_particles_optionsCOMMENT = "Default: exclude include";

    public boolean xsec_thresh_use_hsb = false;
    public String xsec_thresh_use_hsbCOMMENT = "Default: false";
    public int xsec_thresh_s1_min = 0;
    public String xsec_thresh_s1_minCOMMENT = "Default: 0";
    public int xsec_thresh_s1_max = 254;
    public String xsec_thresh_s1_maxCOMMENT = "Default: 254";
    public int xsec_thresh_s2_min = 90;
    public String xsec_thresh_s2_minCOMMENT = "Default: 90";
    public int xsec_thresh_s2_max = 255;
    public String xsec_thresh_s2_maxCOMMENT = "Default: 255";
    public int xsec_thresh_s3_min = 0;
    public String xsec_thresh_s3_minCOMMENT = "Default: 0";
    public int xsec_thresh_s3_max = 255;
    public String xsec_thresh_s3_maxCOMMENT = "Default: 255";
    public boolean xsec_thresh_s1_pass = true;
    public String xsec_thresh_s1_passCOMMENT = "Default: true";
    public boolean xsec_thresh_s2_pass = true;
    public String xsec_thresh_s2_passCOMMENT = "Default: true";
    public boolean xsec_thresh_s3_pass = true;
    public String xsec_thresh_s3_passCOMMENT = "Default: true";
    public boolean xsec_thresh_flip = false;
    public String xsec_thresh_flipCOMMENT = "Default: false";

    public int xsec_particles_size_min = 50;
    public String xsec_particles_size_minCOMMENT = "Default: 50";
    public int xsec_particles_size_max = 2500;
    public String xsec_particles_size_maxCOMMENT = "Default: 2500";
    public double xsec_particles_circ_min = 0.03;
    public String xsec_particles_circ_minCOMMENT = "Default: 0.03";
    public double xsec_particles_circ_max = 1.0;
    public String xsec_particles_circ_maxCOMMENT = "Default: 1.0";
    // after display
    public String xsec_particles_options = "include";
    public String xsec_particles_optionsCOMMENT = "Default: include";

    public boolean chalk_thresh_use_hsb = false;
    public String chalk_thresh_use_hsbCOMMENT = "Default: false";
    public int chalk_thresh_s1_min = 150;
    public String chalk_thresh_s1_minCOMMENT = "Default: 150";
    public int chalk_thresh_s1_max = 240;
    public String chalk_thresh_s1_maxCOMMENT = "Default: 240";
    public int chalk_thresh_s2_min = 160;
    public String chalk_thresh_s2_minCOMMENT = "Default: 160";
    public int chalk_thresh_s2_max = 240;
    public String chalk_thresh_s2_maxCOMMENT = "Default: 240";
    public int chalk_thresh_s3_min = 160;
    public String chalk_thresh_s3_minCOMMENT = "Default: 160";
    public int chalk_thresh_s3_max = 240;
    public String chalk_thresh_s3_maxCOMMENT = "Default: 240";
    public boolean chalk_thresh_s1_pass = true;
    public String chalk_thresh_s1_passCOMMENT = "Default: true";
    public boolean chalk_thresh_s2_pass = true;
    public String chalk_thresh_s2_passCOMMENT = "Default: true";
    public boolean chalk_thresh_s3_pass = true;
    public String chalk_thresh_s3_passCOMMENT = "Default: true";
    public boolean chalk_thresh_flip = false;
    public String chalk_thresh_flipCOMMENT = "Default: false";

    public int chalk_particles_size_min = 50;
    public String chalk_particles_size_minCOMMENT = "Default: 50";
    public int chalk_particles_size_max = 2500;
    public String chalk_particles_size_maxCOMMENT = "Default: 2500";
    public double chalk_particles_circ_min = 0.03;
    public String chalk_particles_circ_minCOMMENT = "Default: 0.03";
    public double chalk_particles_circ_max = 1.0;
    public String chalk_particles_circ_maxCOMMENT = "Default: 1.0";
    // after display
    public String chalk_particles_options = "include";
    public String chalk_particles_optionsCOMMENT = "Default: include";

    public boolean chkgrm_thresh_use_hsb = false;
    public String chkgrm_thresh_use_hsbCOMMENT = "Default: false";
    public int chkgrm_thresh_s1_min = 0;
    public String chkgrm_thresh_s1_minCOMMENT = "Default: 0";
    public int chkgrm_thresh_s1_max = 150;
    public String chkgrm_thresh_s1_maxCOMMENT = "Default: 150";
    public int chkgrm_thresh_s2_min = 100;
    public String chkgrm_thresh_s2_minCOMMENT = "Default: 100";
    public int chkgrm_thresh_s2_max = 240;
    public String chkgrm_thresh_s2_maxCOMMENT = "Default: 240";
    public int chkgrm_thresh_s3_min = 0;
    public String chkgrm_thresh_s3_minCOMMENT = "Default: 0";
    public int chkgrm_thresh_s3_max = 240;
    public String chkgrm_thresh_s3_maxCOMMENT = "Default: 240";
    public boolean chkgrm_thresh_s1_pass = false;
    public String chkgrm_thresh_s1_passCOMMENT = "Default: false";
    public boolean chkgrm_thresh_s2_pass = true;
    public String chkgrm_thresh_s2_passCOMMENT = "Default: true";
    public boolean chkgrm_thresh_s3_pass = true;
    public String chkgrm_thresh_s3_passCOMMENT = "Default: true";
    public boolean chkgrm_thresh_flip = false;
    public String chkgrm_thresh_flipCOMMENT = "Default: false";

    public int chkgrm_particles_size_min = 25;
    public String chkgrm_particles_size_minCOMMENT = "Default: 25";
    public int chkgrm_particles_size_max = 2500;
    public String chkgrm_particles_size_maxCOMMENT = "Default: 2500";
    public double chkgrm_particles_circ_min = 0.03;
    public String chkgrm_particles_circ_minCOMMENT = "Default: 0.03";
    public double chkgrm_particles_circ_max = 1.0;
    public String chkgrm_particles_circ_maxCOMMENT = "Default1.0";
    // after display
    public String chkgrm_particles_options = "";
    public String chkgrm_particles_optionsCOMMENT = "Default: ";
}//end class ProcConfig
