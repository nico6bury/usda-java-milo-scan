package IJM;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import IJM.Constants.PassOrNot;
import IJM.RoiGrid.RoiImageOutputConfiguration;
import Utils.Constants;
import SimpleResult.SimpleResult;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.plugin.frame.RoiManager;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

/**
 * This class keeps track of everything related to running files through the imagej milo macros.
 * It also handles all the calls to imagej.
 * @author Nicholas Sixbury
 */
public class IJProcess {
	/**
	 * Should be the directory where the jar is located
	 */
	// File base_macro_dir;
	// File base_macro_file;
	// default lower size limit for analyze particles
	int szMin = 2;
	// default upper size limit for analyze particles
	int defSizeLimit = 1000;
	// upper threshold for analyze particles
	public int th01 = 160;
	public List<SumResult> lastProcResult;
	public double lower_flag_thresh = 0.05;
	public double upper_flag_thresh = 0.10;
	public boolean shouldOutputKernImages = false;

	/**
	 * Constructs the class
	 */
	public IJProcess() {
		// String macro_folder = jar_location + File.separator + "macros";
		// base_macro_dir = new File(macro_folder);
		// String macro_file = macro_folder + File.separator + "NS-FlourScan-Main.ijm";
		// base_macro_file = new File(macro_file);
	}//end constructor

	/**
	 * This method does all the necessary fileIO to determine the path for an output file. 
	 * @param ensureDirectoryExists If this is true, then this method will create a new directory if it doesn't already exist.
	 * @return Returns a resulting path as a File if successful. Otherwise, returns the exception that prevented success.
	 */
	public SimpleResult<File> getOutputFilePath(boolean ensureDirectoryExists) {
		// get path of the jar as base directory
		String jar_location;
		try {
			jar_location = new File(IJProcess.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString();
			String output_folder_storage = jar_location + File.separator + Constants.IMAGEJ_OUTPUT_FOLDER_NAME;
			File output_folder_storage_file = new File(output_folder_storage);
			if (!output_folder_storage_file.exists() && ensureDirectoryExists) {
				output_folder_storage_file.mkdir();
			}//end if we need to make our output folder

			LocalDateTime currentDateTime = LocalDateTime.now();
			DateTimeFormatter year = DateTimeFormatter.ofPattern("yyyy");
			DateTimeFormatter month = DateTimeFormatter.ofPattern("MMM");
			DateTimeFormatter day = DateTimeFormatter.ofPattern("dd");
			DateTimeFormatter hour = DateTimeFormatter.ofPattern("kk");
			DateTimeFormatter min = DateTimeFormatter.ofPattern("mm");
			DateTimeFormatter sec = DateTimeFormatter.ofPattern("ss");
			// DateTimeFormatter dir_formatter = DateTimeFormatter.ofPattern("yyyy-MM");
			// DateTimeFormatter file_formatter = DateTimeFormatter.ofPattern("MM-d_H:m");
			File newDirectory = new File(output_folder_storage_file.getAbsolutePath() + File.separator + "milo-scan-" + currentDateTime.format(year) + "-" + currentDateTime.format(month));
			// create the directory if it doesn't exist
			if (ensureDirectoryExists && !newDirectory.exists()) {
				newDirectory.mkdir();
			}//end if new directory needs to be created
			String newExtension = ".OUT.csv";
			String current_time_stamp = currentDateTime.format(month) + "-" + currentDateTime.format(day) + "_" + currentDateTime.format(hour) + ";" + currentDateTime.format(min) + ";" + currentDateTime.format(sec);
			String newFileName = current_time_stamp + newExtension;
			File outputFile = new File(newDirectory.getAbsolutePath() + File.separator + newFileName);

			return new SimpleResult<File>(outputFile);
		} catch (Exception e) {
			return new SimpleResult<File>(e);
		}//end catching any exceptions
	}//end getOutputFilePath(ensureDirectoryExists)

	/**
	 * Builds all the text for the output file, and then writes that text to a file. 
	 * Uses path from IJProcess.getOutputFilePath().
	 * @param inputList The list of SumResult objects that represent the processed data. This will be formatted and written to the file.
	 * @return Returns a result containing the full string output, or a wrapped error if something prevents the output file from being written.
	 */
	public SimpleResult<String> makeOutputFile(List<SumResult> inputList) {
		// save to output file
		SimpleResult<File> outputFileResult = getOutputFilePath(true);
		if (outputFileResult.isErr()) {
			return new SimpleResult<>(outputFileResult.getError());
		}//end if we couldn't get output file path
		// otherwise, continue as normal
		File outputFile = outputFileResult.getValue();

		if (!outputFile.exists()) {
			outputFile.getParentFile().mkdirs();
		}//end if we need to make the resulting directories
		PrintWriter pw;
		try {
			pw = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			return new SimpleResult<>(e);
		}//end catching FileNotFoundExceptions

		// print first section of header
		pw.printf("%s  %s\n%s\n", Constants.PROGRAM_NAME, Constants.VERSION, Constants.LOCATION + "    " + Constants.DATE() + "    " + Constants.PEOPLE);

		// print second section of header
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		pw.printf("Data Processed: %s\n", 
				dateFormat.format(cal.getTime()));

		// print third line of header
		// pw.printf("flag lower threshold: %f     flag upper threshold: %f\n", lower_flag_thresh, upper_flag_thresh);

		// print a little key for my abbreviations
		pw.print("Abbreviation Key: K => Kernel | V => Vitreous Endosperm | C => Chalky Endosperm | G => Germ\n");

		// print headers for the columns we're about to print
		pw.print(
			"FileID,GridIdx" + 
			",KPixels,VPixels,CPixels,GPixels,X-Sec_Pixels,CGPixels" + 
			",V%Area,C%Area,G%Area,CG%Area" + 

			",KX,KY" + 
			",VX,VY" + 
			",CX,CY" + 
			",GX,GY" + 
			
			",KBX,KBY,KWidth,KHeight" + 
			",VBX,VBY,VWidth,VHeight" + 
			",CBX,CBY,CWidth,CHeight" + 
			",GBX,GBY,GWidth,GHeight" +
			
			",KPerim" +
			",VPerim" +
			",CPerim" +
			",GPerim" +
			
			",KCirc,KRound,KAR,KSolidity" +
			",VCirc,VRound,VAR,VSolidity" +
			",CCirc,CRound,CAR,CSolidity" +
			",GCirc,GRound,GAR,GSolidity" +
			"\n");
		
		StringBuilder data_output = new StringBuilder();
		// build timestamp for output file
		// LocalDateTime curDateTime = LocalDateTime.now();
		// DateTimeFormatter timestamp = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");
		// group sumresults into our grouped lists
		// List<List<SumResult>> groupedResults = SumResult.groupResultsByFile(inputList);
		// build output for all the images processed
		for(int i = 0; i < inputList.size(); i++) {
			SumResult res = inputList.get(i);
			String filename_no_ext = res.file.getName();
			if (filename_no_ext.contains(".")) {
				int last_dot_idx = filename_no_ext.lastIndexOf('.');
				filename_no_ext = filename_no_ext.substring(0, last_dot_idx);
			}//end if we should remove the file extension
			double total_area = res.getResValSentinel("Area");
			// double endosperm_area = res.getResValSentinel("EndospermArea");
			// double endo_percent = (endosperm_area * 100) / total_area;
			// double vit_area = res.getResValSentinel("VitreousArea");
			double chk_area = res.getResValSentinel("ChalkArea");
			// double grm_area = res.getResValSentinel("GermArea");
			double cross_section_area = res.getResValSentinel("CrossSectionArea");
			double chkgrm_area = res.getResValSentinel("ChalkGermArea");
			double chk_perc = (chk_area * 100) / cross_section_area;
			double chkgrm_perc = (chkgrm_area * 100) / cross_section_area;
			double grm_area = chkgrm_area - chk_area;
			double vit_area = cross_section_area - chkgrm_area;
			double vit_perc = (vit_area * 100) / cross_section_area;
			double grm_perc = (grm_area * 100) / cross_section_area;

			double kx = res.rrr.roi.getBounds().getCenterX();
			double ky = res.rrr.roi.getBounds().getCenterY();
			
			// double ex = res.getResValSentinel("EndospermX") + res.rrr.roi.getBounds().getX();
			// double ey = res.getResValSentinel("EndospermY") + res.rrr.roi.getBounds().getY();
			double vx = res.rrr.roi.getBounds().getCenterX();
			double vy = res.rrr.roi.getBounds().getCenterY();
			double cx = res.rrr.roi.getBounds().getCenterX();
			double cy = res.rrr.roi.getBounds().getCenterY();
			double gx = res.rrr.roi.getBounds().getCenterX();
			double gy = res.rrr.roi.getBounds().getCenterY();

			int kbx = res.rrr.roi.getBounds().x;
			int kby = res.rrr.roi.getBounds().y;
			int kw = res.rrr.roi.getBounds().width;
			int kh = res.rrr.roi.getBounds().height;

			// double ebx = res.getResValSentinel("EndospermBX") + kbx;
			// double eby = res.getResValSentinel("EndospermBY") + kby;
			// double ew = res.getResValSentinel("EndospermWidth");
			// double eh = res.getResValSentinel("EndospermHeight");
			double vbx = res.getResValSentinel("VitreousBX") + kbx;
			double vby = res.getResValSentinel("VitreousBY") + kby;
			double vw = res.getResValSentinel("VitreousWidth");
			double vh = res.getResValSentinel("VitreousHeight");
			double cbx = res.getResValSentinel("ChalkBX") + kbx;
			double cby = res.getResValSentinel("ChalkBY") + kby;
			double cw = res.getResValSentinel("ChalkWidth");
			double ch = res.getResValSentinel("ChalkHeight");
			double gbx = res.getResValSentinel("GermBX") + kbx;
			double gby = res.getResValSentinel("GermBY") + kby;
			double gw = res.getResValSentinel("GermWidth");
			double gh = res.getResValSentinel("GermHeight");

			double kperim = res.getResValSentinel("KernPerim.");

			// double eperim = res.getResValSentinel("EndospermPerim.");
			double vperim = res.getResValSentinel("VitreousPerim.");
			double cperim = res.getResValSentinel("ChalkPerim.");
			double gperim = res.getResValSentinel("GermPerim.");

			double kcirc = res.getResValSentinel("KernCirc.");
			double kround = res.getResValSentinel("KernRound");
			double kar = res.getResValSentinel("KernAR");
			double ksolidity = res.getResValSentinel("KernSolidity");

			// double ecirc = res.getResValSentinel("EndospermCirc.");
			// double eround = res.getResValSentinel("EndospermRound");
			// double ear = res.getResValSentinel("EndospermAR");
			// double esolidity = res.getResValSentinel("EndospermSolidity");
			double vcirc = res.getResValSentinel("VitreousCirc.");
			double vround = res.getResValSentinel("VitreousRound");
			double var = res.getResValSentinel("VitreousAR");
			double vsolidity = res.getResValSentinel("VitreousSolidity");
			double ccirc = res.getResValSentinel("ChalkCirc.");
			double cround = res.getResValSentinel("ChalkRound");
			double car = res.getResValSentinel("ChalkAR");
			double csolidity = res.getResValSentinel("ChalkSolidity");
			double gcirc = res.getResValSentinel("GermCirc.");
			double ground = res.getResValSentinel("GermRound");
			double gar = res.getResValSentinel("GermAR");
			double gsolidity = res.getResValSentinel("GermSolidity");

			data_output.append(String.format(
				"%s,%d" + 
				",%3.1f,%3.1f,%3.1f,%3.1f,%3.1f,%3.1f" +
				",%3.1f,%3.1f,%3.1f,%3.1f" +

				",%f,%f" +
				",%f,%f" +
				",%f,%f" +
				",%f,%f" +
				
				",%d,%d,%d,%d" +
				",%f,%f,%f,%f" +
				",%f,%f,%f,%f" +
				",%f,%f,%f,%f" +
				
				",%f" +
				",%f" +
				",%f" +
				",%f" +
				
				",%f,%f,%f,%f" +
				",%f,%f,%f,%f" +
				",%f,%f,%f,%f" +
				",%f,%f,%f,%f" +
				"\n",

				filename_no_ext, res.rrr.gridCellIdx + 1,
				total_area,vit_area,chk_area,grm_area,cross_section_area,chkgrm_area,
				vit_perc,chk_perc,grm_perc,chkgrm_perc,

				kx,ky,
				vx,vy,
				cx,cy,
				gx,gy,
				kbx,kby,kw,kh,
				vbx,vby,vw,vh,
				cbx,cby,cw,ch,
				gbx,gby,gw,gh,
				kperim,
				vperim,
				cperim,
				gperim,
				kcirc,kround,kar,ksolidity,
				vcirc,vround,var,vsolidity,
				ccirc,cround,car,csolidity,
				gcirc,ground,gar,gsolidity
			));
		}//end making the output string
		
		// print output for all images
		pw.print(data_output.toString());

		// close output file
		pw.close();

		return new SimpleResult<>(data_output.toString());
	}//end makeOutputFile(inputList, icl)

	/**
	 * This method performs the unsharp mask filter for files scanned through twain instead of the EPSON Scan Utility.  
	 * By default, this method will save the new image next to the original, with "-unsharp_sigma-[sigma]_weight-[weight]" appended to the filename.
	 * @param filepath The filepath of the image to process
	 * @param sigma The sigma (radius) value to use for the unsharp filter
	 * @param weight The mask weight to use for the unsharp filter. It must be between 0.1 and 0.9
	 * @param rename_file Whether or not we should rename the unsharp masked file.
	 * @return Returns either the filepath for the resulting file, or some error.
	 */
	public static SimpleResult<String> doUnsharpCorrection(String filepath, double sigma, double weight, boolean rename_file) {
		// open img and run the unsharp mask
		ImagePlus img = IJ.openImage(filepath);
		IJ.run(img, "Unsharp Mask...", "radius=" + sigma + " mask=" + weight);
		// figure out path to save img to, then save it there
		String baseDir = filepath.substring(0, filepath.lastIndexOf(File.separator) + 1);
		String baseName = filepath.substring(filepath.lastIndexOf(File.separator) + 1, filepath.lastIndexOf("."));
		String baseExt = filepath.substring(filepath.lastIndexOf("."));
		
		String newName;
		if (rename_file) {
			newName = baseName + String.format("_unsharp_sigma-[%2.1f]_weight-[%2.1f]", sigma, weight);
		} else {
			newName = baseName;
		}//end else we don't rename the file
		
		String newPath = baseDir + newName + baseExt;

		IJ.save(img, newPath);
		// return filename as string, probably
		return new SimpleResult<String>(newPath);
	}//end doUnsharpCorrection(filepath, sigma, weight)

	/**
	 * Runs the code to process a list of images. Actually just passes everything to IJProcess.MainMacro()
	 * @param files_to_process The list of image Files to process.
	 * @return Returns a result that will contain the full string written to an output file, or an error if something prevented completion.
	 */
	public SimpleResult<String> runMacro(List<File> files_to_process) {
		try {
			return MainMacro(files_to_process);
		} catch (Exception e) {
			return new SimpleResult<>(e);
		}//end if we catch any exceptions
	}//end runMacro()

	/**
	 * The main method for organizing and processing images.
	 * @param files_to_process The list of image Files to process
	 * @return Returns a result that will contain the full string written to an output file, or an error if something prevented completion.
	 */
	public SimpleResult<String> MainMacro(List<File> files_to_process) {
		List<SumResult> runningSum = new ArrayList<SumResult>();
		for (int i = 0; i < files_to_process.size(); i++) {
			File file = files_to_process.get(i);
			// String sliceBase = file.getName().substring(0, file.getName().length() - 4);

			System.out.println("\n\nGetting ready to process image file \"" + file.getAbsolutePath() + "\"\n");

			// figure out some directory stuff if we're doing roi image output
			RoiGrid.RoiImageOutputConfiguration riocBase = null;
			if (shouldOutputKernImages) {
				File baseDirectory = file.getParentFile();
				String newFolderName = file.getName().substring(0,file.getName().lastIndexOf(".")) + "-kern-imgs";
				File baseDir = new File(baseDirectory, newFolderName);
				for (int d = 0; baseDir.exists(); d++) {
					baseDir = new File(baseDirectory, newFolderName + "-" + d);
				}//end ensuring that we have a new directory
				boolean createDirRes = baseDir.mkdirs();
				System.out.println("Base directory provided for roi image ouput: " + baseDirectory.getAbsolutePath());
				System.out.println("New folder for kern-output: " + baseDir.getAbsolutePath());
				System.out.println("Successfully created directory?: " + createDirRes);
				riocBase = new RoiImageOutputConfiguration(baseDir);
			}//end if we want to output kernel images

			// actually start processing
			ImagePlus this_image = IJ.openImage(file.getAbsolutePath());
			this_image.getProcessor().flipHorizontal();

			// get location of grid cells
			ArrayList<Roi[]> gridCells = getGridCells(this_image);

			// remove grid and background, get whole kernels
			RoiGrid kernGrid = getRoiGrid(this_image, riocBase);

			// Update kernelGrid with gridCells information
			kernGrid.updateGridLocs(gridCells);

			// Get procs from endosperm(white) area
			// procEndosperm(kernGrid, this_image);

			// Get three parts of kernel area
			// procThreeParts(kernGrid, this_image, riocBase);

			// Use new processing
			danProcMar2025(kernGrid, this_image, riocBase);

			this_image.close();

			// add all the results and stuff we got
			runningSum.addAll(SumResult.sortSumResultList(SumResult.fromRoiGrid(file, kernGrid)));
		}//end looping over each file we want to split

		// output the output file
		SimpleResult<String> outputFileResult = makeOutputFile(runningSum);
		lastProcResult = runningSum;
		// return the rows of data that wil show up in the output file
		return outputFileResult;
	}//end Main Macro converted from ijm

	/**
	 * Mutates rg, doesn't mutate image.
	 * Tries to threshold out and get information on vitreous endopserm,
	 * chalky endosperm, and the germ.
	 * @param rg
	 * @param image
	 */
	public void procThreeParts(
		RoiGrid rg,
		ImagePlus image,
		RoiGrid.RoiImageOutputConfiguration roiImageOutputBaseConfig
	) {
		ImagePlus img = image.duplicate();
		// img.getProcessor().blurGaussian(0.5);

		// try to find the vitreous endosperm (also catches strips of germ)
		ImagePlus vitImg = img.duplicate();
		IJProcess.colorThRGB(
			vitImg,
			IJM.Constants.vitreous_endosperm_lower_rgb_thresh,
			IJM.Constants.vitreous_endosperm_upper_rgb_thresh,
			IJM.Constants.vitreous_endosperm_rgb_pass_or_not
		);
		// ImageConverter vitIc = new ImageConverter(vitImg);
		// vitIc.convertToGray8();
		RoiGrid.RoiImageOutputConfiguration riocVit = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocVit != null) {
			riocVit.imagePrefix = "vit-";
		}
		HashMap<String,double[]>[][] vitResMap = rg.analyzeParticles(
			vitImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=50-2500 circularity=0.03-1.00 show=[Overlay Masks] display",
			riocVit
		);
		procResultsHelper(rg, vitResMap, "Vitreous");

		// try to find the chalky endosperm
		ImagePlus chkImg = img.duplicate();
		IJProcess.colorThRGB(
			chkImg,
			IJM.Constants.chalk_endosperm_lower_rgb_thresh,
			IJM.Constants.chalk_endosperm_upper_rgb_thresh,
			IJM.Constants.chalk_endosperm_rgb_pass_or_not
		);
		// ImageConverter chkIc = new ImageConverter(chkImg);
		// chkIc.convertToGray8();
		RoiGrid.RoiImageOutputConfiguration riocChk = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocChk != null) {
			riocChk.imagePrefix = "chk-";
		}
		HashMap<String,double[]>[][] chkResMap = rg.analyzeParticles(
			chkImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=50-2500 circularity=0.03-1.00 show=[Overlay Masks] display",
			riocChk
		);
		procResultsHelper(rg, chkResMap, "Chalk");
		

		// try and find the germ
		ImagePlus grmImg = img.duplicate();
		IJProcess.colorThRGB(
			grmImg,
			IJM.Constants.germ_endosperm_lower_rgb_pre_thresh,
			IJM.Constants.germ_endosperm_upper_rgb_pre_thresh,
			IJM.Constants.germ_endosperm_rgb__pre_pass_or_not
		);
		IJProcess.colorThRGB(
			grmImg,
			IJM.Constants.germ_endosperm_lower_rgb_post_thresh,
			IJM.Constants.germ_endosperm_upper_rgb_post_thresh,
			IJM.Constants.germ_endosperm_rgb__post_pass_or_not
		);
		// ImageConverter grmIc = new ImageConverter(grmImg);
		// grmIc.convertToGray8();
		RoiGrid.RoiImageOutputConfiguration riocGrm = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocGrm != null) {
			riocGrm.imagePrefix = "grm-";
		}
		HashMap<String,double[]>[][] grmResMap = rg.analyzeParticles(
			grmImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=25-2500 circularity=0.03-1.00 show=[Overlay Masks] display",
			riocGrm
		);
		procResultsHelper(rg, grmResMap, "Germ");

	}//end procThreeParts()

	public void danProcMar2025(
		RoiGrid rg,
		ImagePlus image,
		RoiGrid.RoiImageOutputConfiguration roiImageOutputBaseConfig
	) {
		ImagePlus img = image.duplicate();

		// try to find cross section
		ImagePlus xscImg = img.duplicate();
		IJProcess.colorThRGB(xscImg,
			new int[] {0,90,0},
			new int[] {254,255,255},
			new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass}
		);
		RoiImageOutputConfiguration riocXsc = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocXsc != null) {
			riocXsc.imagePrefix = "cross_section-";
		}
		HashMap<String,double[]>[][] xscResMap = rg.analyzeParticles(
			xscImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=50-2500 circularity=0.03-1.00 show=[Overlay Masks] display include",
			riocXsc
		);
		procResultsHelper(rg, xscResMap, "CrossSection");

		// try to find chalk
		ImagePlus chkImg = img.duplicate();
		IJProcess.colorThRGB(chkImg,
			new int[] {150,160,160},
			new int[] {240,240,240},
			new PassOrNot[] {PassOrNot.Pass,PassOrNot.Pass,PassOrNot.Pass}
		);
		RoiImageOutputConfiguration riocChk = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocChk != null) {
			riocChk.imagePrefix = "db-chalk-";
		}
		HashMap<String,double[]>[][] chkResMap = rg.analyzeParticles(
			chkImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=50-2500 circularity=0.03-1.00 show=[Overlay Masks] display include",
			riocChk
		);
		procResultsHelper(rg, chkResMap, "Chalk");

		// try and find the chalk germ
		ImagePlus chkgrmImg = img.duplicate();
		IJProcess.colorThRGB(
			chkgrmImg,
			new int[] {0,100,0},
			new int[] {150,240,240},
			new PassOrNot[] {PassOrNot.Stop,PassOrNot.Pass,PassOrNot.Pass}
		);
		// ImageConverter grmIc = new ImageConverter(grmImg);
		// grmIc.convertToGray8();
		RoiGrid.RoiImageOutputConfiguration riocChkGrm = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (riocChkGrm != null) {
			riocChkGrm.imagePrefix = "chkgrm-";
		}
		HashMap<String,double[]>[][] grmResMap = rg.analyzeParticles(
			chkgrmImg,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=25-2500 circularity=0.03-1.00 show=[Overlay Masks] display",
			riocChkGrm
		);
		procResultsHelper(rg, grmResMap, "ChalkGerm");

	}//end danProcMar2025()

	/**
	 * In proc functions, handles the movement and parsing of result from the
	 * result map (resMap) to the RoiGrid (rg).
	 * @param rg
	 * @param resMap
	 * @param headerPrefix
	 */
	public void procResultsHelper(RoiGrid rg, HashMap<String,double[]>[][] resMap, String headerPrefix) {
		for(int i = 0; i < rg.rrrs.length; i++) {
			for(int ii = 0; ii < rg.rrrs[i].length; ii++) {
				Set<String> these_headers = resMap[i][ii].keySet();
				for (String header : these_headers) {
					double[] res = resMap[i][ii].get(header);
					if (res.length == 0) {System.out.println("Couldn't get results for roi at grid 0-index " + rg.rrrs[i][ii].gridCellIdx);}
					else if (header == "Area") {
						rg.rrrs[i][ii].resultsHeaders.add(headerPrefix + "Area");
						double totalArea = 0; for(int j = 0; j < res.length; j++) {totalArea += res[j];}
						rg.rrrs[i][ii].resultsValues.add(totalArea);
					} else if (header == "AR" || header == "Solidity" || header == "Perim." || header == "BX" || header == "BY" ||
					header == "Circ." || header == "X" || header == "Y" || header == "Round" || header == "Height" || header == "Width") {
						if (res.length == 1) {
							rg.rrrs[i][ii].resultsHeaders.add(headerPrefix + header);
							rg.rrrs[i][ii].resultsValues.add(res[0]);
						}//end if we have one particle, as expected
						else if (res.length == 0) {
							rg.rrrs[i][ii].resultsHeaders.add(headerPrefix + header);
							rg.rrrs[i][ii].resultsValues.add(-1.0);
						}//end else if we need to alert someone that nothing was detected
						else {
							for (int r = 0; r < res.length; r++) {
								rg.rrrs[i][ii].resultsHeaders.add(headerPrefix + r + header);
								rg.rrrs[i][ii].resultsValues.add(res[r]);
							}//end looping over all results we got for this kernel
						}//end else we try to output whatever we can find
					}
					else {System.out.println("Didn't include header " + header);}
				}//end looping over each header in headers
			}//end looping over kernels
		}//end looping over groups of kernels
	}//end procResultsHelper()

	/**
	 * Gets a RoiGrid, holding Rois for all kernels.
	 * Does not determine grid cell locations.
	 * @param image Image to pull kernels from.
	 * @return Returns RoiGrid with sorted, grouped Rois for all kernels.
	 */
	public RoiGrid getRoiGrid(
		ImagePlus image,
		RoiGrid.RoiImageOutputConfiguration roiImageOutputBaseConfig
	) {
		ImagePlus img = image.duplicate();

		// set up results, roi, particle analysis
		ResultsTable rt = new ResultsTable();
		RoiManager rm = new RoiManager(false);
		int options = ParticleAnalyzer.SHOW_NONE + ParticleAnalyzer.ADD_TO_MANAGER;
		int measurements = Measurements.AREA;
		ParticleAnalyzer.setRoiManager(rm);
		ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, 400, 12500,0.0,1.0);
		// actually get on processing
		colorThHSB(
			img,
			IJM.Constants.kernel_lower_hsb_thresh,
			IJM.Constants.kernel_upper_hsb_thresh,
			IJM.Constants.kernel_hsb_pass_or_not,
			true);
		ImageConverter ic = new ImageConverter(img);
		ic.convertToGray8();
		IJ.setThreshold(img, 1, 255);
		pa.analyze(img);
		System.out.println("Detected " + rm.getCount() + " kernels.");
		RRR[][] rrrs = RoiGrid.createRRRs(rm);
		RoiGrid nrg = new RoiGrid(rrrs);
		RoiGrid.RoiImageOutputConfiguration roiImageOutputConfig = RoiImageOutputConfiguration.clone(roiImageOutputBaseConfig);
		if (roiImageOutputConfig != null) {
			roiImageOutputConfig.imagePrefix = "kern-";
		}
		// get measurements for the kernels
		HashMap<String,double[]>[][] resMap = nrg.analyzeParticles(img,
			"area centroid perimeter bounding shape display redirect=None decimal=2",
			"size=400-12500 circularity=0.03-1.00 show=[Overlay Masks] display include", roiImageOutputConfig);
		for (int i = 0; i < nrg.rrrs.length; i++) {
			for (int ii = 0; ii < nrg.rrrs[i].length; ii++) {
				Set<String> these_headers = resMap[i][ii].keySet();
				for (String header : these_headers) {
					double[] res = resMap[i][ii].get(header);
					if (res.length == 0) {System.out.println("Couldn't get results for roi at grid 0-index " + nrg.rrrs[i][ii].gridCellIdx);}
					else if (header == "Area") {
						nrg.rrrs[i][ii].resultsHeaders.add("Area");
						double totalArea = 0; for(int j = 0; j < res.length; j++) {totalArea += res[j];}
						nrg.rrrs[i][ii].resultsValues.add(totalArea);
					} else if (header == "AR" || header == "Solidity" || header == "Perim." || header == "BX" || header == "BY" ||
					header == "Circ." || header == "X" || header == "Y" || header == "Round" || header == "Height" || header == "Width") {
						if (res.length == 1) {
							nrg.rrrs[i][ii].resultsHeaders.add("Kern" + header);
							nrg.rrrs[i][ii].resultsValues.add(res[0]);
						}//end if we have one particle, as expected
						else if (res.length == 0) {
							nrg.rrrs[i][ii].resultsHeaders.add("Kern" + header);
							nrg.rrrs[i][ii].resultsValues.add(-1.0);
						}//end else if we need to alert someone that nothing was detected
						else {
							for (int r = 0; r < res.length; r++) {
								nrg.rrrs[i][ii].resultsHeaders.add("Kern" + r + header);
								nrg.rrrs[i][ii].resultsValues.add(res[r]);
							}//end looping over all results we got for this kernel
						}//end else we try to output whatever we can find
					} else {System.out.println("Didn't include header " + header + " for whole kernels.");}
				}//end looping over each header in headers
			}//end looping over kernel rois
		}//end looping over rows of kernel rois
		return nrg;
	}//end getRoiGrid

	/**
	 * This method, given an image with the blue grid and blue background, isolates,
	 * groups, and sorts the grid cells.
	 * @param image The image to pull grid cell locations from
	 * @return Returns sorted array list of Rois, representing all grid cells in img
	 */
	public ArrayList<Roi[]> getGridCells(ImagePlus image) {
		ImagePlus img = image.duplicate();

		// actually get processing
		colorThHSB(
			img,
			IJM.Constants.cells_lower_hsb_thresh,
			IJM.Constants.cells_upper_hsb_thresh,
			IJM.Constants.cells_hsb_pass_or_not,
			false
		);
		ImageConverter ic = new ImageConverter(img);
		// IJ.save(img, img.getTitle() + "-grid");
		ic.convertToGray8();

		// get temp file path and work around IJ's buggy BS
		URL jarurl = getClass().getProtectionDomain().getCodeSource().getLocation();
		File jarFile;
		try {
			jarFile = new File(jarurl.toURI());
		} catch (URISyntaxException e) {e.printStackTrace(); return null;}
		String tmpPth = jarFile.getParent() + "\\temp.tif";
		File tmpFile = new File(tmpPth);
		tmpFile.deleteOnExit();
		System.out.println(tmpFile.getAbsolutePath());
		IJ.save(img,tmpFile.getAbsolutePath());
		System.out.println(tmpFile.exists());
		System.out.println("Getting ready to analyze particles for grid cells");
		String macro = 
			"run(\"Set Measurements...\",\"bounding\");\n" +
			"setThreshold(1,255);\n" +
			"run(\"Analyze Particles...\", \"size=6500-12500 exclude include\")";
		IJ.open(tmpFile.getAbsolutePath());
		IJ.runMacro(macro);

		ResultsTable rt = ResultsTable.getResultsTable();
		Rectangle[] rects = new Rectangle[rt.size()];
		for(int i = 0; i < rt.size(); i++) {
			int bx = (int)rt.getValue("BX", i);
			int by = (int)rt.getValue("BY", i);
			int bw = (int)rt.getValue("Width", i);
			int bh = (int)rt.getValue("Height", i);
			Rectangle rect = new Rectangle(bx, by, bw, bh);
			rects[i] = rect;
		}

		System.out.println("Detected " + rects.length + " grid cells.");
		RoiManager rm = new RoiManager(false);

		for(int i = 0; i < rects.length; i++) {
			rects[i].grow(-6,-7);
			Roi newRoi = new Roi(rects[i]);
			rm.addRoi(newRoi);
		}//end shrinking every rectangle roi

		RoiGrid.groupRoiRows(rm);
		ArrayList<Roi[]> sortedRois = RoiGrid.createSortedClones(rm);
		// rm.save(img.getShortTitle() + "-roi.zip");
		rm.reset();
		return sortedRois;
	}//end getGridCells()

	/**
	 * Removes overly blue pixels by setting color value
	 * to 0,0,0. Mutates img parameter.
	 * @param img The input img, from which to remove blue
	 */
	public static void removeBlue(ImagePlus img) {
		ImageProcessor proc = img.getProcessor();
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++){
				int[] pixel = img.getPixel(x,y);
				int R = pixel[0];
				int G = pixel[1];
				int B = pixel[2];
				if (B > (R + G) * 3 / 5) {
					proc.set(x,y,0);
				}//end if pixel appears blue
			}//end looping over y values
		}//end looping over x values
		img.setProcessor(proc);
	}//end removeBlue

	/**
	 * Removes pixels outside a range by setting them to 0.  
	 * Uses HSB color space for constraints.
	 * Mutates the img parameter.
	 * @param img The image to process. This parameter is mutated.
	 * @param min int[3], minimum value (0-255) for H,S,B
	 * @param max int[3], maximum value (0-255) for H,S,B
	 * @param filter PassOrNot[3], for H,S,B, either "pass", or inverts
	 * @param flipThreshold If this is true, then the thresholded region is flipped to be whatever area is not covered by the thresholds given.
	 */
	public static void colorThHSB(ImagePlus img, int[] min, int[] max, IJM.Constants.PassOrNot[] filter, boolean flipThreshold) {
		ColorProcessor prc = img.getProcessor().convertToColorProcessor();
		ImageStack hsb = prc.getHSBStack();
		ImageProcessor h = hsb.getProcessor(1);
		ImageProcessor s = hsb.getProcessor(2);
		ImageProcessor b = hsb.getProcessor(3);
		for (int x = 0; x < prc.getWidth(); x++) {
			for (int y = 0; y < prc.getHeight(); y++) {
				int H = h.getPixel(x,y);
				int S = s.getPixel(x,y);
				int B = b.getPixel(x,y);
				boolean hin = H >= min[0] && H <= max[0];
				boolean sin = S >= min[1] && S <= max[1];
				boolean bin = B >= min[2] && B <= max[2];
				if (filter[0] != PassOrNot.Pass) {hin = !hin;}
				if (filter[1] != PassOrNot.Pass) {sin = !sin;}
				if (filter[2] != PassOrNot.Pass) {bin = !bin;}
				if (!hin || !sin || !bin) {
					if (!flipThreshold) {
						prc.set(x,y,0);
					}
				}//end if pixel is outside constraints
				else {
					if (flipThreshold) {
						prc.set(x,y,0);
					}
				}
			}//end looping over y coords for pixels
		}//end looping over x coords for pixels
		img.setProcessor(prc);
	}//end colorThHSB

	/**
	 * Removes pixels outside a range by setting them to 0.  
	 * Uses RGB color space for constraints.
	 * Mutates the img parameter.
	 * @param img The image to process. This parameter is mutated.
	 * @param min int[3], minimum value (0-255) for R,G,B
	 * @param max int[3], maximum value (0-255) for R,G,B
	 * @param filter PassOrNot[3], for R,G,B, either "pass", or inverts
	 */
	public static void colorThRGB(ImagePlus img, int[] min, int[] max, PassOrNot[] filter) {
		ImageProcessor prc = img.getProcessor();
		for (int x = 0; x < prc.getWidth(); x++) {
			for (int y = 0; y < prc.getHeight(); y++) {
				int[] rgb = prc.getPixel(x, y, null);
				int r = rgb[0];
				int g = rgb[1];
				int b = rgb[2];
				boolean rin = r >= min[0] && r <= max[0];
				boolean gin = g >= min[1] && g <= max[1];
				boolean bin = b >= min[2] && b <= max[2];
				if (filter[0] != PassOrNot.Pass) {rin = !rin;}
				if (filter[1] != PassOrNot.Pass) {gin = !gin;}
				if (filter[2] != PassOrNot.Pass) {bin = !bin;}
				if (!rin || !gin || !bin) {
					prc.set(x,y,0);
				}//end if pixel is outside constraints
			}//end looping over y coords for pixels
		}//end looping over x coords for pixels
		img.setProcessor(prc);
	}//end colorThRGB

	
	/**
	 * Removes pixels outside a range by setting them to 0.  
	 * Uses YUV color space for constraints.
	 * Mutates the img parameter.
	 * @param img The image to process. This parameter is mutated.
	 * @param min int[3], minimum value (0-255) for Y,U,V
	 * @param max int[3], maximum value (0-255) for Y,U,V
	 * @param filter PassOrNot[3], for Y,U,V, either "pass", or inverts
	 */
	public static void colorThYUV(ImagePlus img, int[] min, int[] max, PassOrNot[] filter) {
		ImageProcessor prc = img.getProcessor();
		for (int x = 0; x < prc.getWidth(); x++) {
			for (int y = 0; y < prc.getHeight(); y++) {
				int[] rgb = prc.getPixel(x, y, null);
				double r = rgb[0];
				double g = rgb[1];
				double b = rgb[2];
				// conversion formula taken from:
				// https://softpixel.com/~cwright/programming/colorspace/yuv/
				double Y = r * 0.299000 + g * 0.587000 + b * 0.114000;
				double u = r * -0.168736 + g * -0.331264 + b * 0.5 + 128;
				double v = r * 0.500000 + g * -0.418688 + b * -0.081312 + 128;
				boolean yin = Y >= min[0] && Y <= max[0];
				boolean uin = u >= min[1] && u <= max[1];
				boolean vin = v >= min[2] && v <= max[2];
				if (filter[0] != PassOrNot.Pass) {yin = !yin;}
				if (filter[1] != PassOrNot.Pass) {uin = !uin;}
				if (filter[2] != PassOrNot.Pass) {vin = !vin;}
				if (!yin || !uin || !vin) {
					prc.set(x,y,0);
				}//end if pixel is outside constraints
			}//end looping over y coords for pixels
		}//end looping over x coords for pixels
		img.setProcessor(prc);
	}//end colorThYUV

	/**
	 * Removes pixels outside a range by setting them to 0
	 * Uses grayscale color spaces for constraints.
	 * Mutates the img parameter.
	 * @param img The image to process. This parameter is mutated.
	 * @param min minimum grayscale value (0,255)
	 * @param max maximum grayscale value (0,255)
	 * @param filter Either "pass", or inverts
	 */
	public static void colorThGrayscale(ImagePlus img, int min, int max, PassOrNot filter) {
		ImageProcessor prc = img.getProcessor();
		for (int x = 0; x < prc.getWidth(); x++) {
			for (int y = 0; y < prc.getHeight(); y++) {
				double val = prc.getPixelValue(x,y);
				boolean in = val >= min && val <= max;
				if (filter != PassOrNot.Pass) {in = !in;}
				if (!in) { prc.set(x,y,0); }
			}//end looping over y coords for pixels
		}//end looping over x coords for pixels
		img.setProcessor(prc);
	}//end colorThGrayscale
}//end class IJProcess
