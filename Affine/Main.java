import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;


public class Main
{
    public static void main(String[] args)
    {
        int a = 5; //default test values
        int b = 21; //default test values

        String chooseKeys = ConsoleInput.readString("Would you like to set a and b? (y/n)");
        String encrypted = "";
        String decrypted = "";

        if (chooseKeys.equals("y"))
        {
            System.out.println("Possible keys for a: ");
            Affine.possibleKeys(-1);
            //get a
            a = ConsoleInput.readInt("\n\nPlease enter a valid value for a: ");
            if ( !Affine.checkKey(a, -1) ){
                throw new IllegalArgumentException(a + " is not valid as a key!");
            }
            System.out.println("\nPossible keys for b: ");
            Affine.possibleKeys(a);
            b = ConsoleInput.readInt("\n\nPlease enter a valid value for b: ");
            if ( !Affine.checkKey(-1, b) ){
                throw new IllegalArgumentException(b + " is not valid as a key!");
            }
        }

        //ENCRYPTION//
        String toEncrypt = readFile("testfile_affine_cipher.txt", false);
        System.out.println("\n\n\nEncrypting file.....");
        encrypted = Affine.encrypt(toEncrypt, a, b);
        writeFile(new String(encrypted), "encrypted.txt"); //save the file

        //DECRYPTION//
        String toDecrypt = readFile("encrypted.txt", true);
        System.out.println("\n\n\nDecrypting file.....");
        decrypted = Affine.decrypt(toDecrypt, a, b);
        writeFile(new String(decrypted), "decrypted.txt");//save the file
        System.out.println("\n\n\nDone!");

        System.out.println("Graph of original file:");
        printGraph("testfile_affine_cipher.txt");
    }

    /**
     *
     * @param inFile
	 * @param encrypted
     * @return
     */
	private static String readFile(String inFile, boolean encrypted)
	{
		StringBuffer stringBuffer = new StringBuffer();
		String line;

		try{
			File toencrypt = new File(inFile);
			FileReader file = new FileReader(toencrypt);
			BufferedReader fileReader = new BufferedReader(
				new InputStreamReader(
                      new FileInputStream(toencrypt)
			));
			// BufferedReader fileReader = new BufferedReader(file);
			while ((line = fileReader.readLine()) != null) {
				System.out.println(line);
				stringBuffer.append(line);
                stringBuffer.append("\n");
			}
            int last = stringBuffer.lastIndexOf("\n");
            if (last >= 0)
            {
                stringBuffer.delete(last, stringBuffer.length());
            }
			fileReader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}

    /**
     * @param fileName
     * @param toWrite
     */
	private static void writeFile(String toWrite, String fileName)
	{
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
			    new FileOutputStream(fileName)));
			// BufferedWriter writer = new BufferedWriter(new FileWriter("encrypted.txt"));
			writer.write(toWrite);
			writer.close();
		}
		catch ( IOException e) {
			e.printStackTrace();
		}
	}

    private static void printGraph(String fileName){
        String fileContents = "";
        int[] charCount = new int[256];
        try {
            FileReader file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            StringBuilder builder = new StringBuilder();
            String line;
            // System.out.println("File contents: ");
            // System.out.println();
            while ((line = reader.readLine()) != null){
                for (int ii=0; ii<line.length(); ii++){
                    int charNum = (int)line.charAt(ii);
                    charCount[charNum]++;
                }
                // System.out.println(line);
            }
        } catch (IOException e){
            System.err.format("%s\n", e);
        }
        System.out.println();
        System.out.println("Now the graph: ");
        System.out.println();
        System.out.println("Char | Count \t | ");
        System.out.println("..................................................");
        int maxCount = 0;
        for (int ii=0; ii<127; ii++){
            if (charCount[ii] != 0){
                String numHash = "";
                if (charCount[ii] >= maxCount){
                    maxCount = charCount[ii];
                }
                for (int jj=0; jj<charCount[ii]; jj++){
                    numHash += "# ";
                }
                System.out.println((char)ii +"    | " + charCount[ii] + " \t | " + numHash);
                // System.out.println("char: " + (char)ii + ", char Count: " + charCount[ii]);
            }
        }
        System.out.println("..................................................");
    }
}
