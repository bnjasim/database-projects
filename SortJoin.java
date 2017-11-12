import java.io.*;
import java.util.*;


class SortJoin {
	

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


	public static void main(String[] args) {
		String filepath = "input/R";
        String outpath = "input/output";
        PrintWriter writer = null;
        String[] Data = new String[1000];

        // Open the Output File
        try {
		  writer = new PrintWriter(new FileWriter(outpath));
        }
        catch(IOException e){
            System.out.println("Failed to open the Output File" + e);
            System.exit(1);
        }
        catch(Exception e){
            System.out.println(e);
            System.exit(1);
        }

		// BufferedWriter fout;
        int lineCount = 0;

		long startTime = System.currentTimeMillis();
		

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < 1000) {
                    Data[lineCount] = line;
                }
                lineCount += 1;
			}

            System.out.println("# records = " + lineCount);
            // Now Sort Data
            Arrays.sort(Data, getComparator());

            // Save the sorted Data
            
            
            for (int i=0; i<1000; i++) {
            	writer.println(Data[i]);
            }

            //fout.flush();
            writer.close();

            
            //System.out.println("col1: " + new String(col1));
            //System.out.println("Bytes Read: " + bytesRead);

            long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime);


			// System.out.println(fin.read());
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
}