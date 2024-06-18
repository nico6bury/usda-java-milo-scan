package IJM;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SumResult {
    public File file;
    public RRR rrr;

    public SumResult() {}
    public SumResult(File file, RRR rrr) {this.file = file; this.rrr = rrr;}

    /**
     * Creates a SumResult for each RRR in a RoiGrid.
     * @param file The file these rois came from.
     * @param rg The RoiGrid to pull RRRs from.
     * @return Returns a List of all SumResults created.
     */
    public static List<SumResult> fromRoiGrid(File file, RoiGrid rg) {
        List<SumResult> srl = new ArrayList<SumResult>();
        for (int i = 0; i < rg.rrrs.length; i++) {
            for (int ii = 0; ii < rg.rrrs[i].length; ii++) { 
                srl.add(new SumResult(file,rg.rrrs[i][ii]));
            }//end looping over kernels
        }//end looping over groups of kernels
        return srl;
    }//end fromRoiGrid(file,rg)

    /**
     * Gets the results value in rrr under thes specified header.
     * If the header isn't found, returns -1 as a sentinel value.
     * @param header The results header to search for in rrr.resultsHeaders.
     * @return Returns either the value from rrr.resultsValues or -1.
     */
    public double getResValSentinel(String header) {
        int idx = rrr.resultsHeaders.indexOf(header);
        if (idx == -1) {return -1;}
        else {return rrr.resultsValues.get(idx);}
    }//end getResValSentinel(header)

    /**
     * This method was written as a helper method for uxQueueListValueChanged(), but it could also be helpful for other processes.
     * When given a list of SumResults, it will group together SumResults which have the same absolute file path.
     * @param sumResults The list of SumResults to group.
     * @return Returns a list of lists of SumResults. The inner lists are the groupings.
     */
    public static List<List<SumResult>> groupResultsByFile(List<SumResult> sumResults) {
        List<List<SumResult>> groupedResults = new ArrayList<List<SumResult>>();
        List<String> filenames_indexed = new ArrayList<String>();
        for (int i = 0; i < sumResults.size(); i++) {
            SumResult this_result = sumResults.get(i);
            // check if we need to create new list in groupedResults or just add to existing
            if (filenames_indexed.contains(this_result.file.getAbsolutePath())) {
                // find out which index of grouped results contains the right filename
                // theoretically, this should be the last index in groupedResults,
                // so loop backwards through groupedResults to search
                for (int j = groupedResults.size() - 1; j >= 0; j--) {
                    List<SumResult> thisGroupResults = groupedResults.get(j);
                    SumResult this_grouped_result = thisGroupResults.get(0);
                    if (this_grouped_result.file.getAbsolutePath().equals(this_result.file.getAbsolutePath())) {
                        // add this_result to proper place in groupedResults,
                        groupedResults.get(j).add(this_result);
                        // then break out of inner loop
                        break;
                    }//end if we found matching filename
                }//end looping backwards through groupedResults
                // at this point, we should have added this_result already
            }//end if we can add this file to an existing list in groupedResults
            else {
                List<SumResult> temp_result_list = new ArrayList<SumResult>();
                temp_result_list.add(this_result);
                groupedResults.add(temp_result_list);
                filenames_indexed.add(this_result.file.getAbsolutePath());
            }//end else we must add a new entry
        }//end looping through each sum result
        return groupedResults;
    }//end GroupResultsByFile
}//end struct SumResult

