package com.jumpie;

import javax.swing.*;
import java.awt.event.*;

public class EditorMenuBar {
    private JMenuBar menuBar;
    private ActionListener actionListener;
    private JButton voiceButton;

    public EditorMenuBar(ActionListener listener) {
        this.actionListener = listener;
        this.menuBar = new JMenuBar();
        createFileMenu();
        createEditMenu();
        createVoiceButton();
    }

    private void createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "New Tab");
        addMenuItem(fileMenu, "Open");
        addMenuItem(fileMenu, "Save");
        addMenuItem(fileMenu, "Save As");
        addMenuItem(fileMenu, "Print");
        addMenuItem(fileMenu, "Close Tab");
        menuBar.add(fileMenu);
    }

    private void createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        addMenuItem(editMenu, "Cut");
        addMenuItem(editMenu, "Copy");
        addMenuItem(editMenu, "Paste");
        addMenuItem(editMenu, "Voice Input");
        menuBar.add(editMenu);
    }

    private void createVoiceButton() {
        voiceButton = new JButton("🎤");
        voiceButton.setToolTipText("Start/Stop voice input");
        menuBar.add(voiceButton);
    }

    private void addMenuItem(JMenu menu, String text) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JButton getVoiceButton() {
        return voiceButton;
    }
}