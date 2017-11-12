import java.io.*;
import java.util.*;


class SortJoin {
	
    private final int tuplesPerBlock = 100;
    private int M = 50; // Number of blocks in main memory

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

	public static void main(String[] args) {
        // TODO - command line inputs
        int M = 50;
		String fileR = "input/R";
        String fileS = "input/S";
           
        SortJoin sortJoin = new SortJoin(50);

		long startTime = System.currentTimeMillis();
        
        sortJoin.open(fileR, fileS);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);


	}
}