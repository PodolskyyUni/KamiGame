package org.example.game;

import java.util.Objects;
import java.util.Random;

public class Sector {
    private int x, y;
    private Color color;
    public Sector(int x, int y){
        this.x=x;
        this.y=y;
        this.color=getRandomColor();
    }
    private Color getRandomColor() {
        int index = (int) (Math.random() * 8);
        return switch (index) {
            case 0 -> Color.GREEN;
            case 1 -> Color.RED;
            case 2 -> Color.YELLOW;
            case 3 -> Color.BLUE;
            case 4 -> Color.BLACK;
            case 5 -> Color.ORANGE;
            case 6 -> Color.GRAY;
            default -> Color.PINK;
        };
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sector sector = (Sector) obj;
        return x == sector.x && y == sector.y && color == sector.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, color);
    }

}
