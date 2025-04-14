package com.jumpie;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.*;

public class FileManager {
    private final TabManager tabManager;
    private final JFrame parentFrame;

    public FileManager(JFrame frame, TabManager tabManager) {
        this.parentFrame = frame;
        this.tabManager = tabManager;
    }

    public void openFile() {
        File file = chooseFile(JFileChooser.OPEN_DIALOG);
        if (file == null) return;

        tabManager.addNewTab();
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane == null) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            textPane.read(reader, null);
            textPane.putClientProperty("file", file);
            tabManager.updateTabTitle(file.getName());
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    public void saveFile(boolean saveAs) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane == null) return;

        File currentFile = (File) textPane.getClientProperty("file");
        if (!saveAs && currentFile != null) {
            writeFile(textPane, currentFile);
            return;
        }

        File file = chooseFile(JFileChooser.SAVE_DIALOG);
        if (file != null) {
            writeFile(textPane, file);
            textPane.putClientProperty("file", file);
            tabManager.updateTabTitle(file.getName());
        }
    }

    private File chooseFile(int dialogType) {
        JFileChooser fileChooser = new JFileChooser("f:");
        int result = (dialogType == JFileChooser.SAVE_DIALOG)
                ? fileChooser.showSaveDialog(parentFrame)
                : fileChooser.showOpenDialog(parentFrame);
        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }

    private void writeFile(JTextComponent textComponent, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            textComponent.write(writer);
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "File Error", JOptionPane.ERROR_MESSAGE);
    }
}
