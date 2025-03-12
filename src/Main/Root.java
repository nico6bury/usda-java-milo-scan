package Main;

import java.io.File;
import java.nio.file.FileSystemException;
import java.rmi.UnexpectedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import IJM.IJProcess;
import IJM.SumResult;
import SK.gnome.twain.TwainConstants;
import Scan.Scan;
import SimpleResult.SimpleResult;
import Utils.Config;
import Utils.Constants;
import View.MainWindow;
import java.util.ArrayList;
import java.util.List;

public class Root implements Controller {
	/** The main window, holding the gui for the whole application. */
	private MainWindow mainWindow = new MainWindow(this);
	/** The scan object that we'll use for scanning */
	protected Scan scan = null;
	/** the last file scanned in by the program */
	private File lastScannedFile = null;
	/** The class which holds IJ processing functions. */
	private IJProcess ijProcess = new IJProcess();
	public IJProcess getIJProcess() {return ijProcess;}

	/** Class for handling config options */
	private Config config = new Config();
	public Config getConfig() {return config;}
	public void setConfig(Config c) {config = c;}
	/** Holds the images to eventually process. */
	private List<File> imageQueue = new ArrayList<File>();
	public List<File> getImageQueue() {return imageQueue;}
	/** Holds images that have been processed. */
	private List<File> processedImages = new ArrayList<File>();
	public List<File> getProcessedImages() {return processedImages;}

	public Root() {
		// read config files
		Config config = new Config();
		SimpleResult<String> config_result = config.read_config();
		if (config_result.isOk()) {
			this.config = config;
			// update dialog based on config
			mainWindow.thresholdDialog.thresholdToReturn = this.config.procThreshold;
			mainWindow.areaFlagDialog.firstFlag = this.config.areaThresholdLower;
			mainWindow.areaFlagDialog.secondFlag = this.config.areaThresholdUpper;
			mainWindow.unsharpDialog.unsharp_sigma = this.config.unsharpSigma;
			mainWindow.unsharpDialog.unsharp_weight = this.config.unsharpWeight;
			mainWindow.unsharpDialog.unsharp_skip = this.config.unsharpSkip;
			mainWindow.unsharpDialog.unsharp_rename = this.config.unsharpRename;
			mainWindow.scanAreaDialog.X1 = this.config.scanX1;
			mainWindow.scanAreaDialog.Y1 = this.config.scanY1;
			mainWindow.scanAreaDialog.X2 = this.config.scanX2;
			mainWindow.scanAreaDialog.Y2 = this.config.scanY2;
		}//end if we can read from config
		else {
			MainWindow.showGenericExceptionMessage(config_result.getError());
			JOptionPane.showMessageDialog(mainWindow, "Something went wrong while reading the config file. All settings have reverted to default.");
		}//end else something went wrong
	}//end constructor

	@Override
	public Object handleMessage(InterfaceMessage m, Object args) {
		System.out.println(mainWindow.getAlignmentX());
		System.out.println(m);
		switch (m) {
			case ConnectScanner:
				return connectScanner();
			case ResetScanner:
				return resetScanner();
			case Scan:
				Object[] argss = (Object[]) args;
				boolean shouldOverwriteName = (boolean) argss[0];
				String overwriteName = (String) argss[1];
				return performScan(overwriteName, shouldOverwriteName);
			case AddFilesToQueue:
				File[] filesToAdd = (File[]) args;
				addFilesToQueue(filesToAdd);
				return null;
			case ProcessQueue:
				processQueue();
				return null;
			case EmptyQueue:
				emptyQueue();
				return null;
			case EmptyOutput:
				emptyOutput();
				return null;
		}//end switching based on message
		return new UnexpectedException("unexpected interface message");
	}//end method HandleMessage()

	/**
	 * Tries to connect to the scanner.
	 * @return Returns true if connection succeeds, otherwise returns false.
	 */
	private boolean connectScanner() {
		if (scan != null) {
			JOptionPane.showMessageDialog(mainWindow, "A scanner is already connected. Please disconnect the current scanner before connecting a new one.", "Scanner already connected", JOptionPane.ERROR_MESSAGE);
			return false;
		}//end if scan isn't null
		
		scan = new Scan();
		// try to access the scanner source
		SimpleResult<SimpleResult.ResultType> initScannerResult = scan.initScanner();
		if (initScannerResult.isErr()) {
			MainWindow.showGenericExceptionMessage(initScannerResult.getError());
			// reset scanner to null
			scan = null;
			return false;
		}//end if we encountered an error while detecting the connected scanner
		else {return true;}
	}//end method connectScanner()

