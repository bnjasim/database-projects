import java.io.*;
import java.util.*;


class HashJoin {

	private boolean inverted;
	private Hashtable<String, List<String> > hTable = new Hashtable<String, List<String> >();  

	HashJoin(boolean inverted) {
		this.inverted = inverted;
	}

	public void open(String fileS) {
		// Create a HashTable over S
		// Location of Y attribute (first or second?)
		int locY = 0;
		if (inverted) {
			locY = 1;
		}

        try (BufferedReader br = new BufferedReader(new FileReader(fileS))) {
            String line;
            while ((line = br.readLine()) != null) {
            	// put to Hashtable
            	String key = line.split(" ")[locY];

            	if (hTable.get(key) == null) {
            		// First Time
            		List<String> temp = new ArrayList<String>();
            		temp.add(line);
            		hTable.put(key, temp);
            	}
            	else {
            		List<String> temp = hTable.get(key);
            		temp.add(line);
            		hTable.put(key, temp);

            	}
            }
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

	}

	public void getnext(String fileR) {
		// Load file R and check HashTable for each line of R
		// Location of Y attribute (first or second?)
		int locY = 1;
		if (inverted) {
			locY = 0;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(fileR))) {
            String line;
            while ((line = br.readLine()) != null) {
            	String key = line.split(" ")[locY];
            	// Check if line present in the HashTable
            	List<String> match = hTable.get(key);

            	if (match != null) {
            		System.out.println(match);
            		break;
            	}
            	else {
            		System.out.println("Key " + key + "Not found in the Hashtable");
            	}

            }
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
	}

	public static void main(String[] args) {
		
		if (args.length < 4) {
            System.out.println(args.length);
            System.out.println("The correct format is: java SortJoin input/R input/S sort/hash 50");
            System.exit(1);
        }

        int M = Integer.parseInt(args[3], 10);
        String fileR = args[0]; //"input/R";
        String fileS = args[1]; //"input/S";
        boolean inverted = false;

		try {
            File file1 = new File(args[0]);
            File file2 = new File(args[1]);
            // If file1 is the smaller one, swap
            if (file1.length() < file2.length()) {
            	fileR = args[1];
            	fileS = args[0];
            	inverted = true;
            }
        } 
        catch (Exception e) {
        	e.printStackTrace();
        }


		HashJoin hashJoin = new HashJoin(inverted);

		long startTime = System.currentTimeMillis();
        
        hashJoin.open(fileS);
        hashJoin.getnext(fileR);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
	}
}
	