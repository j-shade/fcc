// Written by Jeremy Shade, 15128706
// 28/03/2016
// Curtin University
// Fundamental Concepts of Cryptography

public class DES {

	//hold one subkey
	private static byte[][] key;

	/**
	 *
	 * @param data
	 * @param inKey
     * @return
     */
	public static byte[] encrypt(byte[] data, byte[] inKey) {
		int lenght=0;
		byte[] padding;
		int i;
		lenght = 8 - data.length % 8;
		padding = new byte[lenght];
		padding[0] = (byte)0x80;
		System.out.println(new String(padding));

		for (i = 1; i < lenght; i++){
			padding[i] = 0;
		}

		byte[] tmp = new byte[data.length + lenght];
		byte[] block = new byte[8];

		key = generateSubKeys(inKey);

		int count = 0;
		//iterate over total length of file including padding
		for ( i = 0; i < (data.length + lenght); i++ ) {
			//iterate over 8 bit blocks
			if (i > 0 && i % 8 == 0) {
				System.out.println("i: " + i + " , " + new String(block));
				block = desCipher(block,key, false);
				System.arraycopy(block, 0, tmp, i - 8, block.length);
			}
			//if its just the data, append it to the block
			if (i < data.length) {
				block[i % 8] = data[i];
				// System.out.print((char)data[i]);
			//otherwise, use padding
			} else {
				block[i % 8] = padding[count % 8];
				count++;
			}
		}
		if ( block.length == 8 ){
			System.out.println("block: " + new String(block));
			block = desCipher(block,key, false);
			System.arraycopy(block, 0, tmp, i - 8, block.length);
		}
		return tmp;
	}

	/**
	 *
	 * @param data
	 * @param inKey
     * @return
     */
	public static byte[] decrypt(byte[] data, byte[] inKey) {
		int i;
		byte[] tmp = new byte[data.length];
		byte[] block = new byte[8];

		key = generateSubKeys(inKey);

		for ( i = 0; i < data.length; i++ ) {
			if (i > 0 && i % 8 == 0) {
				block = desCipher(block,key, true);
				System.out.println("i: " + i + " , " + new String(block));
				System.arraycopy(block, 0, tmp, i - 8, block.length);
			}
			if (i < data.length)
				block[i % 8] = data[i];
		}
		block = desCipher(block,key, true);
		System.out.println("block: " + new String(block));
		System.arraycopy(block, 0, tmp, i - 8, block.length);

		tmp = removePadding(tmp);

		return tmp;
	}

	/**
	 *
	 * @param block
	 * @param inKeys
	 * @param decrypt
     * @return
     */
	private static byte[] desCipher(byte[] block,byte[][] inKey, boolean decrypt) {
		byte[] tmp = new byte[block.length];
		byte[] L = new byte[block.length / 2];
		byte[] R = new byte[block.length / 2];

		tmp = permute(block, initPerm);

		L = getBits(tmp, 0, initPerm.length/2);
		R = getBits(tmp, initPerm.length/2, initPerm.length/2);

		for (int i = 0; i < 16; i++) {
			byte[] tmpR = R;
			if( decrypt ) {
				R = feistel(R, inKey[15-i]);
			} else {
				R = feistel(R,inKey[i]);
			}
			R = xor(L, R);
			L = tmpR;
		}

		tmp = mergeBits(R, initPerm.length/2, L, initPerm.length/2);

		tmp = permute(tmp, initPermInverse);
		return tmp;
	}

	/**
	 *
	 * @param R
	 * @param key
     * @return
     */
	private static byte[] feistel(byte[] R, byte[] key) {
		byte[] tmp;
		tmp = permute(R, eTable);
		tmp = xor(tmp, key);
		tmp = shift(tmp);
		tmp = permute(tmp, feistelPerm);
		return tmp;
	}

	/**
	 *
	 * @param in
	 * @return
     */
	private static byte[] shift(byte[] in) {
		in = separateBytes(in, 6);
		byte[] out = new byte[in.length / 2];
		int halfByte = 0;
		for (int b = 0; b < in.length; b++) {
			byte valByte = in[b];
			//if 1000001 -> grabs row from first and last digit by shifting
			int r = 2 * (valByte >> 7 & 0x0001) + (valByte >> 2 & 0x0001);
			//grab middle 4 values
			int c = valByte >> 3 & 0x000F;
			//get value from s-box
			int val = sBoxes[b][r][c];
			if (b % 2 == 0)
				halfByte = val;
			else
				out[b / 2] = (byte) (16 * halfByte + val);
		}
		return out;
	}

	/**
	 *
	 * @param in
	 * @param len
     * @return
     */
	private static byte[] separateBytes(byte[] in, int len) {
		int numOfBytes = (8 * in.length - 1) / len + 1;
		byte[] out = new byte[numOfBytes];
		for (int i = 0; i < numOfBytes; i++) {
			for (int j = 0; j < len; j++) {
				int val = getBit(in, len * i + j);
				setBit(out, 8 * i + j, val);
			}
		}
		return out;
	}

	/**
	 *
	 * @param a
	 * @param aLen
	 * @param b
	 * @param bLen
     * @return
     */
	private static byte[] mergeBits(byte[] a, int aLen, byte[] b, int bLen) {
		int numOfBytes = (aLen + bLen - 1) / 8 + 1;
		byte[] out = new byte[numOfBytes];
		int j = 0;
		for (int i = 0; i < aLen; i++) {
			int val = getBit(a, i);
			setBit(out, j, val);
			j++;
		}
		for (int i = 0; i < bLen; i++) {
			int val = getBit(b, i);
			setBit(out, j, val);
			j++;
		}
		return out;
	}

	/**
	 *
	 * @param input
	 * @return
     */
	private static byte[] removePadding(byte[] input) {
		int count = 0;
		int i = input.length - 1;
		while (input[i] == 0) {
			count++;
			i--;
		}
		byte[] tmp = new byte[input.length - count - 1];
		System.arraycopy(input, 0, tmp, 0, tmp.length);
		return tmp;
	}

	/**
	 *
	 * @param key
	 * @return
     */
	private static byte[][] generateSubKeys(byte[] key) {
		byte[][] tmp = new byte[16][];
		byte[] tmpK = permute(key, permChoice1);

		byte[] C = getBits(tmpK, 0, permChoice1.length/2);
		byte[] D = getBits(tmpK, permChoice1.length/2, permChoice1.length/2);

		for (int i = 0; i < 16; i++) {

			C = rotateLeft(C, 28, keyShift[i]);
			D = rotateLeft(D, 28, keyShift[i]);

			byte[] cd = mergeBits(C, 28, D, 28);

			tmp[i] = permute(cd, permChoice2);
		}

		return tmp;
	}

	/**
	 *
	 * @param data
	 * @param pos
	 * @param val
     */
	private static void setBit(byte[] data, int pos, int val) {
		int posByte = pos / 8;
		int posBit = pos % 8;
		byte tmpByte = data[posByte];
		tmpByte = (byte) (((0xFF7F >> posBit) & tmpByte) & 0x00FF);
		byte newByte = (byte) ((val << (8 - (posBit + 1))) | tmpByte);
		data[posByte] = newByte;
	}

	/**
	 *
	 * @param data
	 * @param pos
     * @return
     */
	private static int getBit(byte[] data, int pos) {
		int posByte = pos / 8;
		int posBit = pos % 8;
		byte tmpByte = data[posByte];
		int bit = tmpByte >> (8 - (posBit + 1)) & 0x0001;
		return bit;
	}

	/**
	 *
	 * @param input
	 * @param len
	 * @param pas
     * @return
     */
	private static byte[] rotateLeft(byte[] input, int len, int pas) {
		int numBytes = (len - 1) / 8 + 1;
		byte[] out = new byte[numBytes];
		for (int i = 0; i < len; i++) {
			int val = getBit(input, (i + pas) % len);
			setBit(out, i, val);
		}
		return out;
	}

	/**
	 *
	 * @param input
	 * @param pos
	 * @param n
     * @return
     */
	private static byte[] getBits(byte[] input, int pos, int n) {
		int numOfBytes = (n - 1) / 8 + 1;
		byte[] out = new byte[numOfBytes];
		for (int i = 0; i < n; i++) {
			int val = getBit(input, pos + i);
			setBit(out, i, val);
		}
		return out;

	}

	/**
	 *
	 * @param input
	 * @param table
     * @return
     */
	private static byte[] permute(byte[] input, int[] table) {
		int numBytes = (table.length - 1) / 8 + 1;
		byte[] out = new byte[numBytes];
		for (int i = 0; i < table.length; i++) {
			int val = getBit(input, table[i] - 1);
			setBit(out, i, val);
		}
		return out;

	}

	/**
	 *
	 * @param a
	 * @param b
     * @return
     */
	private static byte[] xor(byte[] a, byte[] b) {
		byte[] out = new byte[a.length];
		for (int i = 0; i < a.length; i++) {
			out[i] = (byte) (a[i] ^ b[i]);
		}
		return out;

	}

	///
	// Defining constants and tables relevant to the DES cipher.
	// Data and algorithms obtained from the document below:
	// http://csrc.nist.gov/publications/fips/fips46-3/fips46-3.pdf
	// Refer by page numbers below..
	///

	// initial permutation table (Page 10)
	private static int[] initPerm =
	{
		58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36,
		28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32,
		24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19,
		11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
	};
	// inverse initial permutation (Page 10)
	private static int[] initPermInverse =
	{
		40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47,
		15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13,
		53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51,
		19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17,
		57, 25
	};
	// Permutation P for Feistel (Page 18)
	private static int[] feistelPerm =
	{
		16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5,
		18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4,
		25
	};
	// Initial key permutation 64bits => 56 bits (Page 19)
	private static int[] permChoice1 =
	{
		57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34,
		26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63,
		55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53,
		45, 37, 29, 21, 13, 5, 28, 20, 12, 4
	};
	// Key permutation at round ii 56bits => 48bits (Page 21)
	private static int[] permChoice2 =
	{
		14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
		23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55,
		30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29,
		32
	};
	// left key shift for each round (Page 21)
	private static int[] keyShift =
	{
		1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2,
		2, 1
	};
	// E Bit-Selection Table (Page 13)
	private static int[] eTable =
	{
		32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8,
		9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21,
		20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,
		1
	};
	// S-Boxes (Pages 17-18)
	private static int[][][] sBoxes =
	{
		{   { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
		    { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
		    { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
		    { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }},
		{   { 15, 1, 8, 14, 6, 11, 3, 2, 9, 7, 2, 13, 12, 0, 5, 10 },
		    { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
		    { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
		    { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }},
		{   { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
		    { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
		    { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
		    { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }},
		{   { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
		    { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
		    { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
		    { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }},
		{   { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
		    { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
		    { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
		    { 11, 8, 12, 7, 1, 14, 2, 12, 6, 15, 0, 9, 10, 4, 5, 3 }},
		{   { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
		    { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
		    { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
		    { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }},
		{   { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
		    { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
		    { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
		    { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }},
		{   { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
		    { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
		    { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
		    { 2, 1, 14, 7, 4, 10, 18, 13, 15, 12, 9, 0, 3, 5, 6, 11 }}
	};
}
