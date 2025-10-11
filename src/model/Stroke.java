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
    
    // Data access methods
    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }
    
    public void setPoints(List<Point> points) {
        this.points = new ArrayList<>(points);
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
    
    public boolean isEmpty() {
        return points.isEmpty();
    }
    
    // Point manipulation methods
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }
    
    public void addPoint(Point point) {
        points.add(new Point(point));
    }
    
    public Point getCenter() {
        if (points.isEmpty()) {
            return new Point(0, 0);
        }
        
        int sumX = 0, sumY = 0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        return new Point(sumX / points.size(), sumY / points.size());
    }
    
    // Movement methods
    public void moveBy(int dx, int dy) {
        for (Point point : points) {
            point.x += dx;
            point.y += dy;
        }
    }
}
