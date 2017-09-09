import java.io.*;



class TwoPhaseMergeSort {
	
	public static void main(String[] args) {
		String filepath = "input/input-500mb.txt";
		InputStream fin;
		int bufferSize = 1024 * 64;

		byte[] contents = new byte[1024*64];
		int bytesRead=0;
		String data;

		long startTime = System.currentTimeMillis();
		


		try {
			fin = new BufferedInputStream(new FileInputStream(filepath), bufferSize);


			while( (bytesRead = fin.read(contents)) != -1) {                  
	            //data = new String(contents, 0, bytesRead);
	            // System.out.print(data);
            }

            long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime);


			// System.out.println(fin.read());
		}

		catch(Exception e){
            System.out.println("Failed to open the Input File");
            System.exit(1);
        }


	}
}