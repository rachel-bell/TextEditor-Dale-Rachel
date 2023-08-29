package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.in;
public class TextEditor extends Component {
    private String copiedText;
    public TextEditor() {
        JFrame frame = new JFrame("Text Editor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JMenuItem savePDFMenuItem = new JMenuItem("Save as PDF");
        JMenuItem printMenuItem = new JMenuItem("Print");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(savePDFMenuItem);
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
            JFileChooser fileChooser = new JFileChooser();

            // Set the default directory to the user's home directory
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "odt", "java", "cpp", "py"));
            // Show the open dialog and wait for user input
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getName().toLowerCase();

                try (BufferedReader buff = new BufferedReader(new FileReader(selectedFile))) {
                    // Create a text area for syntax highlighting
                    RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea();
                    String syntaxStyle = null;

                    if (fileName.endsWith(".txt")) {
                        textArea.read(new FileReader(selectedFile), null);
                    } else if (fileName.endsWith(".odt")) {
                        TextDocument odtDoc = TextDocument.loadDocument(selectedFile);
                        StringBuilder content = new StringBuilder();
                        for (Iterator<Paragraph> it = odtDoc.getParagraphIterator(); it.hasNext(); ) {
                            Paragraph paragraph = it.next();
                            content.append(paragraph.getTextContent()).append("\n");
                        }
                        textArea.setText(content.toString());
                    } else if (fileName.endsWith(".java")) {
                        syntaxStyle = SyntaxConstants.SYNTAX_STYLE_JAVA;
                    } else if (fileName.endsWith(".cpp")) {
                        syntaxStyle = SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
                    } else if (fileName.endsWith(".py")) {
                        syntaxStyle = SyntaxConstants.SYNTAX_STYLE_PYTHON;
                    } else {
                        // Unsupported file type
                        return;
                    }

                    if (syntaxStyle != null) {
                        // Apply syntax highlighting style and load code into the syntax text area
                        syntaxTextArea.setSyntaxEditingStyle(syntaxStyle);
                        syntaxTextArea.read(buff, null);

                        JScrollPane syntaxScrollPane = new JScrollPane(syntaxTextArea);
                        // Remove the previous scroll pane from the frame and add the syntax scroll pane
                        frame.remove(scrollPane);
                        frame.add(syntaxScrollPane, BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        saveMenuItem.addActionListener(evt -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".txt")) {
                    // Ensure the file has the ".txt" extension
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                     // Write the content of the text area to the selected file
                    textArea.write(writer);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "An error occurred while saving the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        savePDFMenuItem.addActionListener(evt -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".pdf")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                }
                try (PDDocument document = new PDDocument()) {
                    PDPage page = new PDPage();
                    document.addPage(page);

                    // Set up the content stream for adding text
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, 750);

                    String[] lines = textArea.getText().split("\n");
                    for (String line : lines) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -15);
                    }
                    // closing content stream
                    contentStream.endText();
                    contentStream.close();

                    document.save(file);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "An error occurred while saving the file.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        exitMenuItem.addActionListener(evt -> {
            //checks if the text area is empty
            if (!textArea.getText().isEmpty()) {
                int asking = JOptionPane.showConfirmDialog(this,
                        "Do you want to save your files before exiting this tab?");
                if (asking == JOptionPane.YES_OPTION) {
                    System.out.println("Saving...");
                } else {
                    int asking2 = JOptionPane.showConfirmDialog(this, "Are you sure?");
                    if (asking2 == JOptionPane.YES_OPTION) {
                        // if yes then all tabs get closed
                        System.exit(0);
                    }
                }
            } else {
                frame.dispose();
                int asking = JOptionPane.showConfirmDialog(this,
                        "Do you want to save your other files before exiting?");
                if (asking == JOptionPane.YES_OPTION) {
                    System.out.println("Saving...");
                } else {
                    int asking2 = JOptionPane.showConfirmDialog(this, "Are you sure?");
                    if (asking2 == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
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
            // Get the highlighter for the text area
            Highlighter highlighter = textArea.getHighlighter();

            if (choice != null) {
                if (choice.equals("Search")) {
                    String message = "Enter word you want to search:";
                    String searchWord = JOptionPane.showInputDialog(null, message);

                    String content = textArea.getText();
                    // Clear any existing highlights
                    highlighter.removeAllHighlights();

                    if (searchWord != null && !searchWord.isEmpty()) {
                        int i = 0;
                        // Search for the search word in the content
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
            String currentText = textArea.getText();
            textArea.setText(strDate + "\n" + currentText);
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
