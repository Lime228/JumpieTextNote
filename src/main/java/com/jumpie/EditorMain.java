package com.jumpie;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class EditorMain extends JFrame implements ActionListener, TextAppender {
    TabManager tabManager;
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

        add(createFontToolBar(), BorderLayout.NORTH);
        add(tabManager.getTabbedPane());

        getContentPane().setBackground(new Color(85, 89, 93));

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void appendText(String text) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                doc.insertString(doc.getLength(), text, null); // null = текущий стиль
                textPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
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
                tabManager.getCurrentTextComponent().cut();
                break;
            case "copy":
                tabManager.getCurrentTextComponent().copy();
                break;
            case "paste":
                tabManager.getCurrentTextComponent().paste();
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
            tabManager.getCurrentTextComponent().print();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public void changeFontFamily(String fontFamily) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attrs, fontFamily);

            applyAttributesToSelection(textPane, doc, attrs);
        }
    }

    public void changeFontSize(int size) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontSize(attrs, size);

            applyAttributesToSelection(textPane, doc, attrs);
        }
    }

    public void toggleFontStyle(int style) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();

            int pos = textPane.getCaretPosition();
            AttributeSet current = doc.getCharacterElement(pos > 0 ? pos - 1 : pos).getAttributes();

            boolean isBold = StyleConstants.isBold(current) ^ (style == Font.BOLD);
            boolean isItalic = StyleConstants.isItalic(current) ^ (style == Font.ITALIC);

            StyleConstants.setBold(attrs, isBold);
            StyleConstants.setItalic(attrs, isItalic);

            applyAttributesToSelection(textPane, doc, attrs);
        }
    }

    private void applyAttributesToSelection(JTextPane textPane, StyledDocument doc, SimpleAttributeSet attrs) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        try {
            if (start == end) {
                textPane.setCharacterAttributes(attrs, false);
            } else {
                doc.setCharacterAttributes(start, end - start, attrs, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JToolBar createFontToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(80, 80, 85));

        // Выбор шрифта
        JComboBox<String> fontCombo = new JComboBox<>(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames());
        fontCombo.setSelectedItem("Consolas");
        fontCombo.addActionListener(e ->
                changeFontFamily((String)fontCombo.getSelectedItem()));

        // Выбор размера
        JComboBox<Integer> sizeCombo = new JComboBox<>(new Integer[]{8,10,12,14,16,18,20,24});
        sizeCombo.setSelectedItem(14);
        sizeCombo.addActionListener(e ->
                changeFontSize((Integer)sizeCombo.getSelectedItem()));

        // Кнопки стилей
        JToggleButton boldBtn = createStyleButton("B", Font.BOLD);
        JToggleButton italicBtn = createStyleButton("I", Font.ITALIC);

        toolBar.add(new JLabel("Font: "));
        toolBar.add(fontCombo);
        toolBar.addSeparator();
        toolBar.add(new JLabel("Size: "));
        toolBar.add(sizeCombo);
        toolBar.addSeparator();
        toolBar.add(boldBtn);
        toolBar.add(italicBtn);

        return toolBar;
    }

    private JToggleButton createStyleButton(String text, int style) {
        JToggleButton btn = new JToggleButton(text);
        btn.setBackground(new Color(80, 80, 85));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btn.addActionListener(e -> toggleFontStyle(style));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(96, 208, 191));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(80, 80, 85));
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        new EditorMain();
    }
}