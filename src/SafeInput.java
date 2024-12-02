import java.util.Scanner;

public class SafeInput {

    /** @param pipe a Scanner opened to read from System.in
     * @param prompt prompt for the user
     * @return a String response that is not zero length
     */
    public static String getNonZeroLenString(Scanner pipe, String prompt) {

        String retString = ""; // Loop runs until string isn't blank
        do {
            System.out.print(prompt + ": ");
            retString = pipe.nextLine();
        } while(retString.length() == 0);

        return retString;
    }

    public static int getInt(Scanner pipe, String prompt) {

        boolean okInput = false;
        int retInt = 0;
        String buffer = ""; /*decided to just parse int from a string, to avoid blank input like just pressing or
        the spacebar leading to weird behavior. Spent entirely too much time learning how to do this. */
        do {
            System.out.print("\n" + prompt + ": ");
            buffer = pipe.nextLine();

            try {
                retInt = Integer.parseInt(buffer);
                okInput = true;
            } catch (NumberFormatException error) {
                System.out.print("Invalid int parsing " + error.getMessage());
                okInput = false;
            }

        } while(!okInput); //should only exit when an int is input

        return retInt;
    }

    public static double getDouble(Scanner pipe, String prompt) {
        boolean okInput = false;
        double retDouble = 0.0;
        String buffer = ""; //Again, just decided using wrappers to parse is less headache.

        do {
            System.out.print("\n" + prompt + ": ");
            buffer = pipe.nextLine();

            try {
                retDouble = Double.parseDouble(buffer);
                okInput = true;
            } catch (NumberFormatException error) {
                System.out.print("Invalid double parsing " + error.getMessage());
                okInput = false;
            }

        } while(!okInput); //should only exit when an int is input

        return retDouble;
    }

    public static int getRangedInt(Scanner pipe, String prompt, int low, int high) {
        boolean okInput = false;
        int inputInt = 0;
        int retRangedInt = 0;
        String buffer = "";

        do{
            System.out.print(prompt + " [" + low + "-" + high + "]: ");
            buffer = pipe.nextLine();
            try {
                inputInt = Integer.parseInt(buffer);
                if (inputInt >= low && inputInt <= high) {
                    retRangedInt = inputInt;
                    okInput = true;
                }
                else {
                    System.out.print("Outside valid range \n");
                }
            } catch (NumberFormatException error) {
                System.out.print("Invalid input\n");
            }
        } while(!okInput);

        return retRangedInt;
    }

    public static double getRangedDouble(Scanner pipe, String prompt, double low, double high) {
        boolean okInput = false;
        double inputDouble = 0.0;
        double retRangedDouble = 0.0;
        String buffer = "";

        do{
            System.out.print("\n" + prompt + " [" + low + " - " + high + "]: ");
            buffer = pipe.nextLine();
            try {
                inputDouble = Double.parseDouble(buffer);
                if (inputDouble >= low && inputDouble <= high) {
                    retRangedDouble = inputDouble;
                    okInput = true;
                }
                else {
                    System.out.print("Input double outside valid range.");
                }
            } catch (NumberFormatException error) {
                System.out.print("Invalid double parsing " + error.getMessage());
            }
        } while(!okInput);

        return retRangedDouble;
    }

    public static boolean getYNConfirm(Scanner pipe, String prompt) {
        boolean retYN = false;
        boolean done = false;
        String continueInput = "";
        do {
            System.out.print(prompt +": ");
            continueInput = pipe.nextLine();
            if(continueInput.equalsIgnoreCase("Y") || continueInput.equalsIgnoreCase("yes")) {
                retYN = true;
                done = true;
            }
            else if (continueInput.equalsIgnoreCase("N") || continueInput.equalsIgnoreCase("no")) {
                retYN = false;
                done = true;
            }
            else {
                System.out.print("Invalid input. Please pick either 'y' or 'n'");
            }
        } while(!done);

        return retYN;
    }

    public static String getRegExString (Scanner pipe, String prompt, String regEx) {
        //show the prompt
        //input the data
        //test to see if the value matches valid regex format
        boolean okRegInput = false;
        String value = "";
        do {
            System.out.print(prompt + ": ");
            value = pipe.nextLine();
            if(value.matches(regEx)) {
                okRegInput = true;
            }
            else {
                System.out.println("Invalid");
            }

        }while(!okRegInput);
        return value;
    }
}