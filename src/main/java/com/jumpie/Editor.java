package com.jumpie;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;
import javax.swing.text.*;

class Editor extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JFrame frame;

    public Editor() {
        initializeUI();
        setupMenuBar();
        setupFrame();
        addNewTab();
    }

    private void initializeUI() {
        frame = new JFrame("Editor with Tabs");

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabbedPane = new JTabbedPane();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "New Tab");
        addMenuItem(fileMenu, "Open");
        addMenuItem(fileMenu, "Save");
        addMenuItem(fileMenu, "Save As");
        addMenuItem(fileMenu, "Print");
        addMenuItem(fileMenu, "Close Tab");

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        addMenuItem(editMenu, "Cut");
        addMenuItem(editMenu, "Copy");
        addMenuItem(editMenu, "Paste");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        frame.setJMenuBar(menuBar);
    }

    private void addMenuItem(JMenu menu, String itemName) {
        JMenuItem menuItem = new JMenuItem(itemName);
        menuItem.addActionListener(this);
        menu.add(menuItem);
    }

    private void setupFrame() {
        frame.add(tabbedPane);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void addNewTab() {
        JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);

        // панель для вкладки с кнопкой закрытия
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);

        JLabel tabLabel = new JLabel("New Document " + (tabbedPane.getTabCount() + 1));
        JButton closeButton = new JButton("x");
        closeButton.setMargin(new Insets(0, 5, 0, 0));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);

        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfComponent(scrollPane);
            if (index != -1) {
                tabbedPane.remove(index);
            }
        });

        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        tabHeader.add(tabLabel);
        tabHeader.add(closeButton);

        tabPanel.add(tabHeader, BorderLayout.CENTER);

        tabbedPane.addTab(null, scrollPane);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private JTextArea getCurrentTextArea() {
        Component component = tabbedPane.getSelectedComponent();
        if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            return (JTextArea) scrollPane.getViewport().getView();
        }
        return null;
    }

    private void saveFile(boolean saveAs) {
        JTextArea textArea = getCurrentTextArea();
        if (textArea == null) return;

        JFileChooser fileChooser = new JFileChooser("f:");

        // если не "Save As" и у вкладки уже есть связанный файл
        if (!saveAs && textArea.getClientProperty("file") != null) {
            File file = (File) textArea.getClientProperty("file");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write(textArea.getText());
                updateTabTitle(file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
            return;
        }

        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write(textArea.getText());
                textArea.putClientProperty("file", file);
                updateTabTitle(file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "The user cancelled the operation");
        }
    }

    private void updateTabTitle(String title) {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            Component tabComponent = tabbedPane.getTabComponentAt(index);
            if (tabComponent instanceof JPanel) {
                JPanel panel = (JPanel) tabComponent;
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JPanel) {
                        JPanel header = (JPanel) comp;
                        for (Component headerComp : header.getComponents()) {
                            if (headerComp instanceof JLabel) {
                                ((JLabel) headerComp).setText(title);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser("f:");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            addNewTab(); // новая вкладка для открываемого файла

            JTextArea textArea = getCurrentTextArea();
            if (textArea == null) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                textArea.setText(content.toString());
                textArea.putClientProperty("file", file);
                updateTabTitle(file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(frame, "The user cancelled the operation");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().toLowerCase();
        JTextArea textArea = getCurrentTextArea();
        if (textArea == null) return;

        switch (command) {
            case "new tab":
                addNewTab();
                break;
            case "cut":
                textArea.cut();
                break;
            case "copy":
                textArea.copy();
                break;
            case "paste":
                textArea.paste();
                break;
            case "save":
                saveFile(false);
                break;
            case "save as":
                saveFile(true);
                break;
            case "print":
                try {
                    textArea.print();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
                break;
            case "open":
                openFile();
                break;
            case "close tab":
                int index = tabbedPane.getSelectedIndex();
                if (index != -1) {
                    tabbedPane.remove(index);
                }
                break;
        }
    }
}