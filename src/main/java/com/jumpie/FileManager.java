package com.jumpie;

import javax.swing.*;
import java.io.*;
import javax.swing.text.JTextComponent;

public class FileManager {
    private TabManager tabManager;
    private JFrame parentFrame;

    public FileManager(JFrame frame, TabManager tabManager) {
        this.parentFrame = frame;
        this.tabManager = tabManager;
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser("f:");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            tabManager.addNewTab();

            JTextPane textArea = tabManager.getCurrentTextComponent();
            if (textArea == null) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                textArea.putClientProperty("file", file);
                tabManager.updateTabTitle(file.getName());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    public void saveFile(boolean saveAs) {
        JTextPane textArea = tabManager.getCurrentTextComponent();
        if (textArea == null) return;

        File currentFile = (File) textArea.getClientProperty("file");
        if (!saveAs && currentFile != null) {
            saveToFile(textArea, currentFile);
            return;
        }

        JFileChooser fileChooser = new JFileChooser("f:");
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            saveToFile(textArea, file);
            textArea.putClientProperty("file", file);
            tabManager.updateTabTitle(file.getName());
        }
    }

    private void saveToFile(JTextComponent textComponent, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            textComponent.write(writer);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message);
    }
}