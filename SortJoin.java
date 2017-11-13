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

    private void write_to_file(List<String> Data, String outfile, int num_lines) {
        // write to output file
        // Open the Output File
        PrintWriter writer = null;

        try {
            
            writer = new PrintWriter(new FileWriter(outfile));

            // Save the sorted Data 
            for (int i=0; i<num_lines; i++) {
                writer.println(Data.get(i));
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

        // String[] Data = new String[tupleLimit];
        List<String> Data = new ArrayList<String>();

        // For the Relation R
        try (BufferedReader br = new BufferedReader(new FileReader(fileR))) {
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < tupleLimit) {
                    Data.add(line);

                }
                else {

                    String outfile = outpath + "R" + fileCount;
                    // Now Sort Data
                    //Arrays.sort(Data, getComparator(1));
                    Data.sort(getComparator(1));
                    write_to_file(Data, outfile, lineCount);

                    // reset line count
                    lineCount = 0;
                    // We shouldn't miss the current line just read
                    Data.clear();
                    Data.add(line);
                    //Data[0] = line;
                    fileCount += 1;

                }
                lineCount += 1;
            }

            // The last file also need to be written
            String outfile = outpath + "R" + fileCount;
            // R Files Count
            RFC = fileCount;

            System.out.println("length of Data = "+ Data.size());
            // Now Sort Data
            //Arrays.sort(Data, getComparator(1));
            Data.sort(getComparator(1));
            write_to_file(Data, outfile, lineCount);

            // System.out.println("# tupleLimit = " + tupleLimit);

        }

        catch(IOException e){
            System.out.println("Failed to open the Input File" + e);
            System.exit(1);
        }

        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }


        // For the Relation S
        lineCount = 0;
        fileCount = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(fileS))) {
            String line;
            Data.clear();
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < tupleLimit) {
                    Data.add(line);
                }
                else {

                    String outfile = outpath + "S" + fileCount;
                    // Now Sort Data
                    //Arrays.sort(Data, getComparator(0));
                    Data.sort(getComparator(0));
                    write_to_file(Data, outfile, lineCount);

                    // reset line count
                    lineCount = 0;
                    // We shouldn't miss the current line just read
                    Data.clear();
                    Data.add(line);
                    fileCount += 1;

                }
                lineCount += 1;
            }

            // The last file also need to be written
            String outfile = outpath + "S" + fileCount;
            // S Files Count 
            SFC = fileCount;
            // Now Sort Data
            //Arrays.sort(Data, getComparator(0));
            Data.sort(getComparator(0));
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
    private int find_minimum(String[] tupleR, boolean[] finishedR) {
        // String m = arr[0]; // can't do as first sublist may be finished
        String m = "";
        int index = -1;
        int i =0;

        for (; i<tupleR.length; i++) {
            if (!finishedR[i]) {
                m = tupleR[i];
                index = i;
                break;
            }
        }
        i += 1;

        //System.out.println("m = " + m);

        for (; i<tupleR.length; i++) {
            if (!finishedR[i]) {
                if (compare_R_R(tupleR[i], m) < 0) {
                //if (getComparator(col).compare(tupleR[i], m) < 0) {
                    m = tupleR[i];
                    index = i;
                }
            }
        }
        return index;
    }

    // Compare second column of R with first column of S 
    private int compare_R_S(String r, String s) {
        return r.split(" ")[1].compareTo(s.split(" ")[0]);
    }

    // Compare second column of R with second column of R for two different tuples
    private int compare_R_R(String r, String s) {
        return r.split(" ")[1].compareTo(s.split(" ")[1]);
    }

    // R(X, Y) join S(Y, Z) result: Q(X, Y, Z)
    private String join_tuples(String r, String s) {
        return r + " " + s.split(" ")[1];
    }

    private void write_cross_product(PrintWriter writer, List<String> listR, List<String> listS) {
        for (int i=0; i<listR.size(); i++) {
            for (int j=0; j<listS.size(); j++) {
                writer.println(join_tuples(listR.get(i), listS.get(j)));
            }
        }
        writer.flush();
    }

    public void getnext() {

        // Check if B(S) + B(R) <= M^2
        if (RFC + SFC > M) {
            System.out.println("B(S) + B(R) must be less than or equal to M^2");
            System.exit(1);
        }

        PrintWriter writer = null;

        // This is to keep all tuples of same Y and to take cross product
        List<String> listR = new ArrayList<String>();
        List<String> listS = new ArrayList<String>();
        

        try {

            // Open the output file
            writer = new PrintWriter(new FileWriter("input/R_join_S"));
            // Open all the split files
            BufferedReader[] readerR = new BufferedReader[RFC];
            BufferedReader[] readerS = new BufferedReader[SFC];
            // tuple* keeps the topmost tuple of all the split files
            String[] tupleR = new String[RFC];
            String[] tupleS = new String[SFC];
            // keep flags whether the pointers reached end of file
            boolean[] finishedR = new boolean[RFC];
            boolean[] finishedS = new boolean[SFC];


            // Initially read 1 tuple from each of the R and S files
            for (int i=0; i<RFC; i++) {
                String fname = "tmp/R" + (i+1);
                readerR[i] = new BufferedReader(new FileReader(fname));
                tupleR[i] = readerR[i].readLine();
                
                if (tupleR[i] == null) {
                    finishedR[i] = true;
                }
                else {
                    finishedR[i] = false;
                }
            }
            
            for (int i=0; i<SFC; i++) {
                String fname = "tmp/S" + (i+1);
                readerS[i] = new BufferedReader(new FileReader(fname));
                tupleS[i] = readerS[i].readLine();
                
                if (tupleS[i] == null) {
                    finishedS[i] = true;
                }
                else {
                    finishedS[i] = false;
                }
            }

            // finished become true only when all finishedR become true or all finishedS become true
            boolean finished = false;

            int iteration = 1;

            while(!finished) {

                // Find the minimum y in R
                int min_index = find_minimum(tupleR, finishedR);
                String y = tupleR[min_index];
                
                // Add all tuples with Y=y from R to listR
                for (int i=0; i<RFC; i++) {
                    while(!finishedR[i]) {
                        int comp = compare_R_R(y, tupleR[i]);

                        if (comp == 0) {
                            listR.add(tupleR[i]);
                            // increment pointer - read next line
                            tupleR[i] = readerR[i].readLine();

                            if (tupleR[i] == null) {
                                finishedR[i] = true;
                            }

                        }

                        else if (comp < 0) {
                            break;
                        }

                        else {
                            // something seriously wrong
                            // y is the minimum assumption is wrong or S is not sorted
                            System.out.println("iteration = " + iteration);
                            System.out.println("y = " + y + "  R = " + tupleR[i]);
                            System.out.println("Fatal Mistake: y is not minimum\n");
                            System.exit(1);
                        }
                    }
                }

                iteration += 1;
                // System.out.println("Number of tuples in R with 000: " + listR.size());

                // Add all tuples with Y=y from S to listS
                for (int i=0; i<SFC; i++) {
                    while(!finishedS[i]) {
                        int comp = compare_R_S(y, tupleS[i]);

                        if (comp == 0) {
                            listS.add(tupleS[i]);
                            // increment pointer - read next line
                            tupleS[i] = readerS[i].readLine();

                            if (tupleS[i] == null) {
                                finishedS[i] = true;
                            }

                        }

                        else if (comp < 0) {
                            break;
                        }

                        else {
                            // S is smaller than y, which is possible
                            // skip
                            tupleS[i] = readerS[i].readLine();

                            if (tupleS[i] == null) {
                                finishedS[i] = true;
                            }
                        }
                    }
                }

               //  System.out.println("Number of tuples in S with 000: " + listS.size());

                // Cross product of listR and listS
                write_cross_product(writer, listR, listS);
                // clear the arrraylists
                listR.clear();
                listS.clear();

                // Check if finished or not
                boolean t1 = true, t2 = true;
                for (int i=0; i<RFC; i++) {
                    if (!finishedR[i]) {
                        t1 = false;
                        break;
                    }
                }

                for (int i=0; i<SFC; i++) {
                    if (!finishedS[i]) {
                        t2 = false;
                        break;
                    }
                }

                finished = t1 | t2;

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
        if (args.length < 4) {
            System.out.println(args.length);
            System.out.println("The correct format is: java SortJoin input/R input/S sort 50");
            System.exit(1);
        }

        int M = 50; // default
		String fileR = args[0]; //"input/R";
        String fileS = args[1]; //"input/S";

        M = Integer.parseInt(args[3], 10);
           
        SortJoin sortJoin = new SortJoin(M);

		long startTime = System.currentTimeMillis();
        
        sortJoin.open(fileR, fileS);
        sortJoin.getnext();

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);


	}
}