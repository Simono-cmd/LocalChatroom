package com.example.GUI;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField() {
        super();
    }

    public PlaceholderTextField(int columns) {
        super(columns);
    }


    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty() || isFocusOwner()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        Insets insets = getInsets();
        FontMetrics fm = g2.getFontMetrics();
        int x = insets.left + 2;
        int y = getHeight() / 2 + fm.getAscent() / 2 - 2;
        g2.drawString(placeholder, x, y);
        g2.dispose();
    }
}
