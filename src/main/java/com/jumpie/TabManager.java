package com.jumpie;

import javax.swing.*;
import java.awt.*;

public class TabManager {
    private JTabbedPane tabbedPane;
    private float currentZoom = 1.0f;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 5.0f;
    private static final float ZOOM_STEP = 0.5f;

    public TabManager() {
        tabbedPane = new JTabbedPane();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void addNewTab() {
        JTextArea textArea = getjTextArea();
//        textArea.addMouseWheelListener(e -> { работает криво
//            if (e.isControlDown()) {
//                if (e.getWheelRotation() < 0) {
//                    zoomIn();
//                } else {
//                    zoomOut();
//                }
//            }
//        });

        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel tabPanel = createTabHeader("New Document " + (tabbedPane.getTabCount() + 1), scrollPane);

        tabbedPane.addTab(null, scrollPane);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    private static JTextArea getjTextArea() {
        JTextArea textArea = new JTextArea() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(45, 45, 50));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
            }
        };

        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        textArea.setSelectionColor(new Color(96, 208, 191));
        textArea.setSelectedTextColor(Color.BLACK);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return textArea;
    }

    public void closeCurrentTab() {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            tabbedPane.remove(index);
        }
    }

    public JTextArea getCurrentTextArea() {
        Component component = tabbedPane.getSelectedComponent();
        if (component instanceof JScrollPane) {
            return (JTextArea) ((JScrollPane) component).getViewport().getView();
        }
        return null;
    }

    public void updateTabTitle(String title) {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            Component tabComponent = tabbedPane.getTabComponentAt(index);
            if (tabComponent instanceof JPanel) {
                for (Component comp : ((JPanel) tabComponent).getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component headerComp : ((JPanel) comp).getComponents()) {
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

    private JPanel createTabHeader(String title, JScrollPane scrollPane) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);

        JLabel tabLabel = new JLabel(title);
        tabLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabLabel.setForeground(Color.white);

        JButton closeButton = createCloseButton(scrollPane);


        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        tabHeader.add(tabLabel);
        tabHeader.add(closeButton);

        tabPanel.add(tabHeader, BorderLayout.CENTER);
        return tabPanel;
    }

    private JButton createCloseButton(JScrollPane scrollPane) {
        JButton closeButton = new JButton("x");
        closeButton.setMargin(new Insets(0, 5, 0, 0));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);

        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfComponent(scrollPane);
            if (index != -1) tabbedPane.remove(index);
        });
        return closeButton;
    }

    public void zoomIn() {
        float newZoom = currentZoom + ZOOM_STEP;
        newZoom = Math.min(Math.round(newZoom * 10) / 10.0f, MAX_ZOOM);
        changeZoom(newZoom);
    }

    public void zoomOut() {
        float newZoom = currentZoom - ZOOM_STEP;
        newZoom = Math.max(Math.round(newZoom * 10) / 10.0f, MIN_ZOOM);
        changeZoom(newZoom);
    }

    public void resetZoom() {
        changeZoom(1.0f);
    }

    private void changeZoom(float newZoom) {
        if (Math.abs(currentZoom - newZoom) > 0.01f) {
            currentZoom = newZoom;
            updateCurrentTabFont();
        }
    }

    private void updateCurrentTabFont() {
        JTextArea textArea = getCurrentTextArea();
        if (textArea != null) {
            Font currentFont = textArea.getFont();
            float baseSize = 12;
            float newSize = baseSize * currentZoom;
            Font newFont = currentFont.deriveFont(newSize);
            textArea.setFont(newFont);
        }
    }
}