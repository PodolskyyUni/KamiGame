package org.example.game;


import java.util.*;

public class GameEngine {
    private int points=0;
    private int round=0;
    public Grid grid = new Grid();
    public void changeColor(Color color, Sector sector){
        List<Sector> sectors=getSameColorSectors(sector);
        for (Sector value : sectors) {
            value.setColor(color);
            points++;
        }
    }
    private List<Sector> getSameColorSectors(Sector sector) {
        Set<Sector> sameColored = new HashSet<>();
        Queue<Sector> queue = new LinkedList<>();

        queue.add(sector);
        sameColored.add(sector);

        while (!queue.isEmpty()) {
            Sector current = queue.poll();
            for (Sector neighbor : getNeighbours(current)) {
                if (!sameColored.contains(neighbor) && neighbor.getColor().equals(current.getColor())) {
                    sameColored.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return new ArrayList<>(sameColored);
    }

    public List<Sector> getNeighbours(Sector sector){
        List<Sector> neighbours = new ArrayList<>();
        int x= sector.getX();
        int y= sector.getY();
        if(sector.getX()<grid.getLength()-1) neighbours.add(grid.getBoardSector(x+1, y));
        if(sector.getX()>0) neighbours.add(grid.getBoardSector(x-1, y));
        if(sector.getY()<grid.getHeight()-1) neighbours.add(grid.getBoardSector(x, y+1));
        if(sector.getY()>0) neighbours.add(grid.getBoardSector(x, y-1));
        return neighbours;
    }
    public boolean roundOver(){
        return grid.sameColored(grid.getBoard());
    }

    public int getPoints() {
        return points;
    }
}
