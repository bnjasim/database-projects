import java.io.*;
import java.util.*;


class SortJoin {
	
    private final int tuplesPerBlock = 100;
    private int M = 50; // Number of blocks in main memory
    private int RFC = 20; // Number of split files in R
    private int SFC = 12; // Number of split files in S

    // Constructor
    SortJoin(int M) {
        if (M > 0) {
            this.M = M;
        }
    }
	public static Comparator<String> getComparator(final int attr) {
        return new Comparator<String>() {
            public int compare(String s1, String s2) {
                String[] parts1 = s1.split(" ");
                //String X1 = parts1[0]; // 004
                //String Y1 = parts1[1]; // 034556

                String[] parts2 = s2.split(" ");
                //String X2 = parts2[0]; // 004
                //String Y2 = parts2[1]; // 034556

            	//return Y1.compareTo(Y2);

                // Safety code: attr can be 0 or 1 as there are only 2 attributes
                if (attr > 1 || attr <0) {
                    return 0;
                }

                return parts1[attr].compareTo(parts2[attr]);
            }
        };
    }

    private void write_to_file(String[] Data, String outfile, int num_lines) {
        // write to output file
        // Open the Output File
        PrintWriter writer = null;

        try {
            
            writer = new PrintWriter(new FileWriter(outfile));

            // Save the sorted Data 
            for (int i=0; i<num_lines; i++) {
                writer.println(Data[i]);
            }

            writer.flush();
            writer.close();
            // System.out.println("i = " + i);
        }
        catch(IOException e){
            System.out.println("Failed to open the Output File" + outfile + "\n" + e);
            System.exit(1);
        }
        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }

    public void open(String fileR, String fileS) {
        String outpath = "tmp/";

        int tupleLimit = tuplesPerBlock * M;
        int lineCount = 0;
        int fileCount = 1;

        String[] Data = new String[tupleLimit];

        // For the Relation R
        try (BufferedReader br = new BufferedReader(new FileReader(fileR))) {
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < tupleLimit) {
                    Data[lineCount] = line;
                }
                else {

                    String outfile = outpath + "R" + fileCount;
                    // Now Sort Data
                    Arrays.sort(Data, getComparator(1));
                    write_to_file(Data, outfile, lineCount);

                    // reset line count
                    lineCount = 0;
                    // We shouldn't miss the current line just read
                    Data[0] = line;
                    fileCount += 1;

                }
                lineCount += 1;
            }

            // The last file also need to be written
            String outfile = outpath + "R" + fileCount;
            // R Files Count
            RFC = fileCount;
            // Now Sort Data
            Arrays.sort(Data, getComparator(1));
            write_to_file(Data, outfile, lineCount);

            // System.out.println("# tupleLimit = " + tupleLimit);

        }

        catch(IOException e){
            System.out.println("Failed to open the Input File" + e);
            System.exit(1);
        }

        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }


        // For the Relation S
        lineCount = 0;
        fileCount = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(fileS))) {
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < tupleLimit) {
                    Data[lineCount] = line;
                }
                else {

                    String outfile = outpath + "S" + fileCount;
                    // Now Sort Data
                    Arrays.sort(Data, getComparator(0));
                    write_to_file(Data, outfile, lineCount);

                    // reset line count
                    lineCount = 0;
                    // We shouldn't miss the current line just read
                    Data[0] = line;
                    fileCount += 1;

                }
                lineCount += 1;
            }

            // The last file also need to be written
            String outfile = outpath + "S" + fileCount;
            // S Files Count 
            SFC = fileCount;
            // Now Sort Data
            Arrays.sort(Data, getComparator(0));
            write_to_file(Data, outfile, lineCount);

            System.out.println("# tupleLimit = " + tupleLimit);

        }

        catch(IOException e){
            System.out.println("Failed to open the Input File" + e);
            System.exit(1);
        }

        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }

    }

    // Returns the index of which file in R splits having the minimum value
    private int find_minimum(String[] arr, int col) {
        String m = arr[0];
        int index = 0;

        for (int i=1; i<arr.length; i++) {
            // System.out.println("i = " + i);
            //if (arr[i].compareTo(m) < 0) {
            if (getComparator(col).compare(arr[i], m) < 0) {
                m = arr[i];
                index = i;
            }

            
        }
        return index;
    }

    // Compare second column of R with first column of S 
    private int compare_R_S(String r, String s) {
        return r.split(" ")[1].compareTo(s.split(" ")[0]);
    }

    // R(X, Y) join S(Y, Z) result: Q(X, Y, Z)
    private String join_tuples(String r, String s) {
        return r + " " + s.split(" ")[1];
    }

    public void getnext() {
        // Check if B(S) + B(R) <= M^2
        if (RFC + SFC > M) {
            System.out.println("B(S) + B(R) must be less than or equal to M^2");
            System.exit(1);
        }

        PrintWriter writer = null;

        try {

            // Open the output file
            writer = new PrintWriter(new FileWriter("input/R_join_S"));
            // Open all the split files
            BufferedReader[] readerR = new BufferedReader[RFC];
            BufferedReader[] readerS = new BufferedReader[SFC];
            // tuple* keeps the topmost tuple of all the split files
            String[] tupleR = new String[RFC];
            String[] tupleS = new String[SFC];

            // Initially read 1 tuple from each of the R and S files
            for (int i=0; i<RFC; i++) {
                String fname = "tmp/R" + (i+1);
                readerR[i] = new BufferedReader(new FileReader(fname));
                tupleR[i] = readerR[i].readLine();

            }
            
            for (int i=0; i<SFC; i++) {
                String fname = "tmp/S" + (i+1);
                readerS[i] = new BufferedReader(new FileReader(fname));
                tupleS[i] = readerS[i].readLine();

            }

            // Find the minimum y in R
            int min_index = find_minimum(tupleR, 1);
            String y = tupleR[min_index];
            // System.out.println("min index = " + min_index);
            for (int i=0; i<SFC; i++) {
                while(true) {
                    int comp = compare_R_S(y, tupleS[i]);
                    // If equal then join
                    if (comp == 0) {
                        // write to output file
                        writer.println(join_tuples(y, tupleS[i]));
                        // increment pointer, read next line
                        tupleS[i] = readerS[i].readLine();
                    }
                    else if (comp > 0) {
                        // meaning S is smaller than R, then drop that tuple in S
                        tupleS[i] = readerS[i].readLine();
                    }
                    else {
                        break;
                    }
                }
            }

            writer.flush();
            writer.close();

        }

        catch(IOException e){
            System.out.println("Failed to open the Split files" + e);
            System.exit(1);
        }

        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }


    }

	public static void main(String[] args) {
        // TODO - command line inputs
        int M = 50;
		String fileR = "input/R";
        String fileS = "input/S";
           
        SortJoin sortJoin = new SortJoin(50);

		long startTime = System.currentTimeMillis();
        
        // sortJoin.open(fileR, fileS);
        sortJoin.getnext();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);


	}
}