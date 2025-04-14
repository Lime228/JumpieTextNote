package com.jumpie;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class TabManager {
    private final JTabbedPane tabbedPane;
    private float currentZoom = 1.0f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 5.0f;
    private static final float ZOOM_STEP = 0.5f;

    public TabManager() {
        this.tabbedPane = new JTabbedPane();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void addNewTab() {
        JTextPane textPane = createTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);

        String title = "New Document " + (tabbedPane.getTabCount() + 1);
        JPanel tabHeader = createTabHeader(title, scrollPane);

        tabbedPane.addTab(null, scrollPane);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabHeader);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void closeCurrentTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) tabbedPane.remove(index);
    }

    public JTextPane getCurrentTextComponent() {
        Component component = tabbedPane.getSelectedComponent();
        if (component instanceof JScrollPane scroll) {
            Component view = scroll.getViewport().getView();
            if (view instanceof JTextPane textPane) return textPane;
        }
        return null;
    }

    public void updateTabTitle(String title) {
        int index = tabbedPane.getSelectedIndex();
        if (index == -1) return;

        Component tabComponent = tabbedPane.getTabComponentAt(index);
        if (tabComponent instanceof JPanel panel) {
            for (Component comp : panel.getComponents()) {
                if (comp instanceof JPanel inner) {
                    for (Component labelComp : inner.getComponents()) {
                        if (labelComp instanceof JLabel label) {
                            label.setText(title);
                            return;
                        }
                    }
                }
            }
        }
    }

    public void zoomIn() {
        setZoom(currentZoom + ZOOM_STEP);
    }

    public void zoomOut() {
        setZoom(currentZoom - ZOOM_STEP);
    }

    public void resetZoom() {
        scaleDocument(1.0f / currentZoom);
        currentZoom = 1.0f;
    }

    public void setZoom(float newZoom) {
        newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));
        if (Math.abs(currentZoom - newZoom) > 0.01f) {
            float zoomFactor = newZoom / currentZoom;
            currentZoom = newZoom;
            scaleDocument(zoomFactor);
        }
    }

    private void scaleDocument(float zoomFactor) {
        JTextPane textPane = getCurrentTextComponent();
        if (textPane == null) return;

        try {
            StyledDocument doc = textPane.getStyledDocument();
            int length = doc.getLength();

            int caretPos = textPane.getCaretPosition();
            int selStart = textPane.getSelectionStart();
            int selEnd = textPane.getSelectionEnd();

            for (int i = 0; i < length;) {
                Element elem = doc.getCharacterElement(i);
                AttributeSet attrs = elem.getAttributes();
                int end = elem.getEndOffset();

                SimpleAttributeSet newAttrs = new SimpleAttributeSet(attrs);
                int originalSize = StyleConstants.getFontSize(attrs);
                int newSize = Math.max(1, Math.round(originalSize * zoomFactor));
                StyleConstants.setFontSize(newAttrs, newSize);

                doc.setCharacterAttributes(i, end - i, newAttrs, false);
                i = end;
            }

            textPane.setCaretPosition(caretPos);
            textPane.select(selStart, selEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createTabHeader(String title, JScrollPane scrollPane) {
        JLabel tabLabel = new JLabel(title);
        tabLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabLabel.setForeground(Color.WHITE);

        JButton closeButton = new JButton("x");
        closeButton.setMargin(new Insets(0, 5, 0, 0));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfComponent(scrollPane);
            if (index != -1) tabbedPane.remove(index);
        });

        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        tabHeader.add(tabLabel);
        tabHeader.add(closeButton);

        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        tabPanel.add(tabHeader, BorderLayout.CENTER);

        return tabPanel;
    }

    private static JTextPane createTextPane() {
        JTextPane textPane = new JTextPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(45, 45, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, "Consolas");
        StyleConstants.setFontSize(attrs, 14);
        StyleConstants.setForeground(attrs, Color.WHITE);

        textPane.setParagraphAttributes(attrs, true);
        textPane.setCaretColor(Color.WHITE);
        textPane.setSelectionColor(new Color(96, 208, 191));
        textPane.setSelectedTextColor(Color.BLACK);
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        return textPane;
    }
}
