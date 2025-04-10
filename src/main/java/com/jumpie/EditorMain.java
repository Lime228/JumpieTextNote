package com.jumpie;

import javax.swing.*;
import java.awt.event.*;

public class EditorMain extends JFrame implements ActionListener, TextAppender {
    private TabManager tabManager;
    private FileManager fileManager;
    private VoiceRecognitionService voiceService;
    private EditorMenuBar editorMenuBar;
    private JButton voiceButton;

    public EditorMain() {
        super("Editor with Tabs");
        setupLookAndFeel();
        initializeComponents();
        setupFrame();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        tabManager = new TabManager();
        fileManager = new FileManager(this, tabManager);
        voiceService = new VoiceRecognitionService(this, "voicemodels/voskSmallRu0.22");
        editorMenuBar = new EditorMenuBar(this);
        voiceButton = editorMenuBar.getVoiceButton(); // Сохраняем ссылку на кнопку

        voiceService.setOnStateChangeListener(this::updateVoiceButtonState);
        voiceButton.addActionListener(e -> voiceService.toggleRecognition(this));
    }

    private void updateVoiceButtonState() {
        if (voiceService.isListening()) {
            voiceButton.setText("⏹");
            voiceButton.setToolTipText("Stop voice input");
        } else {
            voiceButton.setText("🎤");
            voiceButton.setToolTipText("Start voice input");
        }
    }

    private void setupFrame() {
        setJMenuBar(editorMenuBar.getMenuBar());
        add(tabManager.getTabbedPane());
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
            case "new tab": tabManager.addNewTab(); break;
            case "open": fileManager.openFile(); break;
            case "save": fileManager.saveFile(false); break;
            case "save as": fileManager.saveFile(true); break;
            case "close tab": tabManager.closeCurrentTab(); break;
            case "cut": tabManager.getCurrentTextArea().cut(); break;
            case "copy": tabManager.getCurrentTextArea().copy(); break;
            case "paste": tabManager.getCurrentTextArea().paste(); break;
            case "print": printTextArea(); break;
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