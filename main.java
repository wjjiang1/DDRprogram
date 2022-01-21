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
    static int freezeFoot= -1;
    /** Map whose key is position of the freeze note and value is foot that holds the note */
    static HashMap<Integer, Integer> freezePositions= new HashMap<>();
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
            if (numNotes == 0)
                numRests++ ;
            if (numNotes == 1) {
                mapStep(s, i);
            } else if (numNotes == 2) {
                mapJump(s, i);
                lastUsed= -1;
                numRests= 0;
            }

            unfreeze(s);
            if (numRests >= 2) {
                lastUsed= -1;
                numRests= 0;
            }

            /* DEBUGGING
             * for (int n : translation[i]) {
                String step= " ";
                if (n == LEFT)
                    step= "L";
                if (n == RIGHT)
                    step= "R";
                System.out.print(step + " ");
            }
            System.out.println(freezePositions); */
        }
        System.out.println("← ↓ ↑ →");
        printTranslation(translation);
    }

    public static void unfreeze(String row) {
        for (int i= 0; i < 4; i++ ) {
            if (row.charAt(i) == '3')
                freezePositions.remove(i);
        }
    }

    /** Maps a single note to its corresponding foot. */
    public static void mapStep(String row, int line) {
        for (int i= 0; i < 4; i++ ) {
            translation[line][i]= freezePositions.getOrDefault(i, -1);
            if (row.charAt(i) == '1') {
                translation[line][i]= getFoot(i);
            }
            if (row.charAt(i) == '2') {
                int heldFoot= getFoot(i);
                translation[line][i]= heldFoot;
                freezeFoot= heldFoot;
                freezePositions.put(i, heldFoot);
            }
        }
    }

    /** Maps a two-note jump to its corresponding feet. */
    public static void mapJump(String row, int line) {
        // TODO if jumps are freeze arrows
        if (row.charAt(0) == '1' || row.charAt(0) == '2') {
            /* Maps the following jumps:
             * ← ↓
             * ←   ↑
             * ←     →
             */
            if (row.charAt(0) == '2') {
                freezeFoot= LEFT;
                freezePositions.put(0, LEFT);
            }
            translation[line][0]= LEFT;
            if (row.indexOf('1', 1) != -1)
                translation[line][row.indexOf('1', 1)]= RIGHT;
            else {
                translation[line][row.indexOf('2', 1)]= RIGHT;
                freezeFoot= RIGHT;
                freezePositions.put(row.indexOf('2', 1), RIGHT);
            }
        } else if (row.charAt(3) == '1' || row.charAt(3) == '2') {
            /* Maps the following jumps:
             *     ↑ →
             *   ↓   →
             */
            if (row.charAt(3) == '2') {
                freezeFoot= RIGHT;
                freezePositions.put(3, RIGHT);
            }
            translation[line][3]= RIGHT;
            String fstThree= row.substring(0, 3);
            if (fstThree.contains("1"))
                translation[line][fstThree.indexOf('1')]= LEFT;
            else {
                translation[line][fstThree.indexOf('2')]= LEFT;
                freezeFoot= LEFT;
                freezePositions.put(fstThree.indexOf('2'), LEFT);
            }
        } else {
            // TODO map jump ↓ ↑ to R L or L R
            if (row.charAt(1) == '2') {
                freezeFoot= RIGHT;
                freezePositions.put(1, RIGHT);
            }
            translation[line][1]= RIGHT;
            if (row.charAt(2) == '2') {
                freezeFoot= LEFT;
                freezePositions.put(2, LEFT);
            }
            translation[line][2]= LEFT;
        }
    }

    /** Returns the corresponding foot that a note maps to. */
    public static int getFoot(int position) {
        if (freezePositions.size() == 0) {
            switch (lastUsed) {
            case LEFT:
                lastUsed= RIGHT;
                break;
            case RIGHT:
                lastUsed= LEFT;
                break;
            case -1:
                if (position == 3)
                    lastUsed= RIGHT;
                else
                    lastUsed= LEFT;
            }
        } else {
            lastUsed= freezeFoot == LEFT ? RIGHT : LEFT;
        }
        return lastUsed;
    }

    /** Counts number of notes in a given beat. */
    public static int countNotes(String s) {
        int count= 0;
        for (int i= 0; i < 4; i++ ) {
            if (s.charAt(i) == '1' || s.charAt(i) == '2')
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