	/**
	 * Tries to disconnect the scanner.
	 * @return Returns true if disconnect was successful. Returns false is disconnect failed or scanner is already disconnected.
	 */
	private boolean resetScanner() {
		if (scan == null) {
			JOptionPane.showMessageDialog(mainWindow, "The scanner is already disconnected. It can't be reset further.");
			return false;
		} else {
			SimpleResult<SimpleResult.ResultType> closeResult = scan.closeScanner();
			if (closeResult.isErr()) {
				MainWindow.showGenericExceptionMessage(closeResult.getError());
				return false;
			}//end if closing scanner resulted in error
			else {return true;}
		}//end else we need to reset scanner connection
	}//end method resetScanner()

	/**
	 * This method performs the operation of scanning an image. 
	 * It then returns the resulting file if successful, or 
	 * the exception wrapped in a result if not.
	 * @return Returns a SimpleResult wrapped File.
	 */
	private SimpleResult<File> performScan(String filename, boolean shouldOverwriteName) {
		System.out.println("You clicked the \"Scan\" button.");
		if (scan == null || !scan.isScannerConnected()) { 
			return new SimpleResult<File>(new Exception("Scanner is not connected."));
		}
		// try to set scanner settings
		SimpleResult<SimpleResult.ResultType> setScanSettingResult = scan.setScanSettings(config);
		if (setScanSettingResult.isErr()) {
			MainWindow.showGenericExceptionMessage(setScanSettingResult.getError());
			// reset scan to null
			scan = null;
		}//end if we encountered an error while setting scan settings
		// try to scan something with the scanner
		SimpleResult<String> scanFilename_result = validateScanImageName(filename, shouldOverwriteName, config);
		if (scanFilename_result.isOk()) {
			SimpleResult<String> scanResult = scan.runScanner(scanFilename_result.getValue(),config.imageFormatCode);
			if (scanResult.isOk()) {
				String result = scanResult.getValue();
				config.numSuffixCurNum += config.numSuffixIncrement;
				if (config.unsharpSkip == true) {
					lastScannedFile = new File(result);
					return new SimpleResult<File>(lastScannedFile);
				}//end if we should just skip the unsharp process
				else {
					SimpleResult<String> unsharpResult = IJProcess.doUnsharpCorrection(
						result,
						config.unsharpSigma,
						config.unsharpWeight,
						config.unsharpRename
					);
					if (unsharpResult.isOk()) {
						lastScannedFile = new File(unsharpResult.getValue());
						return new SimpleResult<File>(lastScannedFile);
					}//end if we have an ok result
					else {
						return new SimpleResult<File>(unsharpResult.getError());
					}//end else we have an error to show
				}//end else we should do the unsharp correction
			}//end else if scan result is ok
			else {
				return new SimpleResult<File>(scanResult.getError());
			}//end if we have an error to show
		}//end if we got a filename ok
		else {
			return new SimpleResult<>(scanFilename_result.getError());
		}//end else something went wrong when getting filename
	}//end method performScan()

	/**
	 * Makes sure that we have a properly located filepath for a new scanned image,
	 * also making sure that we won't overwrite something if the user doesn't want it.
	 * @param filename The name of the file to save, such as "nice-image".
	 * @param shouldOverwriteName If this is true, then the filename param will be ignored and replaced with a timestamp.
	 * @param conf A set of configuration settings.
	 * @return Returns absolute path of a filename to scan to if successful, or an error if the scan should be postponed or something went wrong.
	 */
	private SimpleResult<String> validateScanImageName(String filename, boolean shouldOverwriteName, Config conf) {
		SimpleResult<File> outF_result = getBaseScanDir(filename, !shouldOverwriteName, conf);
		if (outF_result.isOk()) {
			File outF = outF_result.getValue();
			if (outF.exists() && !shouldOverwriteName) {
				String msg = "The file \"" + outF.getName() + "\" already exists.\n" +
					"Are you sure you want to overwrite it?";
				String title = "Confirmation for Overwrite";
				int response = JOptionPane.showConfirmDialog(mainWindow,msg,title,JOptionPane.YES_NO_OPTION);
				if (response != JOptionPane.YES_OPTION) {
					return new SimpleResult<>(new Exception("User cancelled operation."));
				}//end if user doesn't want to overwrite file
				else {
					boolean deleteSuccess = outF.delete();
					if (!deleteSuccess) {
						return new SimpleResult<>(new FileSystemException("Couldn't delete file. Is it open elsewhere or otherwise busy?"));
					}//end if we couldn't delete old file
				}//end else user does want to overwrite file
			}//end if the file already exists
			// if we get here, we should be good
			return new SimpleResult<>(outF.getAbsolutePath());
		}//end if we successfully got a filename
		else {
			return new SimpleResult<>(outF_result.getError());
		}//end else we couldn't determine a filename
	}//end validateScanImageName()

	public SimpleResult<List<String>> getSupportedImageFormats() {
		if (scan != null && scan.isScannerConnected()) {
			try {
				int[] imageFormatCodes = scan.getSource().getSupportedImageFileFormat();
				List<String> imageFormats = new ArrayList<String>();
				for (int code : imageFormatCodes) {
					if (code == TwainConstants.TWFF_BMP) {
						imageFormats.add("TWFF_BMP");
					} if (code == TwainConstants.TWFF_PNG) {
						imageFormats.add("TWFF_PNG");
					}  if (code == TwainConstants.TWFF_EXIF) {
						imageFormats.add("TWFF_EXIF");
					} if (code == TwainConstants.TWFF_FPX) {
						imageFormats.add("TWFF_FPX");
					} if (code == TwainConstants.TWFF_JFIF) {
						imageFormats.add("TWFF_JFIF");
					} if (code == TwainConstants.TWFF_PICT) {
						imageFormats.add("TWFF_PICT");
					} if (code == TwainConstants.TWFF_SPIFF) {
						imageFormats.add("TWFF_SPIFF");
					} if (code == TwainConstants.TWFF_TIFF) {
						imageFormats.add("TWFF_TIFF");
					} if (code == TwainConstants.TWFF_TIFFMULTI) {
						imageFormats.add("TWFF_TIFFMULTI");
					} if (code == TwainConstants.TWFF_XBM) {
						imageFormats.add("TWFF_XBM");
					}
					System.out.println(code);
				}
				System.out.println("imageFormatCodes.length " + imageFormatCodes.length);
				System.out.println("imageFormats.size() " + imageFormats.size());
				return new SimpleResult<>(imageFormats);
			} catch (Exception e) {
				return new SimpleResult<>(e);
			}//end catching any Exceptions that could pop out
		} else {return new SimpleResult<>(new Exception("Scanner is not connected."));}
	}//end getSupportedImageFormats()

	/**
	 * Gets the supported X Resulution for the scanner, which for our
	 * purposes, should be the list of supported DPI settings.
	 * @return Returns result with all dpi settings supported (according to
	 * the scanner) or an error if something went wrong.
	 */
	public SimpleResult<double[]> getSupportedXDpi() {
		if (scan != null && scan.isScannerConnected()) {
			try {
				return new SimpleResult<>(scan.getSource().getSupportedXResolution());
			} catch (Exception e) {
				return new SimpleResult<>(e);
			}//end catching any Exceptions that could pop out
		} else {return new SimpleResult<>(new Exception("Scanner is not connected."));}
	}//end getSupportedXDpi()

	/**
	 * Gets the file location at which we should save the next scanned file.  
	 * The filename and directory is based on timestamping, and things are placed into
	 * the directory at Constants.SCANNED_IMAGES_FOLDER_NAME.
	 * @param filename An optional filename to use instead of a timestamp
	 * @param use_filename Whether to use filename parameter (true) or ignore it and use timestamp (false)
	 * @param conf A set of configuration settings.
	 * @return Returns a File at which to save a file, or an error if an exception happened.
	 */
	private SimpleResult<File> getBaseScanDir(String filename, boolean use_filename, Config conf) {
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
			DateTimeFormatter sec = DateTimeFormatter.ofPattern("s");
			// DateTimeFormatter dir_formatter = DateTimeFormatter.ofPattern("yyyy-MM");
			// DateTimeFormatter file_formatter = DateTimeFormatter.ofPattern("MM-d_H:m");
			String directory_string = 
				output_folder_storage_file.getAbsolutePath() +
				File.separator +
				"petri-scan-" +
				currentDateTime.format(year) +
				"-" + currentDateTime.format(month);
			File newDirectory = new File(directory_string);
			if (conf.scanSubdirEnabled) {
				try {
					directory_string += File.separator + conf.scanSubdirName;
					newDirectory = new File(directory_string);
				} catch (Exception e) {
					System.err.println("Couldn't locate subdirectory for scan. Encountered exception " +
					e.toString() + " with message " + e.getMessage() + " and stack trace\n" + e.getStackTrace());
				}//end catching any exceptions that might occur from malformed config
			}//end if we want to output to a subdirectory
			// create the directory if it doesn't exist
			if (!newDirectory.exists()) {
				newDirectory.mkdir();
			}//end if new directory needs to be created
			String newExtension = "";
			if (conf.imageFormatCode == 0 || conf.imageFormatCode == 6) {newExtension = ".tif";}
			else if (conf.imageFormatCode == 2) {newExtension = ".bmp";}
			else if (conf.imageFormatCode == 7) {newExtension = ".png";}
			String current_time_stamp = currentDateTime.format(month) +
				"-" + currentDateTime.format(day) +
				"_" + currentDateTime.format(hour) +
				";" + currentDateTime.format(min) +
				";" + currentDateTime.format(sec);
			String numberSuffix = "";
			if (conf.numSuffixEnabled) {
				String number = "" + conf.numSuffixCurNum;
				StringBuilder extra_digits = new StringBuilder();
				while (number.length() + extra_digits.length() < conf.numSuffixMinDigits) {
					extra_digits.append(0);
				}
				numberSuffix = extra_digits.toString() + number;
			}//end if number suffix feature is enabled
			String newFileName;
			if (use_filename) {newFileName = filename + conf.scanSuffix + numberSuffix + newExtension;}
			else {newFileName = current_time_stamp + conf.scanSuffix + numberSuffix + newExtension;}
			File outputFile = new File(newDirectory.getAbsolutePath() + File.separator + newFileName);

			return new SimpleResult<File>(outputFile);
		} catch (Exception e) {return new SimpleResult<File>(e);}
	}//end getBaseScanDir()

	/**
	 * Adds the selected files to the image queue, awaiting processing. 
	 * @param files The files to add to the processing queue.
	 */
	private void addFilesToQueue(File[] files) {
		for (File file : files) {
			imageQueue.add(file);
		}//end looping over each file in files
		// update display
		mainWindow.updateQueueList();
	}//end method addFilesToQueue

	/**
	 * Empties the image processing queue and updates the display.
	 */
	private void emptyQueue() {
		imageQueue.clear();
		mainWindow.updateQueueList();
		mainWindow.updateImageDisplay("%=empty");
	}//end method emptyQueue

	/**
	 * Empties the list of processed images and updates the display.
	 */
	private void emptyOutput() {
		processedImages.clear();
		mainWindow.updateOutputTable(new ArrayList<List<SumResult>>());
		mainWindow.updateImageDisplay("%=empty");
	}//end method emptyOutput

	/**
	 * Starts the image processing on all images in the image queue,
	 * then moves finsihed images into the processedImages list.
	 */
	private void processQueue() {
		System.out.println("Starting to gather data for processing, returning to view.");
	}//end method processQueue

	/**
	 * The method called by the imagej task once processing
	 * finishes.
	 * @param outputData The data output by the ij task.
	 */
	public void postProcessHandling(SimpleResult<String> outputData) {
		if (outputData.isErr()) {
			outputData.getError().printStackTrace();
			MainWindow.showGenericExceptionMessage(outputData.getError());
			return;
		}//end if we just got an error
		
	}//end method postProcessHandling
}//end class Root
