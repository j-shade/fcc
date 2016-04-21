// Written by Jeremy Shade, 15128706
// 28/03/2016
// Curtin University
// Fundamental Concepts of Cryptography

import java.io.*;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class Main {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args){
		try{
			byte[] input = readFile(new File("testfile_SDES.txt"));
			byte[] key = readFile(new File("key.txt"));

			System.out.println("Encrypting the message..");
			byte[] encrypted = DES.encrypt(input, key);
			writeFile(encrypted, "encrypted.txt"); //save the file
			System.out.println("Done.");

			System.out.println("\nDecrypting the message..");
			byte[] toDecrypt = readFile(new File("encrypted.txt"));
			byte[] decrypted = DES.decrypt(toDecrypt, key);
			writeFile(decrypted, "decrypted.txt");
			System.out.println("Done.");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param file
	 * @return byte[] data
	 */
	private static byte[] readFile(File file) throws IOException {
		// Open file
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			// Get and check length
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength)
				throw new IOException("File size >= 2 GB");
			// Read file and return data
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		}
		finally {
			f.close();
		}
	}

	/**
	 * @param fileName
	 * @param toWrite
	 */
	private static void writeFile(byte[] toWrite, String fileName)
	{
		FileOutputStream fileStream = null;
		File file;

		try {
			file = new File(fileName);
			fileStream = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fileStream.write(toWrite);
			fileStream.flush();
			fileStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
