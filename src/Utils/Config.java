package Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ConfigScribe.ConfigScribe;
import ConfigScribe.ConfigStore;
import SimpleResult.SimpleResult;

public class Config implements ConfigStore {

    @Override
    public String getConfigFilename() {
        return "milo-scan.config";
    }//end getConfigFilename()

    @Override
    public List<String> getConfigHeader() {
        List<String> head = new ArrayList<String>();
        head.add("# This is the human-readable config file for the USDA Milo Scan Program");
        head.add("# This milo scan program was created by Nicholas Sixbury, based off the"); 
        head.add("# flour scan project created by the efforts of Nicholas Sixbury, Daniel Brabec,");
        head.add("# and Bill Rust, as part of work for the USDA-ARS in Manhattan,KS. The source");
        head.add("# code is available at https://github.com/nico6bury/usda-java-milo-scan/");
        head.add("# ");
        head.add("# In this file, lines not parsed as variable serialization are automatically ignored.");
        head.add("# Because of this, any line starting with \'#\' will automatically be interpretted as");
        head.add("# a comment. Any comments will be untouched in the config file, so feel free to add");
        head.add("# your own comments.");
        head.add("# ");
        head.add("# If this config file is ever deleted, then it should be re-generated on program");
        head.add("# startup. All parameters will be set to default, and all default comments will be");
        head.add("# added to the new config file, including this header comment.");
        head.add("# ");
        head.add("# Just about every parameter in this config file can also be set through a menu in");
        head.add("# the program, so don't feel as though you have to use this config file to change");
        head.add("# settings.");
        head.add("");
        return head;
    }//end getConfigHeader()

    @Override
    public File getDirectoryLocation() {
        return null;
    }//end getDirectoryLocation()
    
    public String file_chooser_dir = "";

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
	public boolean unsharp_skip = true;
	
    /**
	 * If this is false, then the unsharp masked image will overwrite the original. If true, a new file will be generated.
	 */
	public boolean unsharp_rename = false;
	
    /**
	 * scan area x coordinate of upper left corner of scan area in inches
	 */
	public double scan_x1 = 2.89;
	
    /**
	 * scan area y coordinate of upper left corner of scan area in inches
	 */
	public double scan_y1 = 1.38;
	
    /**
	 * scan area x coordinate of lower right corner of scan area in inches
	 */
	public double scan_x2 = 4.99;
	
    /**
	 * scan area y coordinate of lower right corner of scan area in inches
	 */
	public double scan_y2 = 9.89;

    public SimpleResult<String> write_config() {
        return ConfigScribe.writeConfig(this);
    }//end write_config()

    public SimpleResult<String> read_config() {
        return ConfigScribe.readConfig(this);
    }//end read_config()
}//end class Config
