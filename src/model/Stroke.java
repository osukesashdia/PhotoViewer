package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Stroke {
    private List<Point> points;
    private Color color;
    private float strokeWidth;

    public Stroke(Color color, float strokeWidth) {
        this.points = new ArrayList<>();
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public void draw(Graphics2D g2) {
        if (points.size() < 2) return;
        g2.setColor(color);
        g2.setStroke(new java.awt.BasicStroke(strokeWidth, 
            java.awt.BasicStroke.CAP_ROUND, 
            java.awt.BasicStroke.JOIN_ROUND));
        Point prevPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            g2.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
            prevPoint = currentPoint;
        }
    }
}
