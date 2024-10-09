package Main;

import java.io.File;
import java.rmi.UnexpectedException;

import javax.swing.JOptionPane;

import IJM.IJProcess;
import IJM.SumResult;
import Scan.Scan;
import Utils.ConfigScribe;
import Utils.ConfigScribe.PairedConfigStores;
import Utils.ConfigStoreC;
import Utils.ConfigStoreH;
import Utils.Result;
import Utils.Result.ResultType;
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

	/** Class for handling serialization of config options */
	private ConfigScribe configScribe = new ConfigScribe();
	public ConfigScribe getConfigScribe() {return configScribe;}
	/** Class for storing settings of human-readable config values */
	private ConfigStoreH configStoreH = new ConfigStoreH();
	public ConfigStoreH getConfigStoreH() {return configStoreH;}
	public void setConfigStoreH(ConfigStoreH c) {configStoreH = c;}
	/** Class for storing settings of non-human-readable config values */
	private ConfigStoreC configStoreC = new ConfigStoreC();
	public ConfigStoreC getConfigStoreC() {return configStoreC;}
	/** Holds the images to eventually process. */
	private List<File> imageQueue = new ArrayList<File>();
	public List<File> getImageQueue() {return imageQueue;}
	/** Holds images that have been processed. */
	private List<File> processedImages = new ArrayList<File>();
	public List<File> getProcessedImages() {return processedImages;}

	public Root() {
		// read config files
		Result<PairedConfigStores> config_result =  configScribe.read_config();
		if (config_result.isOk()) {
			this.configStoreH = config_result.getValue().configStoreH;
			this.configStoreC = config_result.getValue().configStoreC;
			// update dialog based on config
			mainWindow.thresholdDialog.thresholdToReturn = this.configStoreH.proc_threshold;
			mainWindow.areaFlagDialog.firstFlag = this.configStoreH.area_threshold_lower;
			mainWindow.areaFlagDialog.secondFlag = this.configStoreH.area_threshold_upper;
			mainWindow.unsharpDialog.unsharp_sigma = this.configStoreH.unsharp_sigma;
			mainWindow.unsharpDialog.unsharp_weight = this.configStoreH.unsharp_weight;
			mainWindow.unsharpDialog.unsharp_skip = this.configStoreH.unsharp_skip;
			mainWindow.unsharpDialog.unsharp_rename = this.configStoreH.unsharp_rename;
			mainWindow.scanAreaDialog.X1 = this.configStoreH.scan_x1;
			mainWindow.scanAreaDialog.Y1 = this.configStoreH.scan_y1;
			mainWindow.scanAreaDialog.X2 = this.configStoreH.scan_x2;
			mainWindow.scanAreaDialog.Y2 = this.configStoreH.scan_y2;
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
				return performScan();
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
		Result<ResultType> initScannerResult = scan.initScanner();
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
			Result<ResultType> closeResult = scan.closeScanner();
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
	 * @return Returns a Result wrapped File.
	 */
	private Result<File> performScan() {
		System.out.println("You clicked the \"Scan\" button.");
		if (scan == null || !scan.isScannerConnected()) { 
			return new Result<File>(new Exception("Scanner is not connected."));
		}
		// try to set scanner settings
		Result<ResultType> setScanSettingResult = scan.setScanSettings(configStoreH);
		if (setScanSettingResult.isErr()) {
			MainWindow.showGenericExceptionMessage(setScanSettingResult.getError());
			// reset scan to null
			scan = null;
		}//end if we encountered an error while setting scan settings
		// try to scan something with the scanner
		Result<String> scanResult = scan.runScanner(mainWindow.uxOverwriteName.getText(), !mainWindow.uxShouldOverwriteName.isSelected());
		if (scanResult.isOk()) {
			String result = scanResult.getValue();
			if (configStoreH.unsharp_skip == true) {
				lastScannedFile = new File(result);
				return new Result<File>(lastScannedFile);
			}//end if we should just skip the unsharp process
			else {
				Result<String> unsharpResult = IJProcess.doUnsharpCorrection(
					result,
					configStoreH.unsharp_sigma,
					configStoreH.unsharp_weight,
					configStoreH.unsharp_rename
				);
				if (unsharpResult.isOk()) {
					lastScannedFile = new File(unsharpResult.getValue());
					return new Result<File>(lastScannedFile);
				}//end if we have an ok result
				else {
					return new Result<File>(unsharpResult.getError());
				}//end else we have an error to show
			}//end else we should do the unsharp correction
		}//end else if scan result is ok
		else {
			return new Result<File>(scanResult.getError());
		}//end if we have an error to show
	}//end method performScan()

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
	public void postProcessHandling(Result<String> outputData) {
		if (outputData.isErr()) {
			outputData.getError().printStackTrace();
			MainWindow.showGenericExceptionMessage(outputData.getError());
			return;
		}//end if we just got an error
		
	}//end method postProcessHandling
}//end class Root
