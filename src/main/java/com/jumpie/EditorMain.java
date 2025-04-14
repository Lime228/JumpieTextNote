package com.jumpie;

import javax.swing.*;
import javax.swing.text.*;
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

            Color panelColor = new Color(60, 63, 65);
            Color textAreaColor = new Color(45, 45, 50);
            Color menuItemBg = new Color(80, 80, 85);
            Color selectionBg = new Color(96, 208, 191);
            Color menuBarBg = new Color(70, 70, 75);

            UIManager.put("Panel.background", panelColor);
            UIManager.put("Viewport.background", panelColor);
            UIManager.put("TextArea.background", textAreaColor);
            UIManager.put("MenuItem.background", menuItemBg);
            UIManager.put("MenuItem.foreground", Color.WHITE);
            UIManager.put("MenuItem.selectionBackground", selectionBg);
            UIManager.put("MenuItem.selectionForeground", Color.BLACK);
            UIManager.put("PopupMenu.background", menuItemBg);
            UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(panelColor));
            UIManager.put("MenuBar.background", menuBarBg);
            UIManager.put("Menu.background", menuBarBg);
            UIManager.put("Menu.foreground", Color.WHITE);
            UIManager.put("Menu.selectionBackground", selectionBg);
            UIManager.put("TabbedPane.background", panelColor);
            UIManager.put("TabbedPane.foreground", Color.WHITE);
            UIManager.put("TabbedPane.selected", selectionBg);
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

    private void setupFrame() {
        setJMenuBar(editorMenuBar.getMenuBar());
        add(createFontToolBar(), BorderLayout.NORTH);
        add(tabManager.getTabbedPane());

        getContentPane().setBackground(new Color(85, 89, 93));

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void updateVoiceButtonState() {
        voiceButton.setText(voiceService.isListening() ? "Stop" : "Record");
        voiceButton.setToolTipText(voiceService.isListening() ? "Stop voice input" : "Start voice input");
    }

    @Override
    public void appendText(String text) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                doc.insertString(doc.getLength(), text, null);
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
            case "new tab" -> tabManager.addNewTab();
            case "open" -> fileManager.openFile();
            case "save" -> fileManager.saveFile(false);
            case "save as" -> fileManager.saveFile(true);
            case "close tab" -> tabManager.closeCurrentTab();
            case "cut" -> tabManager.getCurrentTextComponent().cut();
            case "copy" -> tabManager.getCurrentTextComponent().copy();
            case "paste" -> tabManager.getCurrentTextComponent().paste();
            case "print" -> printTextArea();
            case "zoom in", "+" -> tabManager.zoomIn();
            case "zoom out", "-" -> tabManager.zoomOut();
            case "reset zoom", "100%" -> tabManager.resetZoom();
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
        applyFontAttribute(attr -> StyleConstants.setFontFamily(attr, fontFamily));
    }

    public void changeFontSize(int size) {
        applyFontAttribute(attr -> StyleConstants.setFontSize(attr, size));
    }

    public void toggleFontStyle(int style) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            int pos = textPane.getCaretPosition();
            AttributeSet current = doc.getCharacterElement(Math.max(pos - 1, 0)).getAttributes();
            boolean isBold = StyleConstants.isBold(current);
            boolean isItalic = StyleConstants.isItalic(current);

            if (style == Font.BOLD) StyleConstants.setBold(attrs, !isBold);
            if (style == Font.ITALIC) StyleConstants.setItalic(attrs, !isItalic);

            applyAttributesToSelection(textPane, doc, attrs);
        }
    }

    private void applyFontAttribute(java.util.function.Consumer<SimpleAttributeSet> consumer) {
        JTextPane textPane = tabManager.getCurrentTextComponent();
        if (textPane != null) {
            StyledDocument doc = textPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            consumer.accept(attrs);
            applyAttributesToSelection(textPane, doc, attrs);
        }
    }

    private void applyAttributesToSelection(JTextPane textPane, StyledDocument doc, SimpleAttributeSet attrs) {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start == end) {
            textPane.setCharacterAttributes(attrs, false);
        } else {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        }
    }

    private JToolBar createFontToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(80, 80, 85));

        JComboBox<String> fontCombo = new JComboBox<>(GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        fontCombo.setSelectedItem("Consolas");
        fontCombo.addActionListener(e -> changeFontFamily((String) fontCombo.getSelectedItem()));

        JComboBox<Integer> sizeCombo = new JComboBox<>(new Integer[]{8, 10, 12, 14, 16, 18, 20, 24});
        sizeCombo.setSelectedItem(14);
        sizeCombo.addActionListener(e -> changeFontSize((Integer) sizeCombo.getSelectedItem()));

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
        Color normalBg = new Color(80, 80, 85);
        Color hoverBg = new Color(96, 208, 191);

        btn.setBackground(normalBg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.addActionListener(e -> toggleFontStyle(style));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            public void mouseExited(MouseEvent e) { btn.setBackground(normalBg); }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditorMain::new);
    }
}