    // Written by Jeremy Shade, 15128706
// 28/03/2016
// Curtin University
// Fundamental Concepts of Cryptography

import java.math.BigInteger;
import java.util.Arrays;

public class Affine
{
    ////
    //
    //
    //
    ////
    private static final int MODULO = 128; //95 for all lower case, upper case letters, numbers and symbols
    private static final int STARTNUM = 0;
    private static final int ENDNUM = 127;
    private static int[] validKeys = new int[MODULO];

    ////
    //
    //
    //
    ////
    public static String encrypt(String input, int a, int b) {
        String encrypted = "";
        if ( a > 0 && b > 0 && input.length() > 0){
            System.out.println("\nEncrypting with a = " + a + ", b = " + b);
            encrypted = doEncryption(input, a, b);
            System.out.println("Encrypted text:");
            System.out.println(encrypted);
        } else{
            throw new IllegalArgumentException("Either a or b or less than 0, OR input is empty");
        }
        return encrypted;
    }


    ////
    //
    //
    //
    ////
    public static String decrypt(String input, int a, int b) {
        String decrypted = "";
        if ( a > 0 && b > 0 && input.length() > 0){
            System.out.println("\nDecrypting with a = " + a + ", b = " + b);
            decrypted = doDecryption(input, a, b);
            System.out.println("Decrypted text:");
            System.out.println(decrypted);
        } else{
            throw new IllegalArgumentException("Either a or b or less than 0, OR the input is empty");
        }

        return decrypted;
    }

    ////
    //
    //
    //
    ////
    private static String doDecryption(String input, int a, int b) {
        StringBuilder builder = new StringBuilder();
        // compute a^-1 aka "modular inverse"
        BigInteger inverse = BigInteger.valueOf(a).modInverse(BigInteger.valueOf(MODULO));
        // perform actual decryption
        for (int ii = 0; ii < input.length(); ii++) {
            char character = input.charAt(ii);
            int decoded = inverse.intValue() * (character - STARTNUM - b + MODULO);
            character = (char) (decoded % MODULO + STARTNUM);
            builder.append(character);
        }
        return builder.toString();
    }

    ////
    //
    //
    //
    ////
    private static String doEncryption(String input, int a, int b)
    {
        StringBuilder builder = new StringBuilder();
        for (int in = 0; in < input.length(); in++) {
            char character = input.charAt(in);
            character = (char) ((a * (character - STARTNUM) + b) % MODULO + STARTNUM);
            builder.append(character);
        }
        return builder.toString();
    }

    ////
    //
    //
    //
    ////
    public static void possibleKeys(int b){
        if (b < 0){
            for (int ii=1; ii<=MODULO; ii++){
                try{
                    BigInteger inverse = BigInteger.valueOf(ii).modInverse(BigInteger.valueOf(MODULO));
                    validKeys[ii] = inverse.intValue();
                }
                catch(Exception e){
                    // System.out.println("error on: " + ii );
                }
            }
            Arrays.sort(validKeys);
            for (int ii = 0; ii<validKeys.length; ii++){
                if ( validKeys[ii] > 0 ){
                    System.out.print(validKeys[ii]);
                    if (ii != validKeys.length - 1){
                        System.out.print(", ");
                    }
                }
            }
        } else { //print possible values for b with regards to a
            for (int ii = 0; ii<validKeys.length; ii++){
                if ( GCD(validKeys[ii], b) == 1){
                    System.out.print(validKeys[ii]);
                    if (ii != validKeys.length - 1){
                        System.out.print(", ");
                    }
                }
            }
        }
    }

    ////
    //
    //
    //
    ////
    public static boolean checkKey(int a, int b){
        boolean isValidKey = false;
        if (a > 0 && b < 0){
            int contains = Arrays.binarySearch(validKeys, a);
            if (contains > 0 ){
                isValidKey = true;
            }
        }
        if (b > 0 && a < 0){
            int contains = Arrays.binarySearch(validKeys, b);
            if (contains > 0 ){
                isValidKey = true;
            }
        }
        if (a > 0 && b > 0){
            if ( GCD(a,b) == 1 ){
                isValidKey = true;
            }
        }
        return isValidKey;
    }

    ////
    //
    //
    //
    ////
    private static int GCD(int a, int b){
        BigInteger b1 = BigInteger.valueOf(a);
        BigInteger b2 = BigInteger.valueOf(b);
        BigInteger gcd = b1.gcd(b2);
        return gcd.intValue();
    }
}
