package IJM;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.ImageConverter;

/*
 * RoiGrid should have the following methods
 * - function to generate RRRs from rm
 * - function to get 1d or 2d list of RRRs [cancelled?]
 * - function to apply ParticleAnalyzer to all RRRs, get parallel list of data
 */
public class RoiGrid {
	public RRR[][] rrrs;
	
	public RoiGrid(){}
	public RoiGrid(RRR[][] rrrs) {
		this.rrrs = rrrs;
	}//end 1-arg constructor from RoiManager
	
	/**
	 * Given unsorted, ungrouped RoiManager with Rois for whole kernels,
	 * creates RRR[][] to use with RoiGrid.
	 * @param rm The RoiManager with rois for kernels
	 * @return Returns 2d array of RRRs, to use in RoiGrid.
	 */
	public static RRR[][] createRRRs(RoiManager rm, ProcConfig config) {
		groupRoiRows(rm, config);
		ArrayList<Roi[]> ragged_rois = createSortedClones(rm);
		RRR[][] rrrs = new RRR[ragged_rois.size()][];
		// int idx_so_far = 0;
		for(int i = 0; i < ragged_rois.size(); i++) {
			Roi[] this_group = ragged_rois.get(i);
			rrrs[i] = new RRR[this_group.length];
			for(int ii = 0; ii < this_group.length; ii++) {
				RRR rrr = new RRR(this_group[ii]);
				rrrs[i][ii] = rrr;
			}//end looping over Rois within this_group
		}//end looping over groups
		return rrrs;
	}//end createRoiGrid

	/**
	 * This method updates each RRR to note where the RRR is
	 * within the greater grid.
	 * If we can't find the right grid cell, we don't change it.
	 * @param gridLocs The array list of Rois, each being a grid cell.
	 */
	public void updateGridLocs(ArrayList<Roi[]> gridLocs) {
		for(int kernRow = 0; kernRow < rrrs.length; kernRow++) {
			for(int kernCol = 0; kernCol < rrrs[kernRow].length; kernCol++) {
				Rectangle thisKernBound = rrrs[kernRow][kernCol].roi.getBounds();
				int gridIdx = 0;
				boolean exitInner = false;
				for(int gridRow = 0; gridRow < gridLocs.size() && !exitInner; gridRow++) {
					for(int gridCol = 0; gridCol < gridLocs.get(gridRow).length && !exitInner; gridCol++) {
						Rectangle thisGridBound = gridLocs.get(gridRow)[gridCol].getBounds();
						if (thisKernBound.intersects(thisGridBound)) {
							// update gridIdx to proper value
							rrrs[kernRow][kernCol].gridCellIdx = gridIdx;
							System.out.println("Grid 0-Idx " + gridIdx + "\t" + thisKernBound.toString());
							// exit from 2 inner loops
							exitInner = true;
							break;
						}//end if we found the right grid cell
						// loop maintenance
						gridIdx++;
					}//end looping over grid columns
				}//end looping over grid rows
			}//end looping over kernel columns
		}//end looping over kernel rows
	}//end updateGridLocs(gridLocs)

	/**
	 * Adds information from a results column to rrrs.
	 * @param rrrs the RRR[][] object to update.
	 * @param header The name of the header for the results column.
	 * @param vals The column of values for whole image, to be added to appropriate RRR.
	 */
	public static void addResultColumn(RRR[][] rrrs, String header, double[] vals) {
		int total_index = 0;
		for(int i = 0; i < rrrs.length; i++) {
			for(int ii = 0; ii < rrrs[i].length; ii++) {
				rrrs[i][ii].resultsHeaders.add(header);
				rrrs[i][ii].resultsValues.add(vals[total_index]);
				// loop maintenance
				total_index++;
			}//end looping over kernels in rrrs
		}//end looping over groups in rrrs
	}//end addResultColumn(header, vals)

	/**
	 * This class is used to store configuration settings for outputting images from
	 * the analyze particles function.
	 */
	public static class RoiImageOutputConfiguration {
		public File baseDirectory;
		// public String newFolderName;
		public String imagePrefix;
		public String imageSuffix;

		public RoiImageOutputConfiguration(
			File baseDirectory
			// ,String newFolderName
		) {
			this.baseDirectory = baseDirectory;
			// this.newFolderName = newFolderName;
			this.imagePrefix = "";
			this.imageSuffix = "";
		}//end 1-arg constructor

		public RoiImageOutputConfiguration(
			File baseDirectory,
			// String newFolderName,
			String imagePrefix,
			String imageSuffix
		) {
			this.baseDirectory = baseDirectory;
			// this.newFolderName = newFolderName;
			this.imagePrefix = imagePrefix;
			this.imageSuffix = imageSuffix;
		}//end 3-arg constructor

		public static RoiImageOutputConfiguration clone(RoiImageOutputConfiguration other) {
			if (other != null) {
				return new RoiImageOutputConfiguration(
					other.baseDirectory,
					other.imagePrefix,
					other.imageSuffix
				);
			} else return null;
		}//end copy constructor
	}//end public inner class RoiImageOutputConfiguration

	/**
	 * Uses Particle Analyzer to analyze every kernel roi in provided image.
	 * The image provided should already be edited such that 8-bit threshold 1-255
	 * retrieves all required information.  
	 * Because parameters are passed to a macro as strings, it's possible that imagej will refuse to do
	 * something and print something to the console. This is done to get around the buggy ParticleAnalyzer class.
	 * @param image The image you wish to process. It should be based on image for rrrs.
	 * @param measurementsParam The parameter to pass to setMeasurements macro call. For example, "area centroid perimeter bounding shape display redirect=None decimal=2"
	 * @param particlesParam The parameter to pass to analyze particles macro call. For example, "size=500-10000 display"
	 * @return Parallel 2d array to rrrs. Each element is results-table information, column-header->column vals.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String,double[]>[][] analyzeParticles(
		ImagePlus image,
		String measurmentsParam,
		String particlesParam,
		RoiImageOutputConfiguration roiImageOutputConfig
	) {
		HashMap<String,double[]>[][] resMap = new HashMap[rrrs.length][];
		// go through and do image analysis on each roi
		URL jarurl = getClass().getProtectionDomain().getCodeSource().getLocation();
		File jarFile;
		try {
			jarFile = new File(jarurl.toURI());
		} catch (URISyntaxException e) {e.printStackTrace(); return null;}
		String tmpPth = jarFile.getParent() + "\\temp.tif";
		File tmpFile = new File(tmpPth);
		tmpFile.deleteOnExit();
		System.out.println(tmpFile.getAbsolutePath());

		for(int i = 0; i < rrrs.length; i++) {
			resMap[i] = new HashMap[rrrs[i].length];
			for(int ii = 0; ii < rrrs[i].length; ii++) {
				resMap[i][ii] = new HashMap<>(1);
				// creates duplicate image of appropriate roi from image
				ImagePlus[] kerns = image.crop(new Roi[] {rrrs[i][ii].roi},"");
				ImagePlus kern; if (kerns.length > 0) {kern = kerns[0];} else {break;}
				// IJ.save(kern, "errorwhy-" + i + "-" + ii + "-g" + rrrs[i][ii].gridCellIdx);
				if (roiImageOutputConfig != null) {
					// Save this version of image, showing thresholded region
					IJ.save(
						kern,
						new File(
							roiImageOutputConfig.baseDirectory.getAbsolutePath(),
							roiImageOutputConfig.imagePrefix + "thresh_region-g" + (rrrs[i][ii].gridCellIdx + 1) + roiImageOutputConfig.imageSuffix + ".tiff"
						)
						.getAbsolutePath()
					);
				}//end if we want to save an image of this thresholded image
				ImageConverter ic = new ImageConverter(kern);
				ic.convertToGray8();
				IJ.setThreshold(kern, 1, 255);
				try {
					// analyze particles in image of kernel
					
					IJ.save(kern,tmpFile.getAbsolutePath());
					System.out.println(tmpFile.exists());
					System.out.println("Getting ready to analyze particles for kernel with gridIdx " + rrrs[i][ii].gridCellIdx +
					", X:" + rrrs[i][ii].roi.getBounds().getCenterX() + ", y:" + rrrs[i][ii].roi.getBounds().getCenterY());
					// area centroid perimeter bounding shape display redirect=None decimal=2
					// size=500-10000 display
					String macro = 
						// "open(\"" + tmpPth + "\");\n" + 
						"run(\"Set Measurements...\", \"" + measurmentsParam + "\");\n" +
						"setThreshold(1,255);\n" + 
						"run(\"Analyze Particles...\", \"" + particlesParam + "\");\n";
					if (roiImageOutputConfig != null) {
						// Add more lines to macro text in order to flatten and save image after particle analysis
						macro += "run(\"Flatten\");\n";
						File outputFile = new File(
							roiImageOutputConfig.baseDirectory.getAbsolutePath(),
							roiImageOutputConfig.imagePrefix + "particle_region-g" + (rrrs[i][ii].gridCellIdx + 1) + roiImageOutputConfig.imageSuffix + ".tiff"
						);
						System.out.println("Outputting roi image to \"" + outputFile.getAbsolutePath() + "\"");
						macro += "saveAs(\"Tiff\", \"" + outputFile.getAbsolutePath().replace('\\', '/') + "\");\n";
					}//end if we want to save an image of the particle detection
					// redirect console spam from imagej
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					System.setOut(new PrintStream(baos));
					// System.out.println("macro: \n" + macro);
					IJ.open(tmpFile.getAbsolutePath());
					IJ.runMacro(macro);
					ResultsTable rt = ResultsTable.getResultsTable();
					String[] headings = rt.getHeadings();
					for(int headIdx = 0; headIdx < headings.length; headIdx++) {
						if (headings[headIdx] == "Label") {continue;}
						resMap[i][ii].put(headings[headIdx],rt.getColumn(headings[headIdx]));
					}//end adding each column and heading to resMap
					rt.reset();
					// reset output stream
					System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				}//end trying to catch dumb ParticleAnalyzer bugs
				catch (ArrayIndexOutOfBoundsException e) {System.out.println("Out of bounds?\t" + rrrs[i][ii].roi.getBounds().toString() + " grididx " + rrrs[i][ii].gridCellIdx);}
			}//end looping over kernels
		}//end looping over kernel groups
		return resMap;
	}//end analyzeParticles

	/**
	 * Groups rois in RoiManager, so that each
	 * group represents one row
	 * @param rm The RoiManager to pull from.
	 */
	public static void groupRoiRows(RoiManager rm, ProcConfig config) {
		Roi[] rois = rm.getRoisAsArray();
		int distTolerance = config.distTolerance;
		int curGroupNum = 0;
		if (rois.length == 1) {rois[0].setGroup(curGroupNum); return;}
		else if (rois.length > 1) {rois[0].setGroup(curGroupNum);}
		else {return;}
		for(int i = 1; i < rois.length; i++) {
			Rectangle l = rois[i-1].getBounds();
			Rectangle r = rois[i].getBounds();
			if (vertBoundIntersect(l,r,distTolerance)) {
				rois[i].setGroup(curGroupNum);
			}//end if they're in the same row
			else {
				curGroupNum += 1;
				rois[i].setGroup(curGroupNum);
			}//end else we have a different row
		}//end looping over each roi, grouping by row
	}//end groupRoiRows()

	/**
	 * Checks if two rectangle bounds are close enough to count as intersecting in y dimension.
	 * Intended to be used to check if two bounds are in the same row of a grid.
	 * @param r1 first rectangle bounds
	 * @param r2 second rectangle bounds
	 * @param tolerance if r1 and r2 are separated vertically by tolerance or less, count as intersect
	 * @return returns true if rectangles intersect vertically (similar in height), false otherwise
	 */
	public static boolean vertBoundIntersect(Rectangle r1, Rectangle r2, int tolerance) {
		int top_y;
		int top_height;
		int bottom_y;
		if (r1.y < r2.y) {top_y = r1.y; top_height = r1.height; bottom_y = r2.y;}
		else {top_y = r2.y; top_height = r2.height; bottom_y = r1.y;}

		return (top_y + top_height + tolerance >= bottom_y);
	}//end vertBoundIntersect(r1,r2,threshold)

	/**
	 * Updates a RoiManager with a ragged 2d array list of rois, as might be
	 * created by the createSortedClones(rm) method.
	 * @param gcRois The grouped, sorted, cloned, rois.
	 * @param rm The RoiManager to update.
	 * @param img The image to put rois on
	 */
	public static void sortRoiRows(ArrayList<Roi[]> gcRois, RoiManager rm, ImagePlus img) {
		rm.removeAll();
		for(int i = 0; i < gcRois.size(); i++) {
			Roi[] roiGroup = gcRois.get(i);
			for(int j = 0; j < roiGroup.length; j++) {
				rm.add(img, roiGroup[j], i * 100 + j);
			}//end looping within group
		}//end looping through groups
	}//end sortRoiRows()

	/**
	 * Creates a ragged 2d array list with groups, sorted by x value within each group.
	 * @param rm The RoiManager to pull rois from
	 * @return Returns the ragged 2d array list if everything goes fine, else null.
	 */
	public static ArrayList<Roi[]> createSortedClones(RoiManager rm) {
		Roi[] rois = rm.getRoisAsArray();
		ArrayList<Roi[]> groupedClonedRois = new ArrayList<Roi[]>();
		int curGroupNum;
		if (rois.length > 0) {curGroupNum = rois[0].getGroup();}
		else {
			ArrayList<Roi[]> tmp = new ArrayList<Roi[]>();
			tmp.add(new Roi[0]);
			System.out.println("\n\n\tWe're trying to create sorted clones, but there's no ROIs.");
			System.out.println("\tThis should never happen...\n\tDid you change the dpi?");
			System.out.println("\tMaybe the size constraints for analyze particles failed?\n");
			return tmp;
		}
		int roiIndex = 0;
		while(true) {
			// loop maintenance for ending loop
			if (roiIndex >= rois.length) {break;}

			// gather all rois in group curGroupNum
			ArrayList<Roi> curGroupUnsorted = new ArrayList<Roi>();
			int innerRoiIndex = roiIndex;
			while(true) {
				// add Roi clone to group if:
				// - index is valid
				// - group matches curGroupNum
				// else: end inner loop, assume this is end of group
				if (innerRoiIndex >= rois.length) {break;}
				else if (rois[innerRoiIndex].getGroup() == curGroupNum) {
					curGroupUnsorted.add((Roi)rois[innerRoiIndex].clone());
				} else {break;}
				innerRoiIndex++;
			}//end looping until we gather each Roi with curGroupNum

			// sort Rois in curGroup by x value
			Roi[] curGroup = new Roi[curGroupUnsorted.size()];
			int last_min = 0;
			for (int i = 0; i < curGroup.length; i++) {
				// find i-th smallest x-val
				int min_so_far = Integer.MAX_VALUE;
				int min_so_far_idx = 0;
				for (int ii = 0; ii < curGroupUnsorted.size(); ii++) {
					int this_x = curGroupUnsorted.get(ii).getBounds().x;
					if (this_x < min_so_far && this_x > last_min) {min_so_far = this_x; min_so_far_idx = ii;}
				}//end finding i-th smallest x-val
				curGroup[i] = curGroupUnsorted.get(min_so_far_idx);
				last_min = min_so_far;
			}//end filling elements of curGroup

			// loop maintenance for continuing loop
			curGroupNum++;
			roiIndex += curGroupUnsorted.size();
			if (curGroupUnsorted.size() > 0) {groupedClonedRois.add(curGroup);}
		}//end looping until we've finished all groups
		return groupedClonedRois;
	}//end createSortedClones(rm)
}//end class RoiGrid


