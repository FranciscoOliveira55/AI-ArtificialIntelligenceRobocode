package impl;

import robocode.RobocodeFileOutputStream;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Classe usada para guardar os dados dos robots inimigos, quando sou atingido
 */
public class DadosSectionRisk {

    //SectionsConfig    horizontal#vertical
    private double sectionWidth; //the width of each section
    private double sectionHeight; //the height of each section


    //SectionsInfo
    private ArrayList<String> sectionNames; //Stores the names of the sections vertical#horizontal # I needed because i need an ORDERED pair
    private HashMap<String, String> enemyInSectionHash; //Stores an enemy in a section
    private HashMap<String, Point> enemyPositionHash;

    //Other Variables
    private Long time; //tempo atual
    private String myName;//nome do meu tank
    private Double myVelocity; //the velocity of my tank
    private Double myHeading; //meu angulo em relacao ao note
    private Double myX;
    private Double myY;

    public DadosSectionRisk(
            double widthOfMap,
            double heightOfMap,
            int numberOfHorizontalSections,
            int numberOfVerticalSections
    ) {

        this.sectionNames = new ArrayList<>();
        this.enemyInSectionHash = new HashMap<>();
        this.enemyPositionHash = new HashMap<>();

        this.time = 0L;
        this.myName = "myName";
        this.myVelocity = 0.0;
        this.myHeading = 0.0;
        this.myX = 0.0;
        this.myY = 0.0;

        this.setSectionsConfigs(widthOfMap, heightOfMap, numberOfHorizontalSections, numberOfVerticalSections);
    }


    /**
     * generates the sections
     *
     * @param widthOfMap
     * @param heightOfMap
     * @param numberOfHorizontalSections
     * @param numberOfVerticalSections
     */
    private void setSectionsConfigs(double widthOfMap, double heightOfMap, int numberOfHorizontalSections, int numberOfVerticalSections) {
        //CalculateSectionDimensions
        this.sectionWidth = widthOfMap / numberOfHorizontalSections;
        this.sectionHeight = heightOfMap / numberOfVerticalSections;

        //GenerateSectionNames
        generateSectionNames(numberOfHorizontalSections, numberOfVerticalSections);
    }

    /**
     * Generates the names of the sections
     *
     * @param numberOfVerticalSections
     * @param numberOfHorizontalSections
     */
    private void generateSectionNames(int numberOfHorizontalSections, int numberOfVerticalSections) {
        ArrayList<String> tempSectionNames = new ArrayList<>(); //Stores the names of the sections vertical#horizontal

        //for each horizontal section
        for (int horizontalSection = 0; horizontalSection < numberOfHorizontalSections; horizontalSection++) {
            //for each vertical section
            for (int verticalSection = 0; verticalSection < numberOfVerticalSections; verticalSection++) {
                //Add a section names like horizontal#vertical
                tempSectionNames.add(horizontalSection + "#" + verticalSection);
            }
        }
        this.sectionNames = tempSectionNames;
    }

    /**
     * Sets the momentary data of the map
     *
     * @param time
     * @param myName
     * @param myVelocity
     * @param myHeading
     * @param myX
     * @param myY
     */
    public void setMomentaryData(
            Long time,
            String myName,
            Double myVelocity,
            Double myHeading,
            Double myX,
            Double myY
    ) {
        this.time = time;
        this.myName = myName;
        this.myVelocity = myVelocity;
        this.myHeading = myHeading;
        this.myX = myX;
        this.myY = myY;
    }

    /**
     * Returns a rectangle object of the given section
     *
     * @param sectionName
     * @return
     */
    public Rectangle getSectionAsRectangle(String sectionName) {
        String[] parts = sectionName.split("#"); // X#Y
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid section name: " + sectionName);
        }

        try {
            //Sets sectionNumbers
            int numberOfHorizontalSection = Integer.parseInt(parts[0]);
            int numberOfVerticalSection = Integer.parseInt(parts[1]);

            //The top left corner of 0#0 is (0,0), of 2#0 is (2*sectionWith, 0)
            double topLeftCornerX = (numberOfHorizontalSection * this.sectionWidth);
            double topLeftCornerY = (numberOfVerticalSection * this.sectionHeight);

            //Return section as a rectangle
            return new Rectangle((int) topLeftCornerX, (int) topLeftCornerY, (int) this.sectionWidth, (int) this.sectionHeight);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid section name: " + sectionName, e);
        }
    }

    /**
     * Given a section name, returns the point in the center of that section
     *
     * @param sectionName
     * @return
     */
    public Point getSectionPoint(String sectionName, boolean moveToSectionCenter) {
        String[] parts = sectionName.split("#"); // X#Y
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid section name: " + sectionName);
        }

        try {
            int numberOfHorizontalSection = Integer.parseInt(parts[0]);
            int numberOfVerticalSection = Integer.parseInt(parts[1]);

            //The middleX of section 0#0 is 0*sectionWidth + 0.5*sectionWidth
            double centerX = (numberOfHorizontalSection * this.sectionWidth);
            double centerY = (numberOfVerticalSection * this.sectionHeight);

            //If i want to move to section center
            if (moveToSectionCenter) {
                centerX += (0.5 * this.sectionWidth);
                centerY += (0.5 * this.sectionHeight);
            } else {
                //Move to a random point in the section
                Random ran = new Random();
                centerX += ran.nextDouble(1, this.sectionWidth);
                centerY += ran.nextDouble(1, this.sectionHeight);
            }

            return new Point((int) centerX, (int) centerY);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid section name: " + sectionName, e);
        }
    }

    /**
     * Returns the name of the section corresponding to the given coordinates
     *
     * @param x
     * @param y
     * @return
     */
    public String getSectionName(double x, double y) {
        int numberOfHorizontalSection = (int) (x / this.sectionWidth);
        int numberOfVerticalSection = (int) (y / this.sectionHeight);

        return numberOfHorizontalSection + "#" + numberOfVerticalSection;
    }

    /**
     * Returns an arraylist with the ordered name of the sections
     *
     * @return
     */
    public ArrayList<String> getSectionNames() {
        return sectionNames;
    }

    /**
     * Returns the current number of enemies in all the sections
     *
     * @return
     */
    public HashMap<String, Integer> getNumberOfEnemiesPerSection() {
        //Creates hash to store enemies per section
        HashMap<String, Integer> enemiesPerSection = new HashMap<>();
        //For each section, gets the number of enemies in it
        for (String section : sectionNames) {
            //And stores it in the hash
            enemiesPerSection.put(section, this.getNumberOfEnemiesInSection(section));
        }
        //Then, returns the hash
        return enemiesPerSection;
    }

    /**
     * Returns the current number of enemies in a given section
     *
     * @param nameOfSection
     * @return
     */
    public int getNumberOfEnemiesInSection(String nameOfSection) {
        int numberOfEnemiesInSection = 0;
        //For each section value in the hash
        for (String section : this.enemyInSectionHash.values()) {
            //Check if the section is the one we are looking for
            if (section.equals(nameOfSection)) {
                numberOfEnemiesInSection++;
            }
        }
        return numberOfEnemiesInSection;
    }

    /**
     * Registers the current position of an enemy
     *
     * @param enemyName
     * @param x
     * @param y
     */
    public void registerEnemyPosition(String enemyName, double x, double y) {
        //Registers an enemy in a section using the hash
        this.enemyInSectionHash.put(enemyName, this.getSectionName(x, y));
        this.enemyPositionHash.put(enemyName, new Point((int) x, (int) y));
    }

    /**
     * Removes an enemy from the sections
     *
     * @param enemyName
     */
    public void removeEnemy(String enemyName) {
        enemyInSectionHash.remove(enemyName);
        enemyPositionHash.remove(enemyName);
    }

    public Double getMyVelocity() {
        return myVelocity;
    }

    public String getMyName() {
        return myName;
    }

    public Double getMyHeading() {
        return myHeading;
    }

    public Double getMyX() {
        return myX;
    }

    public Double getMyY() {
        return myY;
    }

    public Long getTime() {
        return time;
    }

    private static double getDistance(Point startPoint, Point endPoint) {
        if (startPoint == null || endPoint == null) {
            System.err.println("WARNING: Trying to calculate distance with null points");
            return -1;
        }
        // Calculate the differences in x and y coordinates
        double dx = startPoint.getX() - endPoint.getX();
        double dy = startPoint.getY() - endPoint.getY();

        return Math.hypot(dx, dy);
    }


    public double getAverageEnemyDistance(Point myPosition) {
        double totalDistance = 0;
        int numberOfEnemies = 0;

        //For each enemy, calculate the distance between me and them
        for (Point enemyPosition : enemyPositionHash.values()) {
            totalDistance += DadosSectionRisk.getDistance(myPosition, enemyPosition);
            numberOfEnemies++;
        }

        //Return the average
        if (numberOfEnemies != 0) {
            System.out.println("AverageEnemyDistance: " + totalDistance / numberOfEnemies);
            return totalDistance / numberOfEnemies;
        } else {
            System.err.println("WARNING: Trying to calculate averageEnemyDistance with no enemies");
            return -1;
        }
    }


}
