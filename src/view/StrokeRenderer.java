package view;

import java.awt.*;
import java.util.List;

public class StrokeRenderer {
    public void drawStroke(Graphics2D g2, Object stroke) {
        if (isEmpty(stroke)) return;
        
        g2.setColor(getColor(stroke));
        g2.setStroke(new BasicStroke(getStrokeWidth(stroke), 
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        List<Point> points = getPoints(stroke);
        if (points.size() < 2) return;
        
        Point prevPoint = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            Point currentPoint = points.get(i);
            g2.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
            prevPoint = currentPoint;
        }
    }
    
    // Helper methods to access stroke data without knowing the model class
    private boolean isEmpty(Object stroke) {
        try {
            return (Boolean) stroke.getClass().getMethod("isEmpty").invoke(stroke);
        } catch (Exception e) {
            return true;
        }
    }
    
    private Color getColor(Object stroke) {
        try {
            return (Color) stroke.getClass().getMethod("getColor").invoke(stroke);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }
    
    private float getStrokeWidth(Object stroke) {
        try {
            return (Float) stroke.getClass().getMethod("getStrokeWidth").invoke(stroke);
        } catch (Exception e) {
            return 2.0f;
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Point> getPoints(Object stroke) {
        try {
            return (List<Point>) stroke.getClass().getMethod("getPoints").invoke(stroke);
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}
