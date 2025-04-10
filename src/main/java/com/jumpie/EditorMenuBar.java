package com.jumpie;

import javax.swing.*;
import java.awt.*;
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
        createToolButtons();
    }

    private void createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        styleMenuItem(fileMenu);
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
        styleMenuItem(editMenu);
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
        voiceButton = new JButton("Record");
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

        styleButton(voiceButton);
        menuBar.add(voiceButton);
        menuBar.add(Box.createHorizontalStrut(5));
        styleButton(zoomInButton);
        menuBar.add(zoomInButton);
        styleButton(zoomOutButton);
        menuBar.add(zoomOutButton);
        styleButton(zoomResetButton);
        menuBar.add(zoomResetButton);
    }

    private void addMenuItem(JMenu menu, String text) {
        JMenuItem item = new JMenuItem(text);
        styleMenuItem(item);
        item.addActionListener(actionListener);
        menu.add(item);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(80, 80, 85));
        button.setForeground(new Color(189, 189, 201));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 105), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(96, 208, 191));
                button.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(80, 80, 85));
                button.setForeground(Color.WHITE);
            }
        });
    }
    private void styleMenuItem(JMenuItem menuItem) {
        menuItem.setForeground(Color.WHITE);
        menuItem.setBackground(new Color(80, 80, 85)); // Серый фон
        menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        menuItem.setContentAreaFilled(false);
        menuItem.setOpaque(true);
        menuItem.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        menuItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(new Color(96, 208, 191));
                menuItem.setForeground(Color.BLACK);
            }
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(new Color(80, 80, 85));
                menuItem.setForeground(Color.WHITE);
            }
        });

        if (menuItem instanceof JMenu) {
            JMenu menu = (JMenu)menuItem;
            menu.setBackground(new Color(80, 80, 85));
            menu.setForeground(Color.WHITE);

            menu.getPopupMenu().setBackground(new Color(80, 80, 85));
            menu.getPopupMenu().setBorder(
                    BorderFactory.createLineBorder(new Color(60, 63, 65), 1)
            );
        }
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JButton getVoiceButton() {
        return voiceButton;
    }
}