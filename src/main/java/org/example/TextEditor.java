package org.example;

import javax.swing.*;
import java.awt.*;
import java.text.*;
import java.util.Date;

public class TextEditor {
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
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(cutMenuItem);

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

        //Set onClick listener for menu items
        newMenuItem.addActionListener(evt -> {
            TextEditor newText = new TextEditor();
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
