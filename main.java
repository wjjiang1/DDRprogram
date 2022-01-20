import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class main {
    private static final int LEFT= 0;
    private static final int RIGHT= 1;
    /** Most recently used foot. Possible values: -1 (if none), LEFT, RIGHT */
    static int lastUsed= -1;
    /** Current held foot. Possible values: -1 (if none), LEFT, RIGHT */
    static int held= -1;
    /** Map whose key is position of the held note and value is foot that holds the note */
    static HashMap<Integer, Integer> heldPositions= new HashMap<>();
    static int[][] translation;

    public static void main(String[] args) {
        Scanner scanner= getFile();

        // Set up scanner to start at first measure of song
        while (scanner.hasNextLine()) {
            String s= scanner.nextLine();
            if (s.contains("measure 1"))
                break;
        }

        // Parse files for all notes
        ArrayList<String> steps= new ArrayList<>();
        while (scanner.hasNextLine()) {
            String s= scanner.nextLine();
            if (!s.contains(",") && !s.contains(";")) {
                steps.add(s);
            }
        }

        translation= new int[steps.size()][4];
        for (int[] arr : translation)
            Arrays.fill(arr, -1);

        int numRests= 0;
        for (int i= 0; i < steps.size(); i++ ) {
            String s= steps.get(i);
            int numNotes= countNotes(s);
            if (numNotes == 1) {
                for (int j= 0; j < 4; j++ ) {
                    translation[i][j]= heldPositions.getOrDefault(j, -1);
                    if (s.charAt(j) == '1') {
                        translation[i][j]= getFoot(j);
                    }
                    if (s.charAt(j) == '2') {
                        int heldFoot= getFoot(j);
                        translation[i][j]= heldFoot;
                        held= heldFoot;
                        heldPositions.put(j, heldFoot);
                    }
                    if (s.charAt(j) == '3') {
                        heldPositions.remove(j);
                    }
                }
            } else if (numNotes > 1) {
                mapJump(s, i);
            }
            if (numNotes == 0)
                numRests++ ;
            if (numNotes == 2 || numRests >= 2) {
                lastUsed= -1;
                numRests= 0;
            }

        }
        System.out.println("← ↓ ↑ →");
        printTranslation(translation);
    }

    public static void mapJump(String row, int line) {
        if (row.charAt(0) == '1') {
            /* Maps the following jumps:
             * ← ↓
             * ←   ↑
             * ←     →
             */
            translation[line][0]= LEFT;
            translation[line][row.indexOf('1', 1)]= RIGHT;
        } else if (row.charAt(3) == '1') {
            /* Maps the following jumps:
             *     ↑ →
             *   ↓   →
             */
            translation[line][3]= RIGHT;
            translation[line][row.indexOf('1', 0)]= LEFT;
        } else {
            // TODO map jump ↓ ↑ to R L or L R
            translation[line][1]= RIGHT;
            translation[line][2]= LEFT;
        }
    }

    /** Returns the corresponding foot that a note maps to. */
    public static int getFoot(int position) {
        if (heldPositions.size() == 0) {
            switch (lastUsed) {
            case LEFT:
                lastUsed= RIGHT;
                return RIGHT;
            case RIGHT:
                lastUsed= LEFT;
                return LEFT;
            case -1:
                if (position == 3)
                    lastUsed= RIGHT;
                else
                    lastUsed= LEFT;
                return lastUsed;
            }
        } else {
            lastUsed= held == LEFT ? RIGHT : LEFT;
            return lastUsed;
        }
        return -1;
    }

    /** Counts number of notes in a given beat. */
    public static int countNotes(String s) {
        int count= 0;
        for (int i= 0; i < 4; i++ ) {
            if (s.charAt(i) == '1')
                count++ ;
        }
        return count;
    }

    /** Prints final translation. */
    public static void printTranslation(int[][] translation) {
        for (int[] arr : translation) {
            for (int s : arr) {
                String step= " ";
                if (s == LEFT)
                    step= "L";
                if (s == RIGHT)
                    step= "R";
                System.out.print(step + " ");
            }
            System.out.println();
        }
    }

    /** Gets file from user input. */
    public static Scanner getFile() {
        System.out.println("Input file name: ");
        Scanner scanner= new Scanner(System.in);
        try {
            File file= new File(scanner.nextLine());
            scanner= new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Try again.");
            return getFile();
        }
        return scanner;
    }

}
