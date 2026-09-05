import java.util.Scanner;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class Window extends JFrame{
    private static int Files; // the ammount of files
    private static int[] lengths;
    private static String[] names;    //  Width Height
    private static final int[]  titleWH = {1000, 035};   // ↓
    private static final int[]   prodWH = {1000, 365};   // The heights must add up to 500 and the widths must all be 1000
    private static final int[] optionWH = {1000, 100};   // ↑
    private static final Font titleFont = new Font("Arial", Font.BOLD, 20);    // ↓
    private static final Font prodTFont = new Font("Arial", Font.BOLD, 15);    // ↓
    private static final Font prodFFont = new Font("Arial", Font.BOLD, 25);    // These are all for the different fonts throughout the Window
    private static final Font optionPFont = new Font("Arial", Font.BOLD, 17);  // ↑
    private static final Font optionFont = new Font("Arial", Font.BOLD, 14);   // ↑
    private static final Border panelLayout = BorderFactory.createLineBorder(Color.BLACK, 2);  // ↓
    private static final Border prodLayout = BorderFactory.createLineBorder(Color.BLACK, 1);   // These are for the borders in the Window
    private static Product[][] inventory;
    private static int currinv = -1;
    private static int currscreen = 0;
    private static int code;
    private static JPanel mainPanel;
    private static JFrame frame;
    private static CardLayout cardLayout;

    public void InitializeVariables () {
        try {
            File nameFile = new File("fileNames.txt"); // where the files are located that the scan will look for
            Scanner nameScan = new Scanner(nameFile);
            Files = nameScan.nextInt();
            nameScan.nextLine();
            names = new String[Files];              // ↓
            for (int i = 0; i < Files; i++) {       // ↓
                names[i] = nameScan.nextLine();     // This is where the scanner then scans the names of files
            }                                       // ↑
            names = reorder(names);     // This orders and then rewrites the file names in the file so that they are in order.

            File[] file = new File[names.length];
            Scanner[] scan = new Scanner[names.length];
            lengths = new int[names.length];
            inventory = new Product[names.length][];
            String[] lines = new String[2];
            int s = 0;
            int max = 0;
            for (int i = 0; i < names.length; i++) {
                file[i] = new File(names[i]);
                scan[i] = new Scanner(file[i]);
                lengths[i] = scan[i].nextInt();
                inventory[i] = new Product[lengths[i]];
                lines = new String[2];
                scan[i].nextLine();
                s = 0;
                while (scan[i].hasNextLine()) {
                    if (s < lengths[i]) {
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
    public Window () {
        InitializeVariables();
        frame = new JFrame("Window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.add(titles(currinv, currscreen), BorderLayout.NORTH);
        panel1.add(products(currinv, currscreen), BorderLayout.CENTER);
        panel1.add(options(currinv, currscreen), BorderLayout.SOUTH);
        mainPanel.add(panel1);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void editScreen(int index, int currscreenindex) {
        InitializeVariables();
        JPanel editPanel = new JPanel(new BorderLayout(0, 0));
        editPanel.add(titles(index, currscreenindex), BorderLayout.NORTH);
        editPanel.add(products(index, currscreenindex), BorderLayout.CENTER);
        editPanel.add(options(index, currscreenindex), BorderLayout.SOUTH);
        mainPanel.add(editPanel, "INV" + index + " " + currscreenindex);
        cardLayout.show(mainPanel, "INV" + index + " " + currscreenindex);
    }
    private JPanel titles(int ind, int screen) {
        JPanel Ftitles = new JPanel();
        if (ind == -1) {
            Ftitles.setLayout(new GridLayout(1,Files,0,0));
            Ftitles.setPreferredSize(new Dimension(titleWH[0], titleWH[1]));
            Ftitles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            for (int i = 0; i < Files; i++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setBorder(panelLayout);
                JLabel title = new JLabel(names[i]);
                title.setFont(titleFont);
                rowPanel.add(title);
                Ftitles.add(rowPanel);
            }
        } else if (screen == 1) {
            Ftitles.setLayout(new GridLayout(1,1,0,0));
            Ftitles.setPreferredSize(new Dimension(titleWH[0], titleWH[1]));
            Ftitles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            JPanel rowPanel = new JPanel();
            rowPanel.setBorder(panelLayout);
            JLabel title = new JLabel(names[ind], SwingConstants.CENTER);
            title.setFont(titleFont);
            rowPanel.add(title);
            Ftitles.add(rowPanel);
        } else if (screen == 2 || screen == 101) {
            Ftitles.setLayout(new GridLayout(1,1,0,0));
            Ftitles.setPreferredSize(new Dimension(titleWH[0], titleWH[1]));
            Ftitles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            JPanel rowPanel = new JPanel();
            rowPanel.setBorder(panelLayout);
            JLabel title = new JLabel(names[currinv], SwingConstants.CENTER);
            title.setFont(titleFont);
            rowPanel.add(title);
            Ftitles.add(rowPanel);
        } else if (screen == 4 || screen == 6 || screen == 7 || screen == 8 || screen == 9) {
            Ftitles.setLayout(new GridLayout(1,1,0,0));
            Ftitles.setPreferredSize(new Dimension(titleWH[0], titleWH[1]));
            Ftitles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            JPanel rowPanel = new JPanel();
            rowPanel.setBorder(panelLayout);
            JLabel title = new JLabel(names[currinv] + " -> Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
            title.setFont(titleFont);
            rowPanel.add(title);
            rowPanel.setFont(titleFont);
            Ftitles.add(rowPanel);
        } else if (screen == 100) {
            Ftitles.setLayout(new GridLayout(1,1,0,0));
            Ftitles.setPreferredSize(new Dimension(titleWH[0], titleWH[1]));
            Ftitles.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            JLabel title = new JLabel("Add or Remove a File", SwingConstants.CENTER);
            title.setBorder(panelLayout);
            title.setFont(titleFont);
            Ftitles.add(title);
        }
        return Ftitles;
    }

    private JPanel products(int ind, int screen) {
        JPanel Fproducts = new JPanel();
        JLabel prod;
        if (ind == -1 && screen == 0) {
            Fproducts.setLayout(new GridLayout( 1, Files, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            for (int i = 0; i < Files; i++) {
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new GridLayout(inventory[i].length, 1, 0, 0));
                rowPanel.setBorder(prodLayout);
                if (inventory[i].length == 0) {
                    prod = new JLabel("Nothing to Show", SwingConstants.CENTER);
                    prod.setFont(prodFFont);
                    rowPanel.add(prod);
                    Fproducts.add(rowPanel);
                } else {
                    for (int x = 0; x < inventory[i].length; x++) {
                        JPanel cell = new JPanel();
                        cell.setBorder(prodLayout);
                        cell.setPreferredSize(new Dimension(prodWH[0] / (inventory.length - 50), 66));
                        cell.setMaximumSize(new Dimension(prodWH[0] / (inventory.length - 50), 66));
                        cell.setLayout(new GridBagLayout());
                        prod = new JLabel("Item " + (x + 1) + ": " + inventory[i][x].getName() + " - " + inventory[i][x].getQuantity());
                        prod.setFont(prodTFont);
                        cell.add(prod);
                        rowPanel.add(cell);
                    }
                    if (inventory[i].length <= 5) {
                        Fproducts.add(rowPanel);
                    } else {
                        JScrollPane scrollPane = new JScrollPane(rowPanel);
                        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        Fproducts.add(scrollPane);
                    }
                }
            }
        } else if (screen == 1) {
            Fproducts.setLayout(new GridLayout( 1, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            currinv = ind;
            if (inventory[currinv].length == 0) {
                prod = new JLabel("Nothing to Show", SwingConstants.CENTER);
                prod.setFont(prodFFont);
                Fproducts.add(prod);
            } else {
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new GridLayout(inventory[currinv].length, 1, 0, 0));
                rowPanel.setBorder(prodLayout);
                for (int x = 0; x < inventory[currinv].length; x++) {
                    JPanel cell = new JPanel();
                    cell.setBorder(prodLayout);
                    cell.setPreferredSize(new Dimension(prodWH[0] / inventory.length - 50, 66));
                    cell.setMaximumSize(new Dimension(prodWH[0] / inventory.length - 50, 66));
                    cell.setLayout(new GridBagLayout());
                    prod = new JLabel("Item " + (x + 1) + ": " + inventory[currinv][x].getName() + " - " + inventory[currinv][x].getQuantity());
                    prod.setFont(prodTFont);
                    cell.add(prod);
                    rowPanel.add(cell);
                }
                if (inventory[ind].length == 1) {
                    Fproducts.add(rowPanel);
                } else {
                    JScrollPane scrollPane = new JScrollPane(rowPanel);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    Fproducts.add(scrollPane);
                }
            }
        } else if (screen == 2) {
            Fproducts.setLayout(new GridLayout( 1, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            if (inventory[currinv].length == 0) {
                prod = new JLabel("Nothing to Show", SwingConstants.CENTER);
                prod.setFont(prodFFont);
                Fproducts.add(prod);
            } else {
                JPanel rowPanel = new JPanel();
                rowPanel.setLayout(new GridLayout(1, 1, 100, 0));
                rowPanel.setBorder(prodLayout);
                prod = new JLabel("Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
                prod.setFont(prodTFont);
                rowPanel.add(prod);
                Fproducts.add(rowPanel);
            }
        } else if (screen == 4 || screen == 6 || screen == 7 || screen == 8) {
            Fproducts.setLayout(new GridLayout( 1, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(3,1,0,0));
            rowPanel.setMaximumSize(new Dimension(750, 365));
            if (screen == 4) {
                prod = new JLabel("Enter the new name for Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
            } else if (screen == 6) {
                prod = new JLabel("Enter the new quantity for Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
            } else if (screen == 7) {
                prod = new JLabel("Enter how much that you want Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity() + ", to recieve", SwingConstants.CENTER);
            } else {
                prod = new JLabel("Enter how much you want to sell from Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
            }
            prod.setFont(prodFFont);
            JTextField userInputField = new JTextField(20);
            userInputField.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField.setFont(prodFFont);
            JButton checkButton = new JButton("CONFIRM");
            checkButton.setFont(prodFFont);
            checkButton.addActionListener(e -> {
                if (screen == 4) {
                    inventory[currinv][ind].setName(userInputField.getText());
                } else if (screen == 6) {
                    inventory[currinv][ind].setQuantity(Integer.parseInt(userInputField.getText()));
                } else if (screen == 7) {
                    inventory[currinv][ind].receive(Integer.parseInt(userInputField.getText()));
                } else if (screen == 8) {
                    inventory[currinv][ind].sell(Integer.parseInt(userInputField.getText()));
                }
                System.out.println("Saving Program");
                SaE(names, lengths, inventory);
                editScreen(-1, 0);
            });
            rowPanel.add(prod);
            rowPanel.add(userInputField);
            rowPanel.add(checkButton);
            Fproducts.add(rowPanel);
        } else if (screen == 9) {                                                       // for removing a product from a file
            Fproducts.setLayout(new GridLayout( 1, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(4,1,0,0));
            rowPanel.setMaximumSize(new Dimension(750, 365));
            prod = new JLabel("ARE YOU SURE YOU WANT TO REMOVE Item " + (ind + 1) + ": " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity(), SwingConstants.CENTER);
            prod.setFont(prodFFont);
            rowPanel.add(prod);
            code = (int)(Math.random() * 10000);
            prod = new JLabel("Enter " + code + " to confirm removal", SwingConstants.CENTER);
            prod.setFont(prodFFont);
            rowPanel.add(prod);
            JTextField userInputField = new JTextField(20);
            userInputField.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField.setFont(prodFFont);
            JButton checkButton = new JButton("CONFIRM");
            checkButton.setFont(prodFFont);
            checkButton.addActionListener(e -> {
                if (Integer.parseInt(userInputField.getText()) == code) {
                    for (int i = 0; i < lengths.length; i++) {
                        if (names[i].equals(names[currinv])) {
                            lengths[i] -= 1;
                            inventory[i][ind].setName(null);
                        }
                    }
                    System.out.println("Saving Program");
                    SaE(names, lengths, inventory);
                    editScreen(-1, 0);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid code inputted, please retry.");
                }
            });
            rowPanel.add(userInputField);
            rowPanel.add(checkButton);
            Fproducts.add(rowPanel);
        } else if (screen == 100) {                                                     // for removing or adding a file
            Fproducts.setLayout(new GridLayout(5, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            JLabel label = new JLabel("Input 1 to remove a file or input 2 to add a file", SwingConstants.CENTER);
            label.setFont(prodFFont);
            Fproducts.add(label);
            JTextField userInputField1 = new JTextField(20);
            userInputField1.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField1.setFont(prodFFont);
            Fproducts.add(userInputField1);
            label = new JLabel("Input the name of the file that you want to add or get rid of with .txt at the end", SwingConstants.CENTER);
            label.setFont(prodFFont);
            Fproducts.add(label);
            JTextField userInputField2 = new JTextField(20);
            userInputField2.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField2.setFont(prodFFont);
            Fproducts.add(userInputField2);
            JButton confirm = new JButton("CONFIRM");
            confirm.setFont(prodFFont);
            confirm.addActionListener(e -> {
                if (userInputField2.getText().length() > 0) {
                    if (Integer.parseInt(userInputField1.getText()) == 1) {
                        System.out.println("Writing to get rid of a file");
                        nameWriter(names, true, userInputField2.getText(), Files);
                        editScreen(-1, 0);
                    } else if (Integer.parseInt(userInputField1.getText()) == 2) {
                        System.out.println("Writing to add a file");
                        nameWriter(names, false, userInputField2.getText(), Files);
                        editScreen(-1, 0);
                    }
                }
            });
            Fproducts.add(confirm);
        } else if (screen == 101) {                                                     // for adding a product to a file
            Fproducts.setLayout(new GridLayout( 1, 1, 0, 0));
            Fproducts.setPreferredSize(new Dimension(prodWH[0], prodWH[1]));
            Fproducts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(5,1,0,0));
            rowPanel.setMaximumSize(new Dimension(750, 100));
            prod = new JLabel("Enter the name of the new product", SwingConstants.CENTER);
            prod.setFont(prodFFont);
            rowPanel.add(prod);
            JTextField userInputField1 = new JTextField(20);
            userInputField1.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField1.setFont(prodFFont);
            rowPanel.add(userInputField1);
            prod = new JLabel("Enter the amount of the new product", SwingConstants.CENTER);
            prod.setFont(prodFFont);
            rowPanel.add(prod);
            JTextField userInputField2 = new JTextField(20);
            userInputField2.setHorizontalAlignment(SwingConstants.CENTER);
            userInputField2.setFont(prodFFont);
            rowPanel.add(userInputField2);
            JButton checkButton = new JButton("CONFIRM");
            checkButton.setFont(prodFFont);
            checkButton.addActionListener( e -> {
                if (userInputField1.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "no name inputted, please retry.");
                } else if (userInputField2.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No value inputted, please retry.");
                } else if (userInputField1.getText().length() >= 1 && Integer.parseInt(userInputField2.getText()) >= 0) {
                    for (int i = 0; i < lengths.length; i++) {
                        if (names[i].equals(names[currinv])) {
                            inventory[i] = Arrays.copyOf(inventory[i], inventory[i].length + 1);
                            inventory[i][inventory[i].length - 1] = new Product();
                            inventory[i][inventory[i].length - 1].setName(userInputField1.getText());
                            inventory[i][inventory[i].length - 1].setQuantity(Integer.parseInt(userInputField2.getText()));
                            lengths[i] += 1;
                        } 
                    }
                    System.out.println("Saving Program");
                    SaE(names, lengths, inventory);
                    editScreen(-1, 0);
                } else if (Integer.parseInt(userInputField2.getText()) < 0) {
                    JOptionPane.showMessageDialog(null, "Value " + userInputField2.getText() + " is invalid, please retry.");
                } else {
                    JOptionPane.showMessageDialog(null, "Something went wrong, please retry.");
                }
            });
            rowPanel.add(checkButton);
            Fproducts.add(rowPanel);
        }
        return Fproducts;
    }

    private JPanel options(int ind, int screen) {
        JPanel Foptions = new JPanel();
        Foptions.setBorder(panelLayout);
        if (screen == -1 || screen == 0 || screen == 1 || screen == 2) {
            Foptions.setLayout(new GridLayout( 2, 1, 0, 0));
        } else {
            Foptions.setLayout(new GridLayout( 1, 1, 0, 0));
        }
        Foptions.setPreferredSize(new Dimension(optionWH[0], optionWH[1]));
        Foptions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        if (ind == -1 && screen == 0) {
            JLabel optPrompt = new JLabel("Press the button for the inventory that you want to edit:", SwingConstants.CENTER);
            optPrompt.setFont(optionPFont);
            Foptions.add(optPrompt);
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(1, Files + 2, 0, 0));
            JButton[] buttons = new JButton[Files + 2];
            for (int i = 0; i < Files + 2; i++) {
                if (i < Files) {
                    int index = i;
                    buttons[i] = new JButton((i + 1) + "");
                    buttons[i].setFont(optionFont);
                    buttons[i].addActionListener(e -> {
                        editScreen(index, 1);
                    });
                } else {
                    if (i == Files) {
                        buttons[i] = new JButton("Add or Remove a File");
                        buttons[i].addActionListener(e -> {
                            editScreen(Files, 100);
                        });
                    } else {
                        buttons[i] = new JButton("Save and Exit");
                        buttons[i].addActionListener(e -> {
                            System.out.println("Saving and Exiting Program");
                            SaE(names, lengths, inventory);
                            System.exit(0);
                        });
                    }
                }
                rowPanel.add(buttons[i]);
            }
            Foptions.add(rowPanel, BorderLayout.SOUTH);
        } else if (screen == 1) {
            JLabel optPrompt = new JLabel("Press the button for the item that you want to edit:", SwingConstants.CENTER);
            optPrompt.setFont(optionPFont);
            Foptions.add(optPrompt);
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(1, inventory[currinv].length + 2, 0, 0));
            JButton[] buttons = new JButton[inventory[currinv].length + 2];
            for (int i = 0; i < inventory[ind].length + 2; i++) {
                if (i < inventory[ind].length) {
                    int index = i;
                    buttons[i] = new JButton((i + 1) + "");
                    buttons[i].setFont(optionFont);

                    buttons[i].addActionListener(e -> {
                        editScreen(index, 2);
                    });
                } else {
                    if (i == inventory[currinv].length) {
                        buttons[i] = new JButton("Add a new item");
                        buttons[i].addActionListener(e -> {
                            editScreen(ind, 101);
                        });
                    } else {
                        buttons[i] = new JButton("Save and Exit");
                        buttons[i].addActionListener(e -> {
                            System.out.println("Saving and Exiting Program");
                            SaE(names, lengths, inventory);
                            System.exit(0);
                        });
                    }
                }
                rowPanel.add(buttons[i]);
            }
            Foptions.add(rowPanel, BorderLayout.SOUTH);
        } else if (screen == 2) {
            JLabel optPrompt = new JLabel("Press the button for how you want to edit " + inventory[currinv][ind].getName() + " - " + inventory[currinv][ind].getQuantity() + ":", SwingConstants.CENTER);
            optPrompt.setFont(optionPFont);
            Foptions.add(optPrompt);
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(1, 8, 0, 0));
            JButton[] buttons = new JButton[8];
            buttons[0] = new JButton("1 - Get Name");
            buttons[0].setFont(optionFont);
            buttons[0].addActionListener(e -> {
                editScreen(ind, 3);
            });
            buttons[1] = new JButton("2 - Set Name");
            buttons[1].setFont(optionFont);
            buttons[1].addActionListener(e -> {
                editScreen(ind, 4);
            });
            buttons[2] = new JButton("3 - Get Quantity");
            buttons[2].setFont(optionFont);
            buttons[2].addActionListener(e -> {
                editScreen(ind, 5);
            });
            buttons[3] = new JButton("4 - Set Quantity");
            buttons[3].setFont(optionFont);
            buttons[3].addActionListener(e -> {
                editScreen(ind, 6);
            });
            buttons[4] = new JButton("5 - Receive");
            buttons[4].setFont(optionFont);
            buttons[4].addActionListener(e -> {
                editScreen(ind, 7);
            });
            buttons[5] = new JButton("6 - Sell");
            buttons[5].setFont(optionFont);
            buttons[5].addActionListener(e -> {
                editScreen(ind, 8);
            });
            buttons[6] = new JButton("7 - Remove Item");
            buttons[6].setFont(optionFont);
            buttons[6].addActionListener(e -> {
                editScreen(ind, 9);
            });
            buttons[7] = new JButton("8 - Save and Exit");
            buttons[7].setFont(optionFont);
            buttons[7].addActionListener(e -> {
                System.out.println("Saving and Exiting Program");
                SaE(names, lengths, inventory);
                System.exit(0);
            });
            for (int i = 0; i < buttons.length; i++) {
                rowPanel.add(buttons[i]);
            }
            Foptions.add(rowPanel, BorderLayout.SOUTH);
        } else if (screen == 3 || screen == 4 || screen == 5 || screen == 6 || screen == 7 || screen == 8 || screen == 9 || screen == 100 || screen == 101) {
            JPanel rowPanel = new JPanel();
            rowPanel.setLayout(new GridLayout(1, 1, 0, 0));
            JButton close = new JButton("Return to main page");
            close.setFont(prodFFont);
            close.addActionListener(e -> {
                editScreen(-1, 0);
            });
            rowPanel.add(close);
            Foptions.add(rowPanel);
        }
        return Foptions;
    }

    public static String[] reorder(String[] names) {
        try {
            String[] Nnames = new String[names.length];
            char z;
            int pos = 0;
            for (int i = 0; i < 10; i++) {      // ↓
                z = (char)(i + 48);             // Makes the range set from 0 -> 9 in ASCII
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

    public static void main(String[] argv) {
        javax.swing.SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                Window panel = new Window();
            }
        });
  }
}