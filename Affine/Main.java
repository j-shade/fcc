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
            if ( !Affine.checkKey(a,b) ){
                throw new IllegalArgumentException(a + " and " + b + " are not coprime.");
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
}
