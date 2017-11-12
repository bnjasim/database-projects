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
	public static Comparator<String> getComparator() {
        return new Comparator<String>() {
            public int compare(String s1, String s2) {
                String[] parts1 = s1.split(" ");
                String X1 = parts1[0]; // 004
                String Y1 = parts1[1]; // 034556

                String[] parts2 = s2.split(" ");
                String X2 = parts2[0]; // 004
                String Y2 = parts2[1]; // 034556

            	return Y1.compareTo(Y2);
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

    public void open(String fileR) {
        String outpath = "tmp/";

        int tupleLimit = tuplesPerBlock * M;
        int lineCount = 0;
        int fileCount = 1;

        String[] Data = new String[tupleLimit];

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
                    Arrays.sort(Data, getComparator());
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
            Arrays.sort(Data, getComparator());
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
           
        SortJoin sortJoin = new SortJoin(50);

		long startTime = System.currentTimeMillis();
        sortJoin.open(fileR);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);


	}
}