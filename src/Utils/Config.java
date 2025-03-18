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

    /**
	 * The dpi to use for scanned images.
	 */
	public int scanDpi = 600;
    public String scanDpiCOMMENT = "The dpi to use for scanned images.";

    /**
	 * Whether or not to add a number suffix at all.
	 */
    public boolean numSuffixEnabled = true;
    public String numSuffixEnabledCOMMENT = "Whether or not to use number suffix settings. If this is false, then number suffix settings won't do anything.";
	/**
	 * The minimum number of digits to include in
	 * the number suffix.
	 */
    public int numSuffixMinDigits = 2;
    public String numSuffixMinDigitsCOMMENT = "The minimum number of digits to display for the number suffix.\n# For example, if you display 2 digits, but the number is 9, then the suffix will be 09.";
	/**
	 * The number for the number suffix to start at when the program begins.
	 */
    public int numSuffixStartNum = 1;
    public String numSuffixStartNumCOMMENT = "The number suffix value to start at when you start the program.";
	/**
	 * The current number for the number suffix.
	 */
    public int numSuffixCurNum = 1;
    public String numSuffixCurNumCOMMENT = "The current number suffix value, which will be used in the next scan.";
	/**
	 * The value to increase num_suffix_cur_num by.
	 * You can use 0 or negative numbers here as well.
	 */
    public int numSuffixIncrement = 1;
    public String numSuffixIncrementCOMMENT = "This number is added to \"numSuffixCurNum\" after each scan. You can set this to zero or a negative as well if you want.";

	/**
	 * The image format code, as in TwainConstants.TWFF.
	 * The code for Tif is 0, as an example.
	 */
    public int imageFormatCode = 0;
    public String imageFormatCodeCOMMENT = "# This number is a code for the image format the scanner will use for scanned images.\n# It can be a huge pain to decipher what each code does, so I'll leave a reference below, accoring to what references I can find.\n# Please be warned that if you select a less common format, I might not know what the proper extension\n# for that is, so I'll just leave a blank extension, which you can then fill in yourself by renaming the file.\n# I know the right extensions for TWFF_TIFF, TWFF_TIFFMULTI, TWFF_BMP, and TWFF_PNG, but other than that, I'm not sure as of writing this.\n# Also, since some of these aren't fully supported, you can only set some of them in the gui menu.\n# 0 = TWFF_TIFF\n# 1 = TWFF_PICT\n# 2 = TWFF_BMP\n# 3 = TWFF_XBM\n# 4 = TWFF_JFIF\n# 5 = TWFF_FPX\n# 6 = TWFF_TIFFMULTI\n# 7 = TWFF_PNG\n# 8 = TWFF_SPIFF\n# 9 = TWFF_EXIF";

    /**
	 * If this is true, then the scanned image will be placed in
	 * a subdirectory.
	 */
	public boolean scanSubdirEnabled = false;
    public String scanSubdirEnabledCOMMENT = "If this is true, then the scanned image will be placed in a subdirectory with a name set by scan_subdir_name.";
	/**
	 * If scan_subdir_enabled is true, then this will be the
	 * name of the subdirectory.
	 */
	public String scanSubdirName = "first";
    public String scanSubdirNameCOMMENT = "If scanSubdirEnabled is true, then this will be the name of the subdirectory for the scanned image.";
	/**
	 * This string will be added as a suffix to the name of the
	 * scanned image. If the value is empty, nothing will be added.
	 * This suffix will go before the number suffix, if one exists.
	 */
	public String scanSuffix = "";
    public String scanSuffixCOMMENT = "This string will be added to the end of the name of the scanned image.\n# If the value is empty, then this feature is effectively disabled.\n# The suffix from this option will go before the number suffix.";
	/**
	 * If this is true, then after each scan, the program will
	 * do a second scan with properties matching the second_scan options.
	 */
	public boolean secondScanEnabled = false;
    public String secondScanEnabledCOMMENT = "If this is true, then after each scan, the program will do another scan,\n# with slightly different settings, matching the secondScan options.";
	/**
	 * The dpi to use for the second scan option.
	 */
	public int secondScanDpi = 300;
    public String secondScanDpiCOMMENT = "The dpi setting to use for the second scan option.";
	/**
	 * If this is true, then on the second scan, the scanned image
	 * will be placed into a subdirectory.
	 */
	public boolean secondScanSubdirEnabled = false;
    public String secondScanSubdirEnabledCOMMENT = "If this is true, then the second scanned image will be placed in a subdirectory with a name set by secondScanSubdirName.";
	/**
	 * If second_scan_subdir_enabled is true, then this will
	 * be the name of the subdirectory.
	 */
	public String secondScanSubdirName = "second";
    public String secondScanSubdirNameCOMMENT = "If secondScanSubdirEnabled is set to true, then this will be the name of the subdirectory for the second scanned image.";
	/**
	 * This string will be appended as a suffix to the name of the scanned
	 * image of the second scan. If it is an empty string, then nothing will be added.
	 */
	public String secondScanSuffix = "";
    public String secondScanSuffixCOMMENT = "This string will be added to the end of the second scanned image.\n# If this value is empty, then this feature is effectively disabled.\n# The suffix from this option will go before the number suffix.";
	/**
	 * If this is true, then the second scan will use corresponding
	 * second_scan_num_suffix settings. If false, then it uses
	 * the normal num suffix settings.
	 */
	public boolean secondScanNumSuffixOverride = false;
    public String secondScanNumSuffixOverrideCOMMENT = "If this is true, then the second scan will use corresponding secondScanNumSuffix settings.";
	/**
	 * If second_scan_num_suffix_override is true, then for the
	 * second scan, the num_suffix_increment is set to this value.
	 */
	public int secondScanNumSuffixIncrement = 0;
    public String secondScanNumSuffixIncrementCOMMENT = "If secondScanNumSuffixOverride is true, then the second scan will use this value for numSuffixIncrement.";
	/**
	 * If this is true, then after each scan, the program
	 * will do a third scan with properties matching the third_scan options.
	 * If third scan is enabled, but second scan is not, then the third scan
	 * will still work.
	 */
	public boolean thirdScanEnabled = false;
    public String thirdScanEnabledCOMMENT = "If this is true, then after each scan, the program will do a third scan,\n# with properties matching the thirdScan options.\n# If third scan is enabled, but second scan is not, then the third scan will still work.";
	/**
	 * The dpi to use for the third scan option.
	 */
	public int thirdScanDpi = 150;
    public String thirdScanDpiCOMMENT = "The dpi setting to use for the third scan option.";
	/**
	 * If this is true, then on the third scan, the scanned image will be placed
	 * into a subdirectory.
	 */
	public boolean thirdScanSubdirEnabled = false;
    public String thirdScanSubdirEnabledCOMMENT = "If this is true, then the third scanned image will be placed in a subdirectory with a name set by thirdScanSubdirName.";
	/**
	 * If third_scan_subdir_enabled is true, then this is the name of the subdirectory
	 * that the third scanned image will be placed into.
	 */
	public String thirdScanSubdirName = "third";
    public String thirdScanSubdirNameCOMMENT = "If thirdScanSubdirEnabled is true, then this will be the name of the subdirectory for the third scanned image.";
	/**
	 * This string will be appended as a suffix to the name of the scanned image
	 * of the third scan. If it is an empty string, then nothing will be added.
	 */
	public String thirdScanSuffix = "";
    public String thirdScanSuffixCOMMENT = "This string will be added to the end of the name of the third scanned image.\n# The suffix from this option will go before the number suffix. If the value is empty, then this feature is effectively disabled.";
	/**
	 * If this is true, then the third scan will use corresponding
	 * third_scan_num_suffix settings. If false, then it uses the
	 * normal num_suffix settings.
	 */
	public boolean thirdScanNumSuffixOverride = false;
    public String thirdScanNumSuffixOverrideCOMMENT = "If this is true, then the third scan will use corresponding thirdScanNumSuffix settings.";
	/**
	 * If third_scan_num_suffix_override is true, then for the third scan,
	 * the num_suffix_increment is set to this value.
	 */
	public int thirdScanNumSuffixIncrement = 0;
    public String thirdScanNumSuffixIncrementCOMMENT = "If thirdScanNumSuffixOverride is true, then the third scan will use this value for numSuffixIncrement.";

    public SimpleResult<String> write_config() {
        return ConfigScribe.writeConfig(this);
    }//end write_config()

    public SimpleResult<String> read_config() {
        return ConfigScribe.readConfig(this);
    }//end read_config()
}//end class Config
