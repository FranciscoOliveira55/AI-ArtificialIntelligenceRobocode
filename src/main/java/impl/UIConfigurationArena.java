package impl;

import interf.IPoint;
import interf.IUIConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class that models the configuration for a given problem
 */
public class UIConfigurationArena implements IUIConfiguration
{
    //Dimensions of the map
    private int width;
    private int height;
    private int borderMargin;

    //Information about the enemies
    private List<Rectangle> obstacles; //Stores the enemies as rectangles
    public HashMap<String, Rectangle> enemiesAsObstacles; //Associates an enemy to a rectangle

    //Information about the path
    public IPoint start, end;

    //Information about the sections
    private HashMap<Rectangle, Double> sectionSafety; //Stores the safety of each section

    public UIConfigurationArena(int width, int height, List<Rectangle> obstacles) {
        this.width = width;
        this.height = height;
        this.borderMargin = 0;

        this.obstacles = obstacles;
        this.enemiesAsObstacles = new HashMap<>();

        this.sectionSafety = new HashMap<>();
    }

    public UIConfigurationArena(int width, int height, int borderMargin) {
        this.width = width;
        this.height = height;
        this.borderMargin = borderMargin;

        this.obstacles = new ArrayList<>();
        this.enemiesAsObstacles = new HashMap<>();

        this.sectionSafety = new HashMap<>();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getBorderMargin() {
        return borderMargin;
    }

    public void setBorderMargin(int borderMargin) {
        this.borderMargin = borderMargin;
    }


    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Rectangle> obstacles) {
        this.obstacles = obstacles;
    }


    public HashMap<String, Rectangle> getEnemiesAsObstacles() {
        return enemiesAsObstacles;
    }

    public void setEnemiesAsObstacles(HashMap<String, Rectangle> enemiesAsObstacles) {
        this.enemiesAsObstacles = enemiesAsObstacles;
    }




    public int getDistance(){
        if(start == null || end == null){
            System.err.println("WARNING: Trying to calculate distance with null points");
            return -1;
        }
        // Calculate the differences in x and y coordinates
        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();

        return (int) Math.hypot(dx, dy);
    }

    @Override
    public IPoint getStart() {
        return start;
    }

    @Override
    public void setStart(IPoint iPoint) {
        this.start = iPoint;
    }

    @Override
    public IPoint getEnd() {
        return end;
    }

    @Override
    public void setEnd(IPoint iPoint) {
        this.end = iPoint;
    }


    public HashMap<Rectangle, Double> getSectionSafety() {
        return sectionSafety;
    }

    public void setSectionSafety(HashMap<Rectangle, Double> sectionSafety) {
        this.sectionSafety = sectionSafety;
    }



}
