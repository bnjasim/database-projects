import java.io.*;
import java.util.*;


public class Join {

	public static void main(String[] args) {
        // TODO - command line inputs
        if (args.length < 4) {
            System.out.println(args.length);
            System.out.println("The correct format is: java Join input/R input/S sort/hash M");
            System.exit(1);
        }

        int M = 50; // default
		String fileR = args[0]; //"input/R";
        String fileS = args[1]; //"input/S";

        M = Integer.parseInt(args[3], 10);

		long startTime = System.currentTimeMillis();

        if (args[2] == "sort") {
            SortJoin sortJoin = new SortJoin(M);
            sortJoin.open(fileR, fileS);
            sortJoin.getnext();
        }
        else { // hash join
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
            hashJoin.open(fileS);
            hashJoin.getnext(fileR);

        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Execution Time: " + totalTime);


	}
}