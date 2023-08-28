package org.example;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.text.*;
import java.util.Date;
import java.util.Scanner;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


import static java.lang.System.in;

public class TextEditor extends Component {
    private String copiedText;
    public TextEditor() {
        JFrame frame = new JFrame("Text Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400); // Set the size of the frame

        // Create a panel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a JTextArea where the user can write things
        JTextArea textArea = new JTextArea(10, 40);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menu items and menus
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem openMenuItem = new JMenuItem("Open");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        JMenuItem printMenuItem = new JMenuItem("Print");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(printMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem copyMenuItem = new JMenuItem("Copy");
        JMenuItem pasteMenuItem = new JMenuItem("Paste");
        JMenuItem cutMenuItem = new JMenuItem("Cut");
        JMenuItem selectMenuItem = new JMenuItem("Select All");
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(selectMenuItem);

        JMenu viewMenu = new JMenu("View");
        JMenuItem searchMenuItem = new JMenuItem("Search");
        JMenuItem timeMenuItem = new JMenuItem("Time & Date");
        JMenuItem aboutMenuItem = new JMenuItem("About Developers");
        viewMenu.add(searchMenuItem);
        viewMenu.add(timeMenuItem);
        viewMenu.add(aboutMenuItem);

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);

        // Set the menu bar to the frame
        frame.setJMenuBar(menuBar);

        //Set onClick listener for File menu items
        newMenuItem.addActionListener(evt -> {
            TextEditor newText = new TextEditor();
        });

        openMenuItem.addActionListener(evt -> {
            //Create file chooser with and only allow users to open .txt files
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files and OpenDocument Text Files", "txt", "odt"));
            int result = fileChooser.showOpenDialog(this);
            //If you select a valid .txt file then try to open it
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedReader buff = null;
                try {
                    buff = new BufferedReader(new FileReader(selectedFile));
                    String str;
                    textArea.setText("");
                    while ((str = buff.readLine()) != null) {
                        textArea.append(str + "\n");
                    }
                } catch (IOException e) {
                } finally {
                    try { in.close(); } catch (Exception ex) { }
                }
            }

        });


        saveMenuItem.addActionListener(evt -> {
            JFileChooser fileChooser = new JFileChooser();
            int retVal = fileChooser.showSaveDialog(frame);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                // Create a bufferedwriter with the specified file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                    // Write the content to the file
                    Scanner scanner = new Scanner(textArea.getText());
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        writer.write(line + "\n");
                    }
                    scanner.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                          }
            }
        });

        printMenuItem.addActionListener(evt -> {
                    try {
                        textArea.print();
                    } catch (PrinterException exc){
                    }
        });

        //function to search for a specific word
        searchMenuItem.addActionListener(evt -> {
            String[] options = {"Search", "Remove Highlights"};
            String choice = (String) JOptionPane.showInputDialog(null, "Choose an option:", "Search and Highlight", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            Highlighter highlighter = textArea.getHighlighter();

            if (choice != null) {
                if (choice.equals("Search")) {
                    String message = "Enter word you want to search:";
                    String searchWord = JOptionPane.showInputDialog(null, message);

                    String content = textArea.getText();
                    highlighter.removeAllHighlights();

                    if (searchWord != null && !searchWord.isEmpty()) {
                        int i = 0;
                        while ((i = content.toLowerCase().indexOf(searchWord.toLowerCase(), i)) >= 0) {
                            int totalLength = i + searchWord.length();
                            try {
                                highlighter.addHighlight(i, totalLength, DefaultHighlighter.DefaultPainter);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            i = totalLength;
                        }
                    }
                } else if (choice.equals("Remove Highlights")) {
                    highlighter.removeAllHighlights();

                }
            }
        });

        //functions for copy, cut and paste
        copyMenuItem.addActionListener(evt -> {
            copiedText = textArea.getSelectedText();
        });

        cutMenuItem.addActionListener(evt -> {
            copiedText = textArea.getSelectedText();
            textArea.replaceSelection("");
        });

        pasteMenuItem.addActionListener(evt -> {
            if (copiedText != null) {
                int caretPosition = textArea.getCaretPosition();
                textArea.insert(copiedText, caretPosition);
            }
        });

        selectMenuItem.addActionListener(evt -> {
            textArea.selectAll();
        });


        //function to display the current time and date at the top of the text area
        timeMenuItem.addActionListener(evt -> {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            String strDate = formatter.format(new Date());
            textArea.setText(strDate + "\n");
        });

        //function to display the developers names and a little message in a pop-up box
        aboutMenuItem.addActionListener(evt -> {
            String message = "Rachel Bell - 20019755\nDale Geronimo - 22004533\nThis is our Assignment 1";
            JOptionPane.showMessageDialog(null, message);
        });

        // Add the panel to the frame
        frame.getContentPane().add(panel);

        // Display the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TextEditor();
        });
    }
}
