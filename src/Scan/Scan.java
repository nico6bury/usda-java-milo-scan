package Scan;

import java.io.File;
import java.nio.file.FileSystemException;

// import SK.gnome.twain.TwainConstants;
import SK.gnome.twain.TwainException;
import SK.gnome.twain.TwainManager;
import SK.gnome.twain.TwainSource;
import SimpleResult.SimpleResult;
import Utils.Config;

/**
 * This class is meant to be used for scanning images with an EPSON scanner for image processing.
 * @author Nicholas.Sixbury
 */
public class Scan {
	/**
	 * Assumedly an object pointing to the scanner we want to access.
	 */
	TwainSource scanSource;
	/**
	 * Returns the scan source, assuming one is available.
	 * Allows for manual manipulation.
	 */
	public TwainSource getSource() {return scanSource;}

	/**
	 * Tries to find a valid TwainSource and save it for later.
	 * @return Returns an error if one was thrown, otherwise is Ok.
	 */
	public SimpleResult<SimpleResult.ResultType> initScanner() {
		// make an attempt to initialize the scanner
		try {
			// Figure out which source to use
			TwainSource tss[] = TwainManager.listSources();
			System.out.println("Twain Sources on following lines.");
			for (TwainSource ts : tss) {
				System.out.println(ts.toString());
			}//end looping over the twain sources we found
			System.out.println();

			// set source to null ???
			scanSource = TwainManager.selectSource(null);
			System.out.println("Selected source is " + scanSource);
			// check that source isn't null ???
			if (scanSource == null) {
				throw new NullPointerException("The twain source was null!");
			}//end if scanSource is null
		}//end trying to figure out the twain source
		catch (Exception e) {
			return new SimpleResult<>(e);
		}//end catching any exceptions

		// if we've reached this point, we must be fine
		return new SimpleResult<>();
	}//end initScanner()

	public boolean isScannerConnected() {
		try {
			if (scanSource == null) {return false;}
			System.out.println("ScanSource Condition Code: " + scanSource.getConditionCode());
			return true;
		}//end trying to figure out if the scanner is connected
		catch (TwainException twe) {return false;}
		catch (NullPointerException npe) {return false;}
	}

	/**
	 * Tries to set the scan settings of the saved twain source.
	 * @return Returns an error if one was thrown, otherwise is Ok.
	 */
	public SimpleResult<SimpleResult.ResultType> setScanSettings(Config config) {
		// make an attempt to set settings of twain source
		try {
			// it's unknown what this does (found in Bill's config)
			scanSource.setVisible(false);
			// controls whether scanning progress bar shows
			scanSource.setIndicators(true);
			
			// does nothing ???
			scanSource.setColorMode();
			// int[] supported_filter = scanSource.getSupportedImageFilter();
			// double[] supported_res = scanSource.getSupportedXResolution();
			// double max_supported = 0;
			// for (int i = 0; i < supported_res.length; i++) {
			//     if (max_supported < supported_res[i]) {max_supported = supported_res[i];}
			// }
			scanSource.setResolution(config.scanDpi);
			// try and print resolution
			double x_res = scanSource.getXResolution();
			double y_res = scanSource.getYResolution();
			System.out.println("x_res: " + x_res + "    y_res: " + y_res + "\n");
			// scanSource.setUnits(TwainConstants.TWUN_PIXELS);
			// correct pixel coordinates, for testing
			// scanSource.setFrame(1260, 10751, 3667, 11981);
			// correct inch cooridates, seems to give correct area
			// defaults should be 1.05, 8.98, 3.05, 9.98
			scanSource.setFrame(config.scanX1, config.scanY1, config.scanX2, config.scanY2);
			// shows more of circle, for testing
			// scanSource.setFrame(.5, 8, 3.5, 11);

			// statement would be if config.getLightSource().equals(Config.lightSource)
			boolean configIndicator = false;
			if (configIndicator) {
				// seems to set scanner to transmissive mode
				scanSource.setLightPath(1);
			}//end if statement
			else {
				// seems to set scanner to reflective mode
				scanSource.setLightPath(0);
			}//end else statement
		}//end trying to set scan settings
		catch (Exception e) {
			return new SimpleResult<>(e);
		}//end catching any exceptions

		// if we've reached this point, we must be fine
		return new SimpleResult<>();
	}//end setScanSettings()

	/**
	 * Tries to run the scanner of the saved twain source with proper settings.
	 * @param filepath The file path to save scanned image to.
	 * @param imageFormat the TwainConstants.TWFF_ code for the format you want. One
	 * example would be TwainConstants.TWFF_BMP, which is equal to 2.
	 * @return Returns an error if one was thrown, otherwise is Ok.
	 */
	public SimpleResult<String> runScanner(String filepath, int imageFormat) {
		// make an attempt to run the scanner
		try {
			// figure out a temp path
			File file = new File(filepath);
			File tmp = file.toPath().getParent().resolve("tmp").toFile();

			// actually run scanner
			scanSource.acquireImage(false, tmp.getAbsolutePath(), imageFormat);
			// make sure the image is named correctly
			boolean renameSuccess = tmp.renameTo(file);
			if (!renameSuccess) {
				return new SimpleResult<>(new FileSystemException("We were unable to rename " + 
				"the scanned image. It might still exist, but it will be called tmp " + 
				"and overwritten in the next scan."));
			}//end if we couldn't rename the scanned file
			return new SimpleResult<>(filepath);
		}//end trying to run the scanner
		catch (Exception e) {
			return new SimpleResult<>(e);
		}//end catching any exceptions
	}//end runScanner()

	/**
	 * Closes the twain manager. This operation might fail, for some reason. If it does, an error will be returned with the result type.
	 * @return Returns an exception result if an exception returns. Otherwise, nothing useful is returned.
	 */
	public SimpleResult<SimpleResult.ResultType> closeScanner() {
		try {
			TwainManager.close();
		} catch (TwainException e) {
			return new SimpleResult<>(e);
		}//end catching and returning exceptions
		return new SimpleResult<>();
	}//end closeScanner()
}//end class Scan
