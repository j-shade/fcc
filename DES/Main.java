// Written by Jeremy Shade, 15128706
// 28/03/2016
// Curtin University
// Fundamental Concepts of Cryptography

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

public class Main {

	/**
	 *
	 * @param args
     */
	public static void main(String[] args){
		String input = readFile("toencrypt.txt", false);
		String key = readFile("key.txt", false);

		System.out.println("Encrypting the message");
		byte[] encrypted = DES.encrypt(input.getBytes(), key.getBytes());
		writeFile(new String(encrypted), "encrypted.txt"); //save the file

		System.out.println("\nDecrypting message..");
	 	String toDecrypt = readFile("encrypted.txt", true);
		byte[] decrypted = DES.decrypt(toDecrypt.getBytes(), key.getBytes());
		writeFile(new String(decrypted), "decrypted.txt");
		// System.out.println("\nDecrypted message: \n" + new String(dec));
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
			// BufferedReader fileReader = new BufferedReader(
			// 	new InputStreamReader(
            //           new FileInputStream(toencrypt)
			// ));
			BufferedReader fileReader = new BufferedReader(file);
			while ((line = fileReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				// if ( !encrypted ){
				// 	stringBuffer.append("\n");
				// }
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
