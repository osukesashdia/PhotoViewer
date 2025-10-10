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

    // Hit testing and movement methods
    public boolean containsPoint(int x, int y) {
        if (points.size() < 2) return false;
        
        // Check if point is within strokeWidth distance of any line segment
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            
            if (isPointNearLine(x, y, p1.x, p1.y, p2.x, p2.y, strokeWidth)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPointNearLine(int px, int py, int x1, int y1, int x2, int y2, float strokeWidth) {
        // Calculate distance from point to line segment
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        
        if (lenSq == 0) {
            // Line segment has zero length
            return Math.sqrt(A * A + B * B) <= strokeWidth / 2;
        }

        double param = dot / lenSq;
        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy) <= strokeWidth / 2;
    }

    public void moveBy(int dx, int dy) {
        for (Point point : points) {
            point.x += dx;
            point.y += dy;
        }
    }

    public Point getCenter() {
        if (points.isEmpty()) return new Point(0, 0);
        
        int sumX = 0, sumY = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        return new Point(sumX / points.size(), sumY / points.size());
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
}
