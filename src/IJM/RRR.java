package IJM;

import java.util.ArrayList;
import java.util.List;

import ij.gui.Roi;


/*
 * 1 to 1 relationship of kernel to RRR
 * RRR should have following info
 * Roi for whole kernel,
 * 
 * I need to figure out what else to put here later
 */
public class RRR {
    public Roi roi;
    // public int rowIdx;
    // public int colIdx;
    // public int totIdx;
    /**
     * The 0-based index of the grid cell that contains this kernel.
     * -1 means it's not set.
     */
    public int gridCellIdx = -1;
    /**
     * All Results headers for this roi.
     * Parallel with resultsValues.
     */
    public List<String> resultsHeaders = new ArrayList<String>();
    /**
     * All Results values for this roi.
     * Parallel with resultsHeaders.
     */
    public List<Double> resultsValues = new ArrayList<Double>();
    // group number
    // public int grpNum;
    // position within group, 0-based
    // public int grpPos;
    public RRR(Roi roi) {
        this.roi = roi;
        // this.grpNum = grpNum;
        // this.grpPos = grpPos;
    }//end constructor
}//end class RRR