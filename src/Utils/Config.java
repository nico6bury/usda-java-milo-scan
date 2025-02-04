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
	public int procThreshold = 160;
	public String procThresholdNAME = "Processing Threshold";
    public String procThresholdCOMMENT = "This is the number used as the upper threshold in imagej particle analysis";
    /**
	 * lower threshold for classifying avg % area of image
	 */
	public double areaThresholdLower = 0.025;
    public String areaThresholdLowerNAME = "Area Threshold, Lower";
    public String areaThresholdLowerCOMMENT = "Any file with average % area greater than this will get a flag of x";
	
    /**
	 * upper threshold for classifying avg % area of image
	 */
	public double areaThresholdUpper = 0.05;
    public String areaThresholdUpperNAME = "Area Threshold, Upper";
    public String areaThresholdUpperCOMMENT = "Any file with average % area greater than this will get a flag of xx";
	
    /**
	 * sigma (radius) to use with unsharp mask filter
	 */
	public double unsharpSigma = 1.5;
    public String unsharpSigmaNAME = "Unsharp Mask Sigma (Radius)";
    public String unsharpSigmaCOMMENT = "Sigma radius to use with unsharp mask to try and replicate epson setting.";
	
    /**
	 * mask weight to use with unsharp mask filter
	 */
	public double unsharpWeight = 0.5;
    public String unsharpWeightNAME = "Unsharp Mask Weight";
    public String unsharpWeightCOMMENT = "Mask weight to use with unsharp mask to try and replicate epson setting.";
	
    /**
	 * If this is true, then the unsharp mask will be skipped
	 */
	public boolean unsharpSkip = true;
    public String unsharpSkipNAME = "Should Skip Unsharp Mask";
    public String unsharpSkipCOMMENT = "if true, then the unsharp mask will be skipped";
	
    /**
	 * If this is false, then the unsharp masked image will overwrite the original. If true, a new file will be generated.
	 */
	public boolean unsharpRename = false;
    public String unsharpRenameNAME = "Should Rename Unsharp Mask Output";
    public String unsharpRenameCOMMENT = "if true, the unsharp masked image will be renamed as a new file. Otherwise, it will overwrite the original.";
	
    /**
	 * scan area x coordinate of upper left corner of scan area in inches
	 */
	public double scanX1 = 2.89;
    public String scanX1NAME = "Scan Area Upper Left X";
    public String scanX1COMMENT = "x coordinate in inches of upper left corner of scan area";
	
    /**
	 * scan area y coordinate of upper left corner of scan area in inches
	 */
	public double scanY1 = 2;
    public String scanY1NAME = "Scan Area Upper Left Y";
    public String scanY1COMMENT = "y coordinate in inches of upper left corner of scan area";
	
    /**
	 * scan area x coordinate of lower right corner of scan area in inches
	 */
	public double scanX2 = 4.99;
    public String scanX2NAME = "Scan Area Lower Right X";
    public String scanX2COMMENT = "x coordinate in inches of lower right corner of scan area";
	
    /**
	 * scan area y coordinate of lower right corner of scan area in inches
	 */
	public double scanY2 = 9.89;
    public String scanY2NAME = "Scan Area Lower Right Y";
    public String scanY2COMMENT = "y coordinate in inches of lower right corner of scan area";

    public SimpleResult<String> write_config() {
        return ConfigScribe.writeConfig(this);
    }//end write_config()

    public SimpleResult<String> read_config() {
        return ConfigScribe.readConfig(this);
    }//end read_config()
}//end class Config
