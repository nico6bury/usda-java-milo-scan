package Scan;

import SK.gnome.twain.TwainConstants;
import SK.gnome.twain.TwainException;
import SK.gnome.twain.TwainManager;
import SK.gnome.twain.TwainSource;
import Utils.Config;
import Utils.Constants;
import SimpleResult.SimpleResult;
import SimpleResult.SimpleResult.ResultType;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import IJM.IJProcess;

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
			scanSource.setResolution(600);
			// try and print resolution
			double x_res = scanSource.getXResolution();
			double y_res = scanSource.getYResolution();
			System.out.println("x_res: " + x_res + "    y_res: " + y_res + "\n");
			// scanSource.setUnits(TwainConstants.TWUN_PIXELS);
			// correct pixel coordinates, for testing
			// scanSource.setFrame(1260, 10751, 3667, 11981);
			// correct inch cooridates, seems to give correct area
			// defaults should be 1.05, 8.98, 3.05, 9.98
			scanSource.setFrame(config.scan_x1, config.scan_y1, config.scan_x2, config.scan_y2);
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
	 * @param filename An optional filename to use instead of a timestamp
	 * @param use_filename Whether to use filename parameter (true) or ignore it and use timestamp (false)
	 * @return Returns an error if one was thrown, otherwise is Ok.
	 */
	public SimpleResult<String> runScanner(String filename, boolean use_filename) {
		// make an attempt to run the scanner
		try {
			// determine file path where image will be outputted
			SimpleResult<File> outF_result = getBaseScanDir(filename, use_filename);
			SimpleResult<File> tmpF_result = getBaseScanDir(filename,false);
			if (outF_result.isOk() && tmpF_result.isOk()) {
				File outF = outF_result.getValue();
				File tmpF = tmpF_result.getValue();
				System.out.println(outF.getAbsolutePath());
				// scan and save image to filepath
				scanSource.acquireImage(false, tmpF.getAbsolutePath(), TwainConstants.TWFF_TIFF);
				if (use_filename) {
					tmpF.renameTo(outF);
				}//end if we want to have the image file be named what the user wanted
				// ImageIO.write(bimg, "bmp", outF);
				return new SimpleResult<>(outF.getAbsolutePath());
			}//end if we successfully got a filename
			else {
				return new SimpleResult<>(outF_result.getError());
			}//end else we got an error
		}//end trying to run the scanner
		catch (Exception e) {
			return new SimpleResult<>(e);
		}//end catching any exceptions
	}//end runScanner()

	/**
	 * Gets the file location at which we should save the next scanned file.  
	 * The filename and directory is based on timestamping, and things are placed into
	 * the directory at Constants.SCANNED_IMAGES_FOLDER_NAME.
	 * @param filename An optional filename to use instead of a timestamp
	 * @param use_filename Whether to use filename parameter (true) or ignore it and use timestamp (false)
	 * @return Returns a File at which to save a file, or an error if an exception happened.
	 */
	public static SimpleResult<File> getBaseScanDir(String filename, boolean use_filename) {
		String jar_location;
		try {
			jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			String output_folder_storage = jar_location + File.separator + Constants.SCANNED_IMAGES_FOLDER_NAME;
			File output_folder_storage_file = new File(output_folder_storage);
			if (!output_folder_storage_file.exists()) {
				output_folder_storage_file.mkdir();
			}//end if we need to make our output folder
			
			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
			DateTimeFormatter month = DateTimeFormatter.ofPattern("MM");
			DateTimeFormatter day = DateTimeFormatter.ofPattern("d");
			DateTimeFormatter hour = DateTimeFormatter.ofPattern("H");
			DateTimeFormatter min = DateTimeFormatter.ofPattern("m");
			// DateTimeFormatter dir_formatter = DateTimeFormatter.ofPattern("yyyy-MM");
			// DateTimeFormatter file_formatter = DateTimeFormatter.ofPattern("MM-d_H:m");
			File newDirectory = new File(output_folder_storage_file.getAbsolutePath() + File.separator + "milo-scan-" + currentDateTime.format(year) + "-" + currentDateTime.format(month));
			// create the directory if it doesn't exist
			if (!newDirectory.exists()) {
				newDirectory.mkdir();
			}//end if new directory needs to be created
			String newExtension = ".tif";
			String current_time_stamp = currentDateTime.format(month) + "-" + currentDateTime.format(day) + "_" + currentDateTime.format(hour) + ";" + currentDateTime.format(min);
			String newFileName;
			if (use_filename) {newFileName = filename + newExtension;}
			else {newFileName = current_time_stamp + newExtension;}
			File outputFile = new File(newDirectory.getAbsolutePath() + File.separator + newFileName);

			return new SimpleResult<File>(outputFile);
		} catch (Exception e) {return new SimpleResult<File>(e);}
	}//end getBaseScanDir()

	/**
	 * Closes the twain manager. This operation might fail, for some reason. If it does, an error will be returned with the result type.
	 * @return Returns an exception result if an exception returns. Otherwise, nothing useful is returned.
	 */
	public SimpleResult<ResultType> closeScanner() {
		try {
			TwainManager.close();
		} catch (TwainException e) {
			return new SimpleResult<>(e);
		}//end catching and returning exceptions
		return new SimpleResult<>();
	}//end closeScanner()
}//end class Scan
