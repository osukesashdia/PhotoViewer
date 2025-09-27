import java.awt.*;

class Annotation {
    String text;
    int x, y;
    
    public Annotation(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }
    
    /**
     * Draw this annotation with word wrapping
     */
    public void draw(Graphics2D g2, int photoWidth) {
        if (text == null || text.isEmpty()) return;
        
        Font font = new Font("Arial", Font.BOLD, 12);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        
        int maxWidth = photoWidth - x - 10; // Leave some margin
        if (maxWidth <= 0) return;
        
        String[] words = text.split(" ");
        int currentX = x;
        int currentY = y;
        int lineHeight = fm.getAscent() + fm.getDescent() + fm.getLeading();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            int lineWidth = fm.stringWidth(testLine);
            
            if (lineWidth <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                // Draw current line
                if (currentLine.length() > 0) {
                    // Draw text with shadow for better visibility
                    g2.setColor(Color.BLACK);
                    g2.drawString(currentLine.toString(), currentX + 1, currentY + 1);
                    g2.setColor(Color.RED);
                    g2.drawString(currentLine.toString(), currentX, currentY);
                    currentY += lineHeight;
                    currentLine = new StringBuilder(word);
                } else {
                    // Word is too long, break it
                    String remainingWord = word;
                    while (remainingWord.length() > 0) {
                        String testWord = remainingWord;
                        while (fm.stringWidth(testWord) > maxWidth && testWord.length() > 1) {
                            testWord = testWord.substring(0, testWord.length() - 1);
                        }
                        // Draw text with shadow
                        g2.setColor(Color.BLACK);
                        g2.drawString(testWord, currentX + 1, currentY + 1);
                        g2.setColor(Color.RED);
                        g2.drawString(testWord, currentX, currentY);
                        currentY += lineHeight;
                        remainingWord = remainingWord.substring(testWord.length());
                    }
                }
            }
        }
        
        // Draw remaining line
        if (currentLine.length() > 0) {
            // Draw text with shadow
            g2.setColor(Color.BLACK);
            g2.drawString(currentLine.toString(), currentX + 1, currentY + 1);
            g2.setColor(Color.RED);
            g2.drawString(currentLine.toString(), currentX, currentY);
        }
    }
}
