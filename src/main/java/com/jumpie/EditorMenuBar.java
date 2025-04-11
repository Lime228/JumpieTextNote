package com.jumpie;

import javax.swing.*;
import java.awt.event.*;

public class EditorMenuBar {
    private JMenuBar menuBar;
    private ActionListener actionListener;
    private JButton voiceButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton zoomResetButton;

    public EditorMenuBar(ActionListener listener) {
        this.actionListener = listener;
        this.menuBar = new JMenuBar();
        createFileMenu();
        createEditMenu();
        createToolButtons();
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
        addMenuItem(editMenu, "Zoom In");
        addMenuItem(editMenu, "Zoom Out");
        addMenuItem(editMenu, "Reset Zoom");
        menuBar.add(editMenu);
    }

    private void createToolButtons() {
        voiceButton = new JButton("🎤");
        voiceButton.setToolTipText("Start/Stop voice input");

        JButton zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.addActionListener(actionListener);

        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.addActionListener(actionListener);

        JButton zoomResetButton = new JButton("100%");
        zoomResetButton.setToolTipText("Reset Zoom");
        zoomResetButton.addActionListener(actionListener);

        menuBar.add(voiceButton);
        menuBar.add(Box.createHorizontalStrut(5));
        menuBar.add(zoomInButton);
        menuBar.add(zoomOutButton);
        menuBar.add(zoomResetButton);
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

    public JButton getZoomInButton() {
        return zoomInButton;
    }

    public JButton getZoomOutButton() {
        return zoomOutButton;
    }

    public JButton getZoomResetButton() {
        return zoomResetButton;
    }
}