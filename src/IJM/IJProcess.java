package IJM;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import Utils.Constants;
import Utils.Result;
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
    public static double lower_flag_thresh = 0.05;
    public static double upper_flag_thresh = 0.10;

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
    public static Result<File> getOutputFilePath(boolean ensureDirectoryExists) {
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

            return new Result<File>(outputFile);
        } catch (Exception e) {
            return new Result<File>(e);
        }//end catching any exceptions
    }//end getOutputFilePath(ensureDirectoryExists)

    /**
     * Builds all the text for the output file, and then writes that text to a file. 
     * Uses path from IJProcess.getOutputFilePath().
     * @param inputList The list of SumResult objects that represent the processed data. This will be formatted and written to the file.
     * @return Returns a result containing the full string output, or a wrapped error if something prevents the output file from being written.
     */
    public static Result<String> makeOutputFile(List<SumResult> inputList) {
        // save to output file
        Result<File> outputFileResult = getOutputFilePath(true);
        if (outputFileResult.isErr()) {
            return new Result<>(outputFileResult.getError());
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
            return new Result<>(e);
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

        // print headers for the columns we're about to print
        pw.print(
            "FileID,GridIdx,Pixels1,Pixels2,%Area" + 
            "," +
            ",KX,KY" + 
            ",EX,EY" + 
            ",KBX,KBY,KWidth,KHeight" + 
            ",EBX,EBY,EWidth,EHeight" +
            ",KPerim" +
            ",EPerim" +
            ",KCirc,KRound,KAR,KSolidity" +
            ",ECirc,ERound,EAR,ESolidity" +
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
            double total_area = res.getResValSentinel("Area");
            double endosperm_area = res.getResValSentinel("EndospermArea");
            double endo_percent = (endosperm_area * 100) / total_area;

            double kx = res.rrr.roi.getBounds().getCenterX();
            double ky = res.rrr.roi.getBounds().getCenterY();
            
            double ex = res.getResValSentinel("EndospermX") + res.rrr.roi.getBounds().getX();
            double ey = res.getResValSentinel("EndospermY") + res.rrr.roi.getBounds().getY();
            
            int kbx = res.rrr.roi.getBounds().x;
            int kby = res.rrr.roi.getBounds().y;
            int kw = res.rrr.roi.getBounds().width;
            int kh = res.rrr.roi.getBounds().height;

            double ebx = res.getResValSentinel("EndospermBX") + kbx;
            double eby = res.getResValSentinel("EndospermBY") + kby;
            double ew = res.getResValSentinel("EndospermWidth");
            double eh = res.getResValSentinel("EndospermHeight");

            double kperim = res.getResValSentinel("KernPerim.");

            double eperim = res.getResValSentinel("EndospermPerim.");

            double kcirc = res.getResValSentinel("KernCirc.");
            double kround = res.getResValSentinel("KernRound");
            double kar = res.getResValSentinel("KernAR");
            double ksolidity = res.getResValSentinel("KernSolidity");

            double ecirc = res.getResValSentinel("EndospermCirc.");
            double eround = res.getResValSentinel("EndospermRound");
            double ear = res.getResValSentinel("EndospermAR");
            double esolidity = res.getResValSentinel("EndospermSolidity");

            data_output.append(String.format(
                "%s,%d,%3.1f,%3.1f,%3.1f" +
                "," +
                ",%f,%f" +
                ",%f,%f" +
                ",%d,%d,%d,%d" +
                ",%f,%f,%f,%f" +
                ",%f" +
                ",%f" +
                ",%f,%f,%f,%f" +
                ",%f,%f,%f,%f" +
                "\n",

                res.file.getName(), res.rrr.gridCellIdx + 1, total_area, endosperm_area, endo_percent,

                kx,ky,
                ex,ey,
                kbx,kby,kw,kh,
                ebx,eby,ew,eh,
                kperim,
                eperim,
                kcirc,kround,kar,ksolidity,
                ecirc,eround,ear,esolidity
            ));
        }//end making the output string
        
        // print output for all images
        pw.print(data_output.toString());

        // close output file
        pw.close();

        return new Result<>(data_output.toString());
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
    public static Result<String> doUnsharpCorrection(String filepath, double sigma, double weight, boolean rename_file) {
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
        return new Result<String>(newPath);
    }//end doUnsharpCorrection(filepath, sigma, weight)

    /**
     * Runs the code to process a list of images. Actually just passes everything to IJProcess.MainMacro()
     * @param files_to_process The list of image Files to process.
     * @return Returns a result that will contain the full string written to an output file, or an error if something prevented completion.
     */
    public Result<String> runMacro(List<File> files_to_process) {
        try {
            return MainMacro(files_to_process);
        } catch (Exception e) {
            return new Result<>(e);
        }//end if we catch any exceptions
    }//end runMacro()

    /**
     * The main method for organizing and processing images.
     * @param files_to_process The list of image Files to process
     * @return Returns a result that will contain the full string written to an output file, or an error if something prevented completion.
     */
    public Result<String> MainMacro(List<File> files_to_process) {
        List<SumResult> runningSum = new ArrayList<SumResult>();
        for (int i = 0; i < files_to_process.size(); i++) {
            File file = files_to_process.get(i);
            // String sliceBase = file.getName().substring(0, file.getName().length() - 4);

            // actually start processing
            ImagePlus this_image = IJ.openImage(file.getAbsolutePath());
            this_image.getProcessor().flipHorizontal();

            // get location of grid cells
            ArrayList<Roi[]> gridCells = getGridCells(this_image);

            // remove grid and background, get whole kernels
            RoiGrid kernGrid = getRoiGrid(this_image);

            // Update kernelGrid with gridCells information
            kernGrid.updateGridLocs(gridCells);

            // Get procs from endosperm(white) area
            procEndosperm(kernGrid, this_image);

            this_image.close();

            // add all the results and stuff we got
            runningSum.addAll(SumResult.fromRoiGrid(file, kernGrid));
        }//end looping over each file we want to split

        // output the output file
        Result<String> outputFileResult = makeOutputFile(runningSum);
        lastProcResult = runningSum;
        // return the rows of data that wil show up in the output file
        return outputFileResult;
    }//end Main Macro converted from ijm

    /**
     * Adds endosperm(white) area from each kernel to each kernel.
     * @param rg The roigrid holding all the kernel location information.
     * @param image The image to process.
     */
    public static void procEndosperm(RoiGrid rg, ImagePlus image) {
        // prepare image to be processed
        ImagePlus img = image.duplicate();
        colorThHSB(img, new int[] {120,0,166}, new int[] {180,255,255}, new String[] {"stop","pass","pass"});
        ImageConverter ic = new ImageConverter(img);
        ic.convertToGray8();
        HashMap<String,double[]>[][] resMap = rg.analyzeParticles(img,
            "area centroid perimeter bounding shape display redirect=None decimal=2",
            "size=500-10000 display");
        // update rg.rrrs with appropriate result info from resMap
        for(int i = 0; i < rg.rrrs.length; i++) {
            for(int ii = 0; ii < rg.rrrs[i].length; ii++) {
                Set<String> these_headers = resMap[i][ii].keySet();
                for (String header : these_headers) {
                    double[] res = resMap[i][ii].get(header);
                    if (res.length == 0) {System.out.println("Couldn't get results for roi at grid 0-index " + rg.rrrs[i][ii].gridCellIdx);}
                    else if (header == "Area") {
                        rg.rrrs[i][ii].resultsHeaders.add("EndospermArea");
                        double totalArea = 0; for(int j = 0; j < res.length; j++) {totalArea += res[j];}
                        rg.rrrs[i][ii].resultsValues.add(totalArea);
                    } else if (header == "AR" || header == "Solidity" || header == "Perim." || header == "BX" || header == "BY" ||
                    header == "Circ." || header == "X" || header == "Y" || header == "Round" || header == "Height" || header == "Width") {
                        if (res.length == 1) {
                            rg.rrrs[i][ii].resultsHeaders.add("Endosperm" + header);
                            rg.rrrs[i][ii].resultsValues.add(res[0]);
                        }//end if we have one particle, as expected
                        else if (res.length == 0) {
                            rg.rrrs[i][ii].resultsHeaders.add("Endosperm" + header);
                            rg.rrrs[i][ii].resultsValues.add(-1.0);
                        }//end else if we need to alert someone that nothing was detected
                        else {
                            for (int r = 0; r < res.length; r++) {
                                rg.rrrs[i][ii].resultsHeaders.add("Endosperm" + r + header);
                                rg.rrrs[i][ii].resultsValues.add(res[r]);
                            }//end looping over all results we got for this kernel
                        }//end else we try to output whatever we can find
                    }
                    else {System.out.println("Didn't include header " + header);}
                }//end looping over each header in headers
            }//end looping over kernels
        }//end looping over groups of kernels
    }//end procEndosperm

    /**
     * Gets a RoiGrid, holding Rois for all kernels.
     * Does not determine grid cell locations.
     * @param image Image to pull kernels from.
     * @return Returns RoiGrid with sorted, grouped Rois for all kernels.
     */
    public static RoiGrid getRoiGrid(ImagePlus image) {
        ImagePlus img = image.duplicate();

        // set up results, roi, particle analysis
        ResultsTable rt = new ResultsTable();
        RoiManager rm = new RoiManager(false);
        int options = ParticleAnalyzer.SHOW_NONE + ParticleAnalyzer.ADD_TO_MANAGER;
        int measurements = Measurements.AREA;
        ParticleAnalyzer.setRoiManager(rm);
        ParticleAnalyzer pa = new ParticleAnalyzer(options, measurements, rt, 4000, 50000,0.0,1.0);
        // actually get on processing
        colorThHSB(img, new int[] {120,0,0}, new int[] {180,255,255}, new String[] {"stop","pass","pass"});
        ImageConverter ic = new ImageConverter(img);
        ic.convertToGray8();
        IJ.setThreshold(img, 1, 255);
        pa.analyze(img);
        System.out.println("Detected " + rm.getCount() + " kernels.");
        RRR[][] rrrs = RoiGrid.createRRRs(rm);
        RoiGrid nrg = new RoiGrid(rrrs);
        // get measurements for the kernels
        HashMap<String,double[]>[][] resMap = nrg.analyzeParticles(img,
            "area centroid perimeter bounding shape display redirect=None decimal=2",
            "size=4000-50000 display");
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
    public static ArrayList<Roi[]> getGridCells(ImagePlus image) {
        ImagePlus img = image.duplicate();
        // set up roi, particle analysis
        ResultsTable rt = new ResultsTable();
        RoiManager rm = new RoiManager(false);
        int options = ParticleAnalyzer.ADD_TO_MANAGER + ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES +
        ParticleAnalyzer.SHOW_NONE + ParticleAnalyzer.INCLUDE_HOLES + ParticleAnalyzer.CLEAR_WORKSHEET;
        int measurements = Measurements.RECT;
        ParticleAnalyzer.setRoiManager(rm);
        ParticleAnalyzer pa = new ParticleAnalyzer(options,measurements,rt,26000,50000);

        // actually get processing
        colorThHSB(img, new int[] {149,0,0}, new int[] {158,255,255}, new String[] {"pass","pass","pass"});
        ImageConverter ic = new ImageConverter(img);
        // IJ.save(img, img.getTitle() + "-grid");
        ic.convertToGray8();
        IJ.setThreshold(img,1,255);
        pa.analyze(img);
        // rt.save(img.getTitle() + "restults.txt");
        System.out.println("Detected " + rm.getCount() + " grid cells.");
        for(int i = 0; i < rm.getCount(); i++) {
            Roi thisRoi = rm.getRoi(i);
            Rectangle thisBound = thisRoi.getBounds();
            thisBound.grow(-25,-30);
            Roi newRoi = new Roi(thisBound);
            rm.setRoi(newRoi, i);
        }//end shrinking and rectangling every roi
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
     * @param filter String[3], for H,S,B, either "pass", or inverts
     */
    public static void colorThHSB(ImagePlus img, int[] min, int[] max, String[] filter) {
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
                if (filter[0] != "pass") {hin = !hin;}
                if (filter[1] != "pass") {sin = !sin;}
                if (filter[2] != "pass") {bin = !bin;}
                if (!hin || !sin || !bin) {
                    prc.set(x,y,0);
                }//end if pixel is outside constraints
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
     * @param filter String[3], for R,G,B, either "pass", or inverts
     */
    public static void colorThRGB(ImagePlus img, int[] min, int[] max, String[] filter) {
        ImageProcessor prc = img.getProcessor();
        for (int x = 0; x < prc.getWidth(); x++) {
            for (int y = 0; y < prc.getHeight(); y++) {
                int[] rgb = prc.getPixel(x, y, null);
                int r = rgb[0];
                int g = rgb[0];
                int b = rgb[0];
                boolean rin = r >= min[0] && r <= max[0];
                boolean gin = g >= min[1] && g <= max[1];
                boolean bin = b >= min[2] && b <= max[2];
                if (filter[0] != "pass") {rin = !rin;}
                if (filter[1] != "pass") {gin = !gin;}
                if (filter[2] != "pass") {bin = !bin;}
                if (!rin || !gin || !bin) {
                    prc.set(x,y,0);
                }//end if pixel is outside constraints
            }//end looping over y coords for pixels
        }//end looping over x coords for pixels
        img.setProcessor(prc);
    }//end colorThRGB
}//end class IJProcess
