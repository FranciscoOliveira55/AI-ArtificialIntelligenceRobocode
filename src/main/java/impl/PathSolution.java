package impl;

import interf.IPoint;
//import viewer.PathViewer;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;

public class PathSolution implements Comparable<PathSolution> {

    private static UIConfigurationArena conf; //Map Configuration (given by the robot)
    private static PathConfiguration pathConf; //Generic Algorithm Configuration (external file)

    private List<IPoint> chromosome;
    private double fitnessScore;


    /**
     * Makes a pathSolution with the given chromosome
     * Also calculates the fitnessScore of the created solution
     *
     * @param
     */
    private PathSolution(List<IPoint> chromosome) {
        this.chromosome = this.generateChromosomeDeepCopy(chromosome);
        this.calculateFitnessScore();
    }

    /**
     * Makes a deep copy of the PathSolution provided as parameter
     */
    private PathSolution(PathSolution originalPathSolution) {
        this.chromosome = this.generateChromosomeDeepCopy(originalPathSolution.getChromosome());
        this.calculateFitnessScore();
    }

    public List<IPoint> getChromosome() {
        return chromosome;
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    /**
     * Generates and returns a deep copy of the provided Chromossome
     *
     * @return
     */
    private List<IPoint> generateChromosomeDeepCopy(List<IPoint> originalChromosome) {
        //Creates a list to store the copied genes
        List<IPoint> chromosomeDeepCopy = new ArrayList<>();
        //For each gene of the original array, makes a copy of it and store it in the list
        for (IPoint gene : originalChromosome)
            chromosomeDeepCopy.add(new Point((Point) gene));
        //Returns the list (chromosome)
        return chromosomeDeepCopy;
    }

    /**
     * Generates and returns a random chromosome
     *
     * @return
     */
    private static List<IPoint> generateRandomChromosome() {
        Random rand = new Random();
        //Creates a list to store the generated genes
        List<IPoint> generatedChromosome = new ArrayList<>();
        //Adds starting point (gene)
        generatedChromosome.add(PathSolution.validateAndTransformSingleGeneInArenaBounds(conf.getStart()));
        //Sets a random number of points for the path
        int size = rand.nextInt(pathConf.getMinNumberOfPointsPerPath(), pathConf.getMaxNumberOfPointsPerPath() + 1);
        //Generates random points (genes) and stores them in the list
        for (int i = 0; i < size; i++)
            generatedChromosome.add(
                    PathSolution.validateAndTransformSingleGeneInArenaBounds(
                            new Point(
                                    rand.nextInt(conf.getBorderMargin(), conf.getWidth() - conf.getBorderMargin()),
                                    rand.nextInt(conf.getBorderMargin(), conf.getHeight() - conf.getBorderMargin())
                            )));
        //Adds the ending point (gene)
        generatedChromosome.add(PathSolution.validateAndTransformSingleGeneInArenaBounds(conf.getEnd()));
        //Returns generated chromosome
        return generatedChromosome;
    }

    /**
     * Generates and returns a random solution
     *
     * @return
     */
    private static PathSolution generateRandomSolution() {
        //Creates a solution with a random chromosome
        PathSolution generatedSolution = new PathSolution(PathSolution.generateRandomChromosome());
        return generatedSolution;
    }

    /**
     * Generates and returns a list with X number of random solutions
     *
     * @return
     */
    private static List<PathSolution> generateXRandomSolutions(int numberOfRandomSolutions) {
        //Creates a list to store the generated solutions
        List<PathSolution> generatedSolutions = new ArrayList<>();
        //Generates random solutions and stores them in the list
        for (int i = 0; i < numberOfRandomSolutions; i++)
            generatedSolutions.add(PathSolution.generateRandomSolution());
        //Sorts the generated Solutions
        generatedSolutions.sort(Comparator.reverseOrder());
        //Returns the generated solutions
        return generatedSolutions;
    }

    /**
     * Validates if the pathSolution is valid
     *
     * @param
     * @return
     */
    private boolean pathSolutionValidateFunction() {
        int collisions = 0;
        //For each point (gene) of the chromosome (path)
        for (int i = 0; i < chromosome.size() - 1; i++) {
            //If we are using StrickArenaBoundsValidation, then validate each point
            if (pathConf.getUseStrickArenaBoundsValidation()) {
                chromosome.set(i, PathSolution.validateAndTransformSingleGeneInArenaBounds(chromosome.get(i)));
            }
            //Sets a 2dPoint with the current point
            Point2D.Double point1 = new Point2D.Double(chromosome.get(i).getX(), chromosome.get(i).getY());
            //Sets a 2dPoint with the next point
            Point2D.Double point2 = new Point2D.Double(chromosome.get(i + 1).getX(), chromosome.get(i + 1).getY());
            //Sets a line between the 2 points
            Line2D.Double line = new Line2D.Double(point1, point2);
            //For each obstacle in the map, check if it intercepts the line
            for (Rectangle obstacle : conf.enemiesAsObstacles.values()) {
                if (obstacle.intersectsLine(line)) {
                    collisions++;
                }
            }
        }
        //If there are no collisions, then the solution is valid, otherwise is not
        if (collisions == 0)
            return true;
        else
            return false;
    }

    /**
     * Checks a given gene is in the bounds of the arena
     *
     * @param originalPoint
     * @return
     */
    private static IPoint validateAndTransformSingleGeneInArenaBounds(IPoint originalPoint) {
        IPoint transformedPoint = new Point(
                PathSolution.validateAndTransformSingleGeneInArenaBoundsX(originalPoint.getX()),
                PathSolution.validateAndTransformSingleGeneInArenaBoundsY(originalPoint.getY())
        );
        return transformedPoint;
    }

    /**
     * Checks a given gene is in the bounds of the arena, if not, transform it
     *
     * @param originalX
     * @return
     */
    private static int validateAndTransformSingleGeneInArenaBoundsX(int originalX) {
        //If originalX >= BorderMargin
        if (originalX >= conf.getBorderMargin()) {
            //If originalX <= Width-BorderMargin
            if (originalX <= conf.getWidth() - conf.getBorderMargin()) {
                //Then keep it and return it
                return originalX;
            } else {
                //If originalX > Width-Border Margin, then, return width-borderMargin (superiorLimit)
                return conf.getWidth() - conf.getBorderMargin();
            }
        } else {
            //If originalX < BorderMargin, then, return border margin (inferiorLimit)
            return conf.getBorderMargin();
        }
    }

    /**
     * Checks a given gene Y is in the bounds of the arena
     *
     * @param originalY
     * @return
     */
    private static int validateAndTransformSingleGeneInArenaBoundsY(int originalY) {
        //If originalY >= BorderMargin
        if (originalY >= conf.getBorderMargin()) {
            //If originalY <= Height-BorderMargin
            if (originalY <= conf.getHeight() - conf.getBorderMargin()) {
                //Then keep it and return it
                return originalY;
            } else {
                //If originalY > Height-Border Margin, then, return height-borderMargin (superiorLimit)
                return conf.getHeight() - conf.getBorderMargin();
            }
        } else {
            //If originalY < BorderMargin, then, return border margin (inferiorLimit)
            return conf.getBorderMargin();
        }
    }

    /**
     * Calculates and returns the distance between 2 points
     *
     * @param point1
     * @param point2
     * @return
     */
    private double calculateDistanceBetween2Points(IPoint point1, IPoint point2) {
        // Extract x and y coordinates of each point
        int x1 = point1.getX();
        int y1 = point1.getY();
        int x2 = point2.getX();
        int y2 = point2.getY();

        // Calculate differences in x and y coordinates
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        // Calculate distance using the distance formula
        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance;
    }

    /**
     * Use the weights of each section to calculate a weighted distance
     *
     * @param point1
     * @param point2
     * @return
     */
    private double calculateWeightedDistanceBetween2Points(IPoint point1, IPoint point2) {
        //Calculate the non weighted distance between the 2 points
        double distance = this.calculateDistanceBetween2Points(point1, point2);
        //Store section safety in local variable
        HashMap<Rectangle, Double> sectionRisk = conf.getSectionSafety();
        //If we don't have weights, then, return the non-weighted distance
        if (sectionRisk.isEmpty()) {
            return distance;
        }
        //If we do have weights, then we are gona calculate the average risk of the sections we pass by
        double totalRisk = 0;
        int numberOfSectionsCrossed = 0;

        //Sets a line between the 2 points
        Line2D.Double line = new Line2D.Double(
                new Point2D.Double(point1.getX(), point1.getY()),
                new Point2D.Double(point2.getX(), point2.getY())
        );

        //For each section (rectangle) in the hash map sectionRisk
        for (Rectangle section : sectionRisk.keySet()) {
            //Lets check if the line intersects the section
            if (section.intersectsLine(line)) {
                //If the line intersects the section, then, add up the risk of the section as well as section counter
                totalRisk += sectionRisk.get(section);
                numberOfSectionsCrossed++;
            }
        }

        //If the number of sections crossed is != 0 (the expected)
        if (numberOfSectionsCrossed != 0) {
            //Calculate the average section risk
            double averageSectionRisk = totalRisk / numberOfSectionsCrossed;
            //Multiply the distance for the risk (the bigger the risk, the bigger the distance) and return it
            return distance * averageSectionRisk;
        } else {
            //Else, return the non-weighted distance
            System.out.println("Something went wrong in the weighting of the distance, sections crossed == 0");
            return distance;
        }
    }

    /**
     * Calculates the path total distance in the current solution
     *
     * @return
     */
    private double calculatePathTotalDistance(boolean useWeightsInDistance) {
        double distance = 0;
        //For each 2 points (genes) in the current path (chromosome), calculates the distance between them
        for (int i = 0; i < chromosome.size() - 1; i++) {
            if (useWeightsInDistance) {
                distance += calculateWeightedDistanceBetween2Points(chromosome.get(i), chromosome.get(i + 1));
            } else {
                distance += calculateDistanceBetween2Points(chromosome.get(i), chromosome.get(i + 1));
            }
        }
        return distance;
    }


    /**
     * Calculates the fitness score of the solution
     *
     * @param
     */
    private void calculateFitnessScore() {
        //I want first a valid solution ... then, i want the shortest one
        double tempFitnessScore = -1;

        if (this.pathSolutionValidateFunction()) {
            tempFitnessScore = 10000 / this.calculatePathTotalDistance(pathConf.getUseWeightsInDistance());
            tempFitnessScore = tempFitnessScore / getChromosome().size();
        }

        //Apply fitness score
        this.fitnessScore = tempFitnessScore;
    }

    /**
     * Calculates the fitness value of each solution in a list
     *
     * @param
     */
    private static void calculateFitnessScores(List<PathSolution> solutionsList) {
        //For each solution in the solutionsList, it calculates the fitnessScore
        for (PathSolution solution : solutionsList)
            solution.calculateFitnessScore();
    }

    /**
     * Selects the X best solutions and returns a list with them
     *
     * @param
     * @return
     */
    private static List<PathSolution> selectionFunctionXBest(List<PathSolution> solutionsList, int xNumberOfBestSolutions) {
        //Creates a list to store the xBest solutions
        List<PathSolution> xBestSolutions = new ArrayList<>();
        //Sorts the solutionsList so that the best ones come first
        solutionsList.sort(Comparator.reverseOrder());
        //Adds the X first (and therefore best) solutions to the storage list
        for (int i = 0; i < xNumberOfBestSolutions; i++)
            xBestSolutions.add(solutionsList.get(i));
        //Returns array with X best solutions
        return xBestSolutions;
    }

    /**
     * Given an array with 2 initial solutions, returns an array with the 2 resulting solutions from crossing of the 2 initial ones
     *
     * @param
     * @return
     */
    private static PathSolution[] crossoverFunction(PathSolution[] initial2SolutionArray) {
        return null;
    }

    /**
     * Crossover the existing solutions into a total of X population
     * Elites are the Y number of solutions that we keep unchanged
     *
     * @param
     * @return
     */
    private static List<PathSolution> crossoverFunctionIntoXPopulation(
            List<PathSolution> solutionsList,
            int xPopulationSizeGoal,
            int yNumberOfEliteUnchangedSolutions) {
        return null;
    }

    /**
     * Based on a eventSuccessChance, returns true of false if the event succeeds or not
     *
     * @param
     * @return
     */
    private static boolean chanceEventRoulette(double eventSuccessChance) throws Exception {
        if (eventSuccessChance < 0 || eventSuccessChance > 1)
            throw new Exception("eventSuccessChance must be between 0 and 1");
        if (eventSuccessChance == 1)
            return true;
        //Makes random number between 0 (inclusive) and 101(exclusive)
        int randomNumber = (new Random()).nextInt(0, 101);
        //Gets eventSuccessChance in % form
        int eventSuccessChanceNumber = (int) (eventSuccessChance * 100);
        //if eventSuccessChanceNumber >= randomNumber it means that the event had success, otherwise it failed
        return (eventSuccessChanceNumber >= randomNumber);
    }


    /**
     * Mutates a gene
     *
     * @param
     * @return
     */
    private static IPoint mutateGene(
            IPoint originalGene,
            int minPixelGeneMutation,
            int maxPixelGeneMutation) throws Exception {
        Random rand = new Random();
        Point mutatedGene = new Point((Point) originalGene);
        //Decides if the X and Y mutation is going be positive or negative
        int xMutation = rand.nextBoolean() ? 1 : -1;
        int yMutation = rand.nextBoolean() ? 1 : -1;
        //For the X coordinates, adds a random mutation value (that can either be positive or negative)
        int mutatedX = mutatedGene.getX() + (rand.nextInt(minPixelGeneMutation, maxPixelGeneMutation) * xMutation);
        //Checks if the mutation doesnt break the bounds of the map
        if (mutatedX > 0 && mutatedX < conf.getWidth()) {
            mutatedGene.setX(mutatedX);
        } else {
            mutatedX = mutatedGene.getX() + (rand.nextInt(minPixelGeneMutation, maxPixelGeneMutation) * (xMutation * -1));
            mutatedGene.setX(mutatedX);
        }
        //For the Y coordinates, adds a random mutation value (that can either be positive or negative)
        int mutatedY = mutatedGene.getY() + (rand.nextInt(minPixelGeneMutation, maxPixelGeneMutation) * yMutation);
        //Checks if the mutation doesnt break the bounds of the map
        if (mutatedY > 0 && mutatedY < conf.getHeight()) {
            mutatedGene.setY(mutatedY);
        } else {
            mutatedY = mutatedGene.getY() + (rand.nextInt(minPixelGeneMutation, maxPixelGeneMutation) * (yMutation * -1));
            mutatedGene.setY(mutatedY);
        }

        //With all the mutations done, returns the mutated gene
        return mutatedGene;
    }


    /**
     * Mutates a chromossome
     *
     * @param originalChromosome
     * @return
     */
    private static List<IPoint> mutateChromosome(
            List<IPoint> originalChromosome,
            double chanceOfMutatedGene,
            int minPixelGeneMutation,
            int maxPixelGeneMutation) throws Exception {
        Random rand = new Random();
        //Creates a list to store the mutated genes
        List<IPoint> mutatedChromosome = new ArrayList<>();
        //Adds the first gene to the list (starting point doesnt change)
        mutatedChromosome.add(new Point((Point) originalChromosome.get(0)));
        //For each gene in the originalChromosome (that is not the first or last)
        for (int i = 1; i < originalChromosome.size() - 1; i++) {
            Point mutatedGene = new Point((Point) originalChromosome.get(i));
            //If given the chance of gene mutation, it mutates
            if (PathSolution.chanceEventRoulette(chanceOfMutatedGene)) {
                mutatedGene = (Point) PathSolution.mutateGene(mutatedGene, minPixelGeneMutation, maxPixelGeneMutation);
                //With the mutation done, stores the mutated gene in the mutated chromosome
                mutatedChromosome.add(mutatedGene);
            } else {
                mutatedChromosome.add(mutatedGene);
            }
        }
        //Adds last gene (ending point)
        mutatedChromosome.add(new Point((Point) originalChromosome.get(originalChromosome.size() - 1)));
        //With all the mutations done, returns the mutated chromosome
        return mutatedChromosome;
    }

    /**
     * Mutates a Solution
     *
     * @return
     */
    private void mutateSolution(double chanceOfGeneMutation, int minPixelGeneMutation, int maxPixelGeneMutation) throws Exception {
        //Mutates the current solution
        this.chromosome = PathSolution.mutateChromosome(chromosome, chanceOfGeneMutation, minPixelGeneMutation, maxPixelGeneMutation);
        this.calculateFitnessScore();
    }

    /**
     * Mutates the solutions in the array on a chance base except for X number of elites
     * Obs: if the xPopulationSizeGoal is different from the originalSolutionsList size, it will add new mutation variations
     * till the populationSizeGoal is achieved
     *
     * @param
     * @return
     */
    private static List<PathSolution> mutationFunctionIntoXPopulationChanceBased(
            List<PathSolution> originalSolutionsList,
            int xPopulationSizeGoal,
            int yNumberOfEliteUnchangedSolutions,
            double chanceOfSolutionMutation,
            double chanceOfGeneMutation,
            int minPixelGeneMutation,
            int maxPixelGeneMutation) throws Exception {
        Random rand = new Random();
        //Creates a list to store the mutated solutions
        List<PathSolution> mutatedSolutions = new ArrayList<>();
        //Adds the elites to the mutatedSolutions
        for (int i = 0; i < yNumberOfEliteUnchangedSolutions; i++)
            mutatedSolutions.add(new PathSolution(originalSolutionsList.get(i)));
        //For each non elite solution
        for (int i = yNumberOfEliteUnchangedSolutions; i < originalSolutionsList.size(); i++) {
            PathSolution mutatedSolution = new PathSolution(originalSolutionsList.get(i));
            //If given the chanceOfSolutionMutation, it mutates
            if (PathSolution.chanceEventRoulette(chanceOfSolutionMutation))
                mutatedSolution.mutateSolution(chanceOfGeneMutation, minPixelGeneMutation, maxPixelGeneMutation);
            //Adds the non elite solution to the list
            mutatedSolutions.add(mutatedSolution);
        }
        //While number of solutions is below the population goal
        for (int i = originalSolutionsList.size(); i < xPopulationSizeGoal; i++) {
            //adds new mutated versions
            PathSolution mutatedSolution = new PathSolution(originalSolutionsList.get(rand.nextInt(originalSolutionsList.size())));
            mutatedSolution.mutateSolution(chanceOfGeneMutation, minPixelGeneMutation, maxPixelGeneMutation);
            mutatedSolutions.add(mutatedSolution);
        }
        //Sorts the new list
        mutatedSolutions.sort(Comparator.reverseOrder());
        //Returns the list with the mutated solutions
        return mutatedSolutions;
    }

    /**
     * Reads the IPathSolution configs from the external file to the static variables
     *
     * @param
     * @return
     */
    public static void readPathConfigs(File fileObject) {
        //RobocodeFileOutputStream fw = new RobocodeFileOutputStream(fileObject.getAbsolutePath());
        //fw.write(new byte[10]);

        //Temp default values
        pathConf = new PathConfiguration(
                1,
                5,
                500,
                125,
                500,
                5,
                0.60,
                0.20,
                10,
                30,
                -1,
                -1,
                20,
                500,
                true,
                true
        );
    }

    /**
     * Reads the UIConfiguration (given by the Map or Robot)
     *
     * @param configs
     */
    public static void setUIConfigs(UIConfigurationArena configs) {
        conf = configs;
    }

    /**
     * Runs the evolution, finds and returns the best path
     *
     * @return
     */
    public static PathSolution runEvolution() throws Exception {

        if (conf.getSectionSafety().isEmpty()) {
            System.err.println("WARNING: No confWeights, using non-weighted distance");
        }

        //Generates X numberOfRandomSolutions (initial population)
        List<PathSolution> generatedSolutions = PathSolution.generateXRandomSolutions(pathConf.getNumberOfSolutionsPerGeneration());

        //While X, Selects, Mutates and repeat
        int currentGeneration = 0;
        long startTime = System.currentTimeMillis();
        long maxDuration = pathConf.getMaxRunEvolutionDuration();// 0.5 seconds in milliseconds

        while (currentGeneration < pathConf.getMaxNumberOfGenerations() && (System.currentTimeMillis() - startTime) <= maxDuration) {
            //Selects top solutions
            generatedSolutions = PathSolution.selectionFunctionXBest(generatedSolutions, pathConf.getXNumberOfBestSolutionsForSelectionFunction());
            //Mutates the solutions (not the elites)
            generatedSolutions = PathSolution.mutationFunctionIntoXPopulationChanceBased(
                    generatedSolutions,
                    pathConf.getXPopulationSizeGoalForMutationFunction(),
                    pathConf.getYNumberOfEliteNonMutatedSolutions(),
                    pathConf.getChanceOfSolutionMutation(),
                    pathConf.getChanceOfGeneMutation(),
                    pathConf.getMinPixelGeneMutation(),
                    pathConf.getMaxPixelGeneMutation());
            //Increase counters
            currentGeneration++;
        }

        System.out.println("CurrentGeneration: " + currentGeneration);
        //returns Best Solution //the solutions all get reverse sorted during the process
        PathSolution bestSolution = generatedSolutions.get(0);
        return bestSolution;
    }


    @Override
    public int compareTo(PathSolution o) {
        //If the fitness score of the current solution is biggeer than the one from 0 solution, return 1, otherwise return -1.
        if (this.fitnessScore == o.getFitnessScore()) {
            return 0;
        } else if (this.fitnessScore > o.getFitnessScore()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "PathSolution{}";
    }

}
