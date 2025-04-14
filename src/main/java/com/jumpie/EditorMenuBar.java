package com.jumpie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditorMenuBar {
    private final JMenuBar menuBar;
    private final ActionListener actionListener;
    private JButton voiceButton;

    public EditorMenuBar(ActionListener listener) {
        this.actionListener = listener;
        this.menuBar = new JMenuBar();
        initializeMenus();
        initializeToolButtons();
    }

    private void initializeMenus() {
        menuBar.add(createMenu("File", "New Tab", "Open", "Save", "Save As", "Print", "Close Tab"));
        menuBar.add(createMenu("Edit", "Cut", "Copy", "Paste", "Voice Input", "Zoom In", "Zoom Out", "Reset Zoom"));
    }

    private JMenu createMenu(String title, String... items) {
        JMenu menu = new JMenu(title);
        styleMenuItem(menu);
        for (String itemText : items) {
            menu.add(createMenuItem(itemText));
        }
        return menu;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        styleMenuItem(item);
        item.addActionListener(actionListener);
        return item;
    }

    private void initializeToolButtons() {
        voiceButton = createStyledButton("Record", "Start/Stop voice input");

        JButton zoomInButton = createStyledButton("+", "Zoom In");
        JButton zoomOutButton = createStyledButton("-", "Zoom Out");
        JButton zoomResetButton = createStyledButton("100%", "Reset Zoom");

        menuBar.add(voiceButton);
        menuBar.add(Box.createHorizontalStrut(5));
        menuBar.add(zoomInButton);
        menuBar.add(zoomOutButton);
        menuBar.add(zoomResetButton);
    }

    private JButton createStyledButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(actionListener);
        styleButton(button);
        return button;
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
        menuItem.setBackground(new Color(80, 80, 85));
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

        if (menuItem instanceof JMenu menu) {
            menu.setBackground(new Color(80, 80, 85));
            menu.setForeground(Color.WHITE);
            menu.getPopupMenu().setBackground(new Color(80, 80, 85));
            menu.getPopupMenu().setBorder(BorderFactory.createLineBorder(new Color(60, 63, 65), 1));
        }
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    public JButton getVoiceButton() {
        return voiceButton;
    }
}
