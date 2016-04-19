/*
* Custom Class made by Jeremy Shade
* Created on the 17/09/2015
* Data Structures and Algorithms 120
*/
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleInput
{
    public static int readInt(String prompt)
    {
        int integer = 0;
        boolean legit = false;

        while ( !legit ){
            try
            {
                Scanner input = new Scanner(System.in);
                System.out.print(prompt);
                integer = input.nextInt();
                legit = true;
            }
            catch (InputMismatchException e)
            {
                System.out.println("-> Error: an integer was not entered");
            }
        }
        return integer;
    }

    public static double readDouble(String prompt)
    {
        double number = 0.0;
        boolean legit = false;

        while ( !legit )
        {
            try
            {
                Scanner input = new Scanner(System.in);
                System.out.print(prompt);
                number = input.nextDouble();
                legit = true;
            }
            catch (InputMismatchException e)
            {
                System.out.println("-> Error: a number was not entered");
            }
        }

        return number;
    }

    public static String readString(String prompt)
    {
        String inputString = "";
        boolean legit = false;

        while ( !legit )
        {
            try
            {
                Scanner input = new Scanner(System.in);
                System.out.print(prompt);
                inputString = input.nextLine();
                legit = true;
            }
            catch (InputMismatchException e)
            {
                System.out.println("-> Error: Please try again");
            }
        }

        return inputString;
    }
}
