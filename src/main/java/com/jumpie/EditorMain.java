package com.jumpie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditorMain extends JFrame implements ActionListener, TextAppender {
    private TabManager tabManager;
    private FileManager fileManager;
    private VoiceRecognitionService voiceService;
    private EditorMenuBar editorMenuBar;
    private JButton voiceButton;

    public EditorMain() {
        super("Jumpie TextNote");
        setupLookAndFeel();
        initializeComponents();
        setupFrame();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");

            // Основной серый фон
            UIManager.put("Panel.background", new Color(60, 63, 65));
            UIManager.put("Viewport.background", new Color(60, 63, 65));
            UIManager.put("TextArea.background", new Color(45, 45, 50));

            // Цвета меню
            UIManager.put("MenuItem.background", new Color(80, 80, 85));
            UIManager.put("MenuItem.foreground", Color.WHITE);
            UIManager.put("MenuItem.selectionBackground", new Color(96, 208, 191));
            UIManager.put("MenuItem.selectionForeground", Color.BLACK);

            // Для выпадающих меню
            UIManager.put("PopupMenu.background", new Color(80, 80, 85));
            UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(new Color(60, 63, 65)));

            // Панель меню
            UIManager.put("MenuBar.background", new Color(70, 70, 75));
            UIManager.put("Menu.background", new Color(70, 70, 75));
            UIManager.put("Menu.foreground", Color.WHITE);
            UIManager.put("Menu.selectionBackground", new Color(96, 208, 191));

            // Вкладки
            UIManager.put("TabbedPane.background", new Color(60, 63, 65));
            UIManager.put("TabbedPane.foreground", Color.WHITE);
            UIManager.put("TabbedPane.selected", new Color(96, 208, 191));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        tabManager = new TabManager();
        fileManager = new FileManager(this, tabManager);
        voiceService = new VoiceRecognitionService(this, "voicemodels/voskSmallRu0.22");
        editorMenuBar = new EditorMenuBar(this);
        voiceButton = editorMenuBar.getVoiceButton();

        voiceService.setOnStateChangeListener(this::updateVoiceButtonState);
        voiceButton.addActionListener(e -> voiceService.toggleRecognition(this));
    }

    private void updateVoiceButtonState() {
        if (voiceService.isListening()) {
            voiceButton.setText("Stop");
            voiceButton.setToolTipText("Stop voice input");
        } else {
            voiceButton.setText("Record");
            voiceButton.setToolTipText("Start voice input");
        }
    }

    private void setupFrame() {
        setJMenuBar(editorMenuBar.getMenuBar());
        add(tabManager.getTabbedPane());

        getContentPane().setBackground(new Color(60, 63, 65));

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void appendText(String text) {
        JTextArea textArea = tabManager.getCurrentTextArea();
        if (textArea != null) {
            textArea.append(text);
        }
    }

    @Override
    public void dispose() {
        voiceService.dispose();
        super.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().toLowerCase();
        switch (command) {
            case "new tab":
                tabManager.addNewTab();
                break;
            case "open":
                fileManager.openFile();
                break;
            case "save":
                fileManager.saveFile(false);
                break;
            case "save as":
                fileManager.saveFile(true);
                break;
            case "close tab":
                tabManager.closeCurrentTab();
                break;
            case "cut":
                tabManager.getCurrentTextArea().cut();
                break;
            case "copy":
                tabManager.getCurrentTextArea().copy();
                break;
            case "paste":
                tabManager.getCurrentTextArea().paste();
                break;
            case "print":
                printTextArea();
                break;
            case "zoom in", "+":
                tabManager.zoomIn();
                break;
            case "zoom out", "-":
                tabManager.zoomOut();
                break;
            case "reset zoom", "100%":
                tabManager.resetZoom();
                break;
        }
    }

    private void printTextArea() {
        try {
            tabManager.getCurrentTextArea().print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new EditorMain();
    }
}