import java.io.*;
import java.util.*;


class SortJoin {
	

	public static Comparator<byte[]> decending(final Comparator<byte[]> other) {
        return new Comparator<byte[]>() {
            public int compare(byte[] o1, byte[] o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

	public static Comparator<byte[]> getComparator(final int... multipleOptions) {
        return new Comparator<byte[]>() {
            public int compare(byte[] o1, byte[] o2) {
            	byte[] c1, c2;
            	String s1, s2;
            	int result = 0;

                for (int option : multipleOptions) {
             
                	// sort by first column
                	if (option == 0) { 
                    	c1 = new byte[10];
                    	c2 = new byte[10];
                    	for (int i=0; i<10; i++) { 
                    		// copy first 10 bytes 
                    		c1[i] = o1[i];
                    		c2[i] = o2[i];
                    	}

                    	s1 = new String(c1);
                    	s2 = new String(c2);

                    	result = s1.compareTo(s2);

                    	if (result != 0) {
                        return result;
                    	}
                    }	

                    // sort by second column
                    if (option == 1) { 
                    	c1 = new byte[32];
                    	c2 = new byte[32];
                    	for (int i=11; i<43; i++) { 
                    		// copy first 10 bytes 
                    		c1[i] = o1[i];
                    		c2[i] = o2[i];
                    	}

                    	s1 = new String(c1);
                    	s2 = new String(c2);

                    	result = s1.compareTo(s2);
                    	
                    	if (result != 0) {
                        return result;
                    	}
                    }	

                    // sort by third column
                    if (option == 2) { 
                    	c1 = new byte[54];
                    	c2 = new byte[54];
                    	for (int i=44; i<98; i++) { 
                    		// copy first 10 bytes 
                    		c1[i] = o1[i];
                    		c2[i] = o2[i];
                    	}

                    	s1 = new String(c1);
                    	s2 = new String(c2);

                    	result = s1.compareTo(s2);
                    	
                    	if (result != 0) {
                        return result;
                    	}
                    }	
                }
                return result;
            }
        };
    }


	public static void main(String[] args) {
		String filepath = "input/R";
        String outpath = "input/output";
        PrintWriter pw;

        try {
		  PrintWriter pw = new PrintWriter(new FileWriter(outpath));
        }

		// BufferedWriter fout;
        int lineCount = 0;

		long startTime = System.currentTimeMillis();
		

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
            // process the line.
                if (lineCount < 10) {
                    System.out.println(line);
                }
                lineCount += 1;
			}

            // Now Sort Data
            //Arrays.sort(Data, getComparator(0));

            // Save the sorted Data
            
            
            /*for (int i=0; i<total_num_records; i++) {
            	fout.write(Data[i]);
            }

            fout.flush();*/
            pw.close();

            
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