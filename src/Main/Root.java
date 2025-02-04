package Main;

import java.io.File;
import java.rmi.UnexpectedException;

import javax.swing.JOptionPane;

import IJM.IJProcess;
import IJM.SumResult;
import Scan.Scan;
import SimpleResult.SimpleResult;
import Utils.Config;
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
	private SimpleResult<File> performScan() {
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
		SimpleResult<String> scanResult = scan.runScanner(mainWindow.uxOverwriteName.getText(), !mainWindow.uxShouldOverwriteName.isSelected());
		if (scanResult.isOk()) {
			String result = scanResult.getValue();
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
	public void postProcessHandling(SimpleResult<String> outputData) {
		if (outputData.isErr()) {
			outputData.getError().printStackTrace();
			MainWindow.showGenericExceptionMessage(outputData.getError());
			return;
		}//end if we just got an error
		
	}//end method postProcessHandling
}//end class Root
