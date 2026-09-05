import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class ProductDr {
    public static void segment(int leng) {
        for (int i = 0; i < leng * 23; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
    public static String[] reorder(String[] names) {
        try {
            String[] Nnames = new String[names.length];
            char z = 'a';
            int pos = 0;
            for (int i = 0; i < 10; i++) {
                z = (char)(i + 48);
                for (int x = 0; x < names.length; x++) {
                    if (names[x] != null) {
                        if (names[x].charAt(names[x].indexOf(".") - 1) == z) {
                            Nnames[pos] = names[x];
                            pos += 1;
                        }
                    }
                }
            }
            PrintWriter files = new PrintWriter("fileNames.txt");
            files.println(Nnames.length);
            for (int i = 0; i < Nnames.length; i++) {
                files.println(Nnames[i]);
            }
            files.close();
            return Nnames;
        } catch (FileNotFoundException e) {
            System.out.print("Could not locate file, exiting program");
            System.exit(1);
        }
        return names;
    }
    public static void nameWriter(String[] names, boolean removing, String Fname, int Files) {
        try {
            if (removing) {
                System.out.println("Removing file");
                for (int i = 0; i < names.length; i++) {
                    if (names[i].equals(Fname)) {
                        names[i] = null;
                        Files -= 1;
                    }
                }
            } else {
                System.out.println("Adding file");
                Files += 1;
                names = Arrays.copyOf(names, Files);
                names[names.length - 1] = Fname;
            }
            PrintWriter writer = new PrintWriter("fileNames.txt");
            writer.println(Files);
            names = reorder(names);
            for (int i = 0; i < names.length; i++) {
                if (names[i] != null) {
                    writer.println(names[i]);
                }
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate file, exiting program");
            System.exit(1);
        }
    }
    public static void SaE (String[] names, int[] leng, Product[][] inventory) {
        try {
            System.out.println("Saving and Exiting Program");
            names = reorder(names);
            PrintWriter[] writer = new PrintWriter[names.length];
            for (int i = 0; i < writer.length; i++) {
                writer[i] = new PrintWriter(names[i]);
                writer[i].println(leng[i]);
                for (int x = 0; x < inventory[i].length; x++) {
                    if (inventory[i][x].getName() != null) {
                        writer[i].println(inventory[i][x].getName() + "," + inventory[i][x].getQuantity());
                    }
                }
                writer[i].close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate file, exiting program");
            System.exit(1);
        }
    }
    
    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            while (true) {
                // for checking the current files in fileNames.txt
                File nameFile = new File("fileNames.txt");
                Scanner nameScan = new Scanner(nameFile);
                int Files = nameScan.nextInt();
                nameScan.nextLine();
                String[] names = new String[Files];
                for (int i = 0; i < Files; i++) {
                    names[i] = nameScan.nextLine();
                }
                // reorders the names to make them in numerical order
                names = reorder(names);

                // makes all of the files, scanners, lengths, products, and lines for the products
                File[] file = new File[names.length];
                Scanner[] scan = new Scanner[names.length];
                int[] leng = new int[names.length];
                Product[][] inventory = new Product[names.length][];
                String[] lines = new String[2];
                int s = 0;
                int max = 0;
                for (int i = 0; i < names.length; i++) {
                    file[i] = new File(names[i]);
                    scan[i] = new Scanner(file[i]);
                    leng[i] = scan[i].nextInt();
                    inventory[i] = new Product[leng[i]];
                    lines = new String[2];
                    scan[i].nextLine();
                    s = 0;
                    while (scan[i].hasNextLine()) {
                        if (s < leng[i]) {
                            lines = scan[i].nextLine().split(",");
                            inventory[i][s] = new Product();
                            inventory[i][s].setName(lines[0]);
                            inventory[i][s].setQuantity(Integer.parseInt(lines[1]));
                            s += 1;
                        } else {
                            throw new IllegalArgumentException("More or less items stated than actual amount of items, exiting program");
                        }
                    }
                    if (max < inventory[i].length) {
                        max = inventory[i].length;
                    }
                }
                if (inventory.length <= 6) {
                    segment(inventory.length);
                } else {
                    segment(6);
                }
                
                int AmtOIntervals = inventory.length / 6;
                int AmtLeft = inventory.length % 6;
                int invIndex1 = 0;
                int invIndex2 = 0;
                int invHolder = 0;
                String[] line = new String[inventory.length];
                while (AmtOIntervals > 0 || AmtLeft > 0) {
                    if (AmtOIntervals > 0 ) {
                        for (int i = 0; i < 6; i++) {
                            System.out.printf("%-23s", names[invIndex1] + " Elements: ");
                            invIndex1 += 1;
                        }
                        invIndex1 -= 1;
                        System.out.println();
                        for (int i = 0; i < max; i++) {
                            line = new String[6];
                            for (int x = 0; x < line.length; x++) {
                                if (i < inventory[x].length) {
                                    line[x] = "Item " + (i + 1) + ": " + inventory[x][i].getName() + " - " + inventory[x][i].getQuantity();
                                } else {
                                    line[x] = "";
                                }
                            }
                            for (int x = 0; x < line.length; x++) {
                                System.out.printf("%-23s", line[x]);
                            }
                            System.out.println();
                        }
                        AmtOIntervals -= 1;
                        segment(6);
                    } else if (AmtLeft > 0) {
                        invHolder = invIndex1;
                        for (int i = 0; i < AmtLeft; i++) {
                            System.out.printf("%-23s", names[invIndex1 + 1] + " Elements: ");
                            invIndex1 += 1;
                        }
                        System.out.println();
                        for (int i = 0; i < max; i++) {
                            invIndex2 = invHolder + 1;
                            
                            line = new String[AmtLeft];
                            for (int x = 0; x < line.length; x++) {
                                if (i < inventory[invIndex2].length) {
                                    line[x] = "Item " + (i + 1) + ": " + inventory[invIndex2][i].getName() + " - " + inventory[invIndex2][i].getQuantity();
                                } else {
                                    line[x] = "";
                                }
                                invIndex2 += 1;
                                
                            }
                            for (int x = 0; x < line.length; x++) {
                                System.out.printf("%-23s", line[x]);
                            }
                            System.out.println();
                        }
                        segment(6);
                        AmtLeft = 0;
                    }
                    if (names.length == 0) {
                        System.out.println("No Files Available For Preview");
                    } else {
                        System.out.println();
                    }
                    
                }



                int invN = 0;
                if (inventory.length > 0) {
                    System.out.print("Enter");
                    for (int i = 1; i <= inventory.length; i++) {
                        if (inventory.length == 1) {
                            System.out.print(" " + 1);
                        } else if (i != inventory.length){
                        System.out.print(" " + i + ",");
                        } else {
                            System.out.print(" or " + i);
                        }
                    }
                    System.out.printf(" for which inventory you want to use, %d to add or remove a file, or %d to save and exit: ", inventory.length + 1, inventory.length + 2);
                    invN = input.nextInt();
                } else {
                    System.out.print("Enter 1 to add a new file: ");
                    invN = input.nextInt();
                }
                input.nextLine();
                if (!(invN >= 1 && invN <= inventory.length + 2)) {
                    throw new IllegalArgumentException("Inventory not available, exiting program");
                }
                if (invN == inventory.length + 1) {
                    if (names.length > 0) {
                        System.out.print("Enter 1 to remove or 2 to add a file: ");
                        int check = input.nextInt();
                        boolean Bcheck = false;
                        if (check == 1) {
                            Bcheck = true;
                        } else if (check == 2) {
                            Bcheck = false;
                        }
                        input.nextLine();
                        if (check == 1 || check == 2) {
                            System.out.print("Please enter the name of the new file you want to remove or add: ");
                            String Fname = input.nextLine();
                            boolean check2 = false;
                            if (check == 2) {
                                for (int i = 0; i < names.length; i++) {
                                    if (names[i].equals(Fname)) {
                                        check2 = true;
                                    }
                                }
                            }
                            if (!check2) {
                                nameWriter(names, Bcheck, Fname, Files);
                            } else {
                                System.out.println("File already exists can not add a second one, continuing program");
                            }
                            continue;
                        } else {
                            throw new IllegalArgumentException("Option not available, exiting program");
                        }
                    } else {
                        System.out.print("Please enter the name of the new file you want to add: ");
                        String Fname = input.nextLine();
                        nameWriter(names, false, Fname, Files);
                        continue;
                    }
                }
                if (invN == inventory.length + 2) {
                    SaE(names, leng, inventory);
                    System.exit(0);
                }
                System.out.println("Inventory " + invN + "'s Elements are: ");
                invN -= 1;
                for (int i = 0; i < inventory[invN].length; i++) {
                    System.out.println("Item " + (i + 1) + ": " + inventory[invN][i].getName() + " - " + inventory[invN][i].getQuantity());
                }
                segment(6);
                System.out.print("Input");
                for (int i = 1; i <= inventory[invN].length; i++) {
                    if (inventory[invN].length == 1) {
                        System.out.print(" " + 1);
                    } else if (i != inventory[invN].length){
                    System.out.print(" " + i + ",");
                    } else {
                        System.out.print(" or " + i);
                    }
                }

                System.out.print(" for the item you want to edit, " + (inventory[invN].length + 1) + " to make a new item" + ", or " + (inventory[invN].length + 2) + " to save and exit: ");
                int itemN = input.nextInt();
                input.nextLine();
                if (!(itemN >= 1 && itemN <= inventory[invN].length + 1)) {
                    throw new IllegalArgumentException("Item not available, exiting program");
                }
                if (itemN == inventory[invN].length + 1) {
                    inventory[invN] = Arrays.copyOf(inventory[invN], inventory[invN].length + 1);
                    inventory[invN][inventory[invN].length - 1] = new Product();
                    System.out.print("Please enter the new products name: ");
                    inventory[invN][inventory[invN].length - 1].setName(input.nextLine());
                    System.out.print("Please enter the amount of " + inventory[invN][inventory[invN].length - 1].getName() + ": ");
                    inventory[invN][inventory[invN].length - 1].setQuantity(input.nextInt());
                    leng[invN] += 1;
                    SaE(names, leng, inventory);
                    continue;
                } else if (itemN == inventory[invN].length + 2) {
                    SaE(names, leng, inventory);
                    System.exit(0);
                }
                itemN -= 1;
                
                System.out.println("1 - GetName");
                System.out.println("2 - SetName");
                System.out.println("3 - GetQuantity");
                System.out.println("4 - SetQuantity");
                System.out.println("5 - Receive");
                System.out.println("6 - Sell");
                System.out.println("7 - Remove Item");
                System.out.println("8 - Save and Exit");

                segment(6);

                System.out.printf("Input 1, 2, 3, 4, 5, 6, 7, or 8 for how you want to edit %s: ", inventory[invN][itemN].getName());
                int c = input.nextInt();
                input.nextLine();
                segment(6);

                if (c == 1) {
                    System.out.println(inventory[invN][itemN].getName());
                } else if (c == 2) {
                    System.out.print("Enter the new name you want for " + inventory[invN][itemN].getName() + ": ");
                    inventory[invN][itemN].setName(input.nextLine());
                } else if (c == 3) {
                    System.out.println("The quantity for " + inventory[invN][itemN].getName() + " is: " + inventory[invN][itemN].getQuantity());
                } else if (c == 4) {
                    System.out.print("Set the new quantity that you want for " + inventory[invN][itemN].getName() + ": ");
                    inventory[invN][itemN].setQuantity(input.nextInt());
                    input.nextLine();
                } else if (c == 5) {
                    System.out.print("Set how much that you want to add to " + inventory[invN][itemN].getName() + ": ");
                    inventory[invN][itemN].receive(input.nextInt());
                    input.nextLine();
                } else if (c == 6) {
                    System.out.print("Set how much that you want to subtract to " + inventory[invN][itemN].getName() + ": ");
                    inventory[invN][itemN].sell(input.nextInt());
                    input.nextLine();
                } else if (c == 7) {
                    System.out.println("ARE YOU SURE YOU WANT TO REMOVE Item " + (itemN + 1) + ": " + inventory[invN][itemN].getName() + " - " + inventory[invN][itemN].getQuantity() + "?");
                    int code = (int)(Math.random() * 10000);
                    System.out.print("Enter " + code + " to confirm: ");
                    if (input.nextInt() == code) {
                        System.out.println("Removing Item");
                        leng[invN] -= 1;
                        inventory[invN][itemN].setName(null);
                    }
                } else if (c == 8) {
                    SaE(names, leng, inventory);
                    System.exit(0);
                } else {
                    throw new IllegalArgumentException("Option not available, exiting program");
                }
                segment(6);
                System.out.println();
                SaE(names, leng, inventory);
                continue;
            }
        } catch (NumberFormatException e) {
            System.out.println("Value inputted is not an integer, exiting program");
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Array or Item not valid, exiting program");
            System.exit(1);
        } catch (InputMismatchException e) {
            System.out.println("Value inputted is not an integer, exiting program");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate file, exiting program");
            System.exit(1);
        }
    }
}