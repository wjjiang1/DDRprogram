import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class main {
    public static Scanner getFile() {
        System.out.println("Input file name: ");
        Scanner scanner= new Scanner(System.in);
        try {
            File file= new File(scanner.nextLine());
            scanner= new Scanner(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("File not found. Try again.");
            getFile();
        }
        return scanner;
    }

    public static String getFoot(String lastUsed) {
        return "L";
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Scanner scanner= getFile();
        while (scanner.hasNextLine()) {
            String s= scanner.nextLine();
            if (s.contains("measure 1"))
                break;
        }

        ArrayList<String> steps= new ArrayList<>();
        while (scanner.hasNextLine()) {
            String s= scanner.nextLine();
            if (!s.contains(",") && !s.contains(";")) {
                steps.add(s);
            }
        }

        String[][] translation= new String[steps.size()][4];
        for (String[] arr : translation)
            Arrays.fill(arr, " ");

        for (int i= 0; i < steps.size(); i++ ) {
            String s= steps.get(i);
            for (int j= 0; j < 4; j++ ) {
                if (s.charAt(j) == '1') {
                    translation[i][j]= getFoot("");
                }
            }
        }
        printTranslation(translation);
    }

    public static void printTranslation(String[][] translation) {
        for (String[] arr : translation) {
            for (String s : arr) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

}
