import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileListMaker {
    private static ArrayList<String> records = new ArrayList<>();
    private static boolean needsToBeSaved = false;
    private static boolean loadTag = false;
    private static String loadedFileName = "";
    private static JFileChooser chooser = new JFileChooser();
    private static File selectedFile;

    public static void main(String[] args) {

        boolean quitYN = false;
        String menuChoice = "";

        Scanner in = new Scanner(System.in);

        do {
            System.out.println();
            displayMenu();
            displayList();
            System.out.println();
            menuChoice = SafeInput.getRegExString(in, "Enter a command", "[AaDdIiMmVvCcOoSsQq]");


            try {
                switch (menuChoice) {

                    case "a", "A" -> addItem(in);

                    case "d", "D" -> removeItem(in);

                    case "i", "I" -> insertItem(in);

                    case "m", "M" -> moveItem(in);

                    case "v", "V" -> viewList();

                    case "c", "C" -> clearList(in);

                    case "o", "O" -> openFile(in);

                    case "s", "S" -> saveFile(in);

                    case "q", "Q" -> quitYN = quit(in);

                    default -> System.out.println("Unexpected menu choice");
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File not found.");
                e.printStackTrace();
            }
            catch (IOException e)
            {
                System.out.println("IO Exception");
                e.printStackTrace();
            }

        } while (!quitYN);

        in.close();
    }

    private static void addItem(Scanner pipe) {
        String item = SafeInput.getNonZeroLenString(pipe, "Add - enter entry");
        records.add(item);
        needsToBeSaved = true;
    }

    private static void removeItem(Scanner pipe) {
        if (records.isEmpty())
            System.out.println("Error: List empty");
        else {
            int index = SafeInput.getRangedInt(pipe, "Delete - enter index value", 0, (records.size() - 1));
            records.remove(index);
            needsToBeSaved = true;
        }
    }

    private static void insertItem(Scanner pipe) {
        if (records.isEmpty()) {
            int index = 0;
            String item = SafeInput.getNonZeroLenString(pipe, "Insert enter item");
            records.add(index, item);
            needsToBeSaved = true;
        } else {
            int index = SafeInput.getRangedInt(pipe, "Insert - enter index value", 0, (records.size()));
            String item = SafeInput.getNonZeroLenString(pipe, "Insert - enter item");
            records.add(index, item);
            needsToBeSaved = true;
        }
    }

    private static void moveItem(Scanner pipe) {
        if (records.isEmpty()) {
            System.out.println("List empty, no entry to move");
        } else {
            int itemIndex = SafeInput.getRangedInt(pipe, "Move - enter starting position",
                    0, (records.size() - 1));
            int destIndex = SafeInput.getRangedInt(pipe, "Move - enter destination position", 0,
                    (records.size() - 1));
            String item = records.get(itemIndex);
            records.remove(itemIndex);
            records.add(destIndex, item);
            needsToBeSaved = true;
        }
    }

    private static void clearList(Scanner pipe) throws IOException {
        boolean confirm = false;
        confirm = SafeInput.getYNConfirm(pipe, "Clear - Warning: will clear current data. Do you wish to proceed? [Y/N]");
        if (confirm && needsToBeSaved) {
            saveCheck(pipe, "Do you wish to save the current file before clearing data? [Y/N]");
        }
        if (confirm) {
            records.clear();
            System.out.println("Data cleared");
            needsToBeSaved = false;
            loadedFileName = "";
            loadTag = false;
            //Resetting dirty and load tags since there's no point saving a blank file; better to not overwrite previous file by default
        }

    }

    private static void displayMenu() {
        System.out.println("A - Add   D - Delete   I - Insert   M - Move   V - View   C - Clear  O - Open   S - Save   Q - Quit");
    }

    private static void displayList() {

        for (int index = 0; index < records.size(); index++)
            System.out.print(index + ") " + records.get(index) + "  ");
    }

    private static void viewList() {
        System.out.println();
        for (int index = 0; index < records.size(); index++) {
            System.out.println(index + ") " + records.get(index));
        }
    }

    private static void openFile(Scanner pipe) throws FileNotFoundException, IOException {

        if (needsToBeSaved) {
            saveCheck(pipe, "Current file has unsaved data. Do you want to save first? [Y/N]");
        }
        JFileChooser chooser = new JFileChooser();
        File selectedFile;
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        String lineEntry = "";

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            Path file = selectedFile.toPath();
            // wrap a BufferedReader around a lower level BufferedInputStream
            InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            int line = 0;
            records.clear();

            while (reader.ready()) {
                lineEntry = reader.readLine();
                records.add(lineEntry);
                line++;
            }
            loadTag = true;
            loadedFileName = selectedFile.getName();
            System.out.println("Successfully loaded " + loadedFileName);
            reader.close();
        }
    }

    private static void saveFile(Scanner pipe) throws IOException {
        String fileName = "";
        if (loadTag) {
            fileName = loadedFileName;
        }
        else {
            fileName = SafeInput.getNonZeroLenString(pipe, "Enter file name to save");
            fileName = fileName + ".txt";
        }

        File workingDirectory = new File(System.getProperty("user.dir"));
        Path file = Paths.get(workingDirectory.getPath() + "\\src\\" + fileName);

        OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, CREATE));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        int lines = 0;

        for(String entry : records)
        {
            writer.write(entry, 0, entry.length());
            lines++;
            if (lines < (records.size())) {
                writer.newLine();  // adds new line on all lines but last (to avoid a blank list item)
            }

        }
        writer.close();
        System.out.println("Data saved to " + fileName);
    }

    private static void saveCheck(Scanner pipe, String prompt) throws IOException {
        //is called when dirty tag is activated, and the file may need to be saved before data is lost
        if (needsToBeSaved) {
            boolean saveCurrentFile = SafeInput.getYNConfirm(pipe, prompt);
            if (saveCurrentFile) {
                saveFile(pipe);
            }
        }
    }

    private static boolean quit(Scanner pipe) throws IOException {
        boolean confirm = SafeInput.getYNConfirm(pipe, "Are you sure you want to quit? [Y/N]");
        if (needsToBeSaved) {
            saveCheck(pipe, "Current file has unsaved data. Do you wish to save before quiting? [Y/N]");
        }
        return confirm;
    }
}