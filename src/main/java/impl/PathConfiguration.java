package impl;

import java.io.Serializable;

public class PathConfiguration implements Serializable {

    private int minNumberOfPointsPerPath;
    private int maxNumberOfPointsPerPath;
    private int numberOfSolutionsPerGeneration;
    private int xNumberOfBestSolutionsForSelectionFunction;

    private int xPopulationSizeGoalForMutationFunction;
    private int yNumberOfEliteNonMutatedSolutions;
    private double chanceOfSolutionMutation;
    private double chanceOfGeneMutation;
    private int minPixelGeneMutation;
    private int maxPixelGeneMutation;

    private int xPopulationSizeGoalForCrossoverFunction;
    private int yNumberOfEliteNonCrossedSolutions;

    private int maxNumberOfGenerations;
    private long maxRunEvolutionDuration;

    private boolean useWeightsInDistance;
    private boolean useStrickArenaBoundsValidation;



    public PathConfiguration(int minNumberOfPointsPerPath,
                             int maxNumberOfPointsPerPath,
                             int numberOfSolutionsPerGeneration,
                             int xNumberOfBestSolutionsForSelectionFunction,
                             int xPopulationSizeGoalForMutationFunction,
                             int yNumberOfEliteNonMutatedSolutions,
                             double chanceOfSolutionMutation,
                             double chanceOfGeneMutation,
                             int minPixelGeneMutation,
                             int maxPixelGeneMutation,
                             int xPopulationSizeGoalForCrossoverFunction,
                             int yNumberOfEliteNonCrossedSolutions,
                             int maxNumberOfGenerations,
                             long maxRunEvolutionDuration,
                             boolean useWeightsInDistance,
                             boolean useStrickArenaBoundsValidation) {
        this.minNumberOfPointsPerPath = minNumberOfPointsPerPath;
        this.maxNumberOfPointsPerPath = maxNumberOfPointsPerPath;
        this.numberOfSolutionsPerGeneration = numberOfSolutionsPerGeneration;
        this.xNumberOfBestSolutionsForSelectionFunction = xNumberOfBestSolutionsForSelectionFunction;
        this.xPopulationSizeGoalForMutationFunction = xPopulationSizeGoalForMutationFunction;
        this.yNumberOfEliteNonMutatedSolutions = yNumberOfEliteNonMutatedSolutions;
        this.chanceOfSolutionMutation = chanceOfSolutionMutation;
        this.chanceOfGeneMutation = chanceOfGeneMutation;
        this.minPixelGeneMutation = minPixelGeneMutation;
        this.maxPixelGeneMutation = maxPixelGeneMutation;
        this.xPopulationSizeGoalForCrossoverFunction = xPopulationSizeGoalForCrossoverFunction;
        this.yNumberOfEliteNonCrossedSolutions = yNumberOfEliteNonCrossedSolutions;
        this.maxNumberOfGenerations = maxNumberOfGenerations;
        this.maxRunEvolutionDuration = maxRunEvolutionDuration;
        this.useWeightsInDistance = useWeightsInDistance;
        this.useStrickArenaBoundsValidation = useStrickArenaBoundsValidation;
    }

    public int getMinNumberOfPointsPerPath() {
        return minNumberOfPointsPerPath;
    }

    public int getMaxNumberOfPointsPerPath() {
        return maxNumberOfPointsPerPath;
    }

    public int getNumberOfSolutionsPerGeneration() {
        return numberOfSolutionsPerGeneration;
    }

    public int getXNumberOfBestSolutionsForSelectionFunction() {
        return xNumberOfBestSolutionsForSelectionFunction;
    }

    public int getXPopulationSizeGoalForMutationFunction() {
        return xPopulationSizeGoalForMutationFunction;
    }

    public int getYNumberOfEliteNonMutatedSolutions() {
        return yNumberOfEliteNonMutatedSolutions;
    }

    public double getChanceOfSolutionMutation() {
        return chanceOfSolutionMutation;
    }

    public double getChanceOfGeneMutation() {
        return chanceOfGeneMutation;
    }

    public int getMinPixelGeneMutation() {
        return minPixelGeneMutation;
    }

    public int getMaxPixelGeneMutation() {
        return maxPixelGeneMutation;
    }

    public int getXPopulationSizeGoalForCrossoverFunction() {
        return xPopulationSizeGoalForCrossoverFunction;
    }

    public int getYNumberOfEliteNonCrossedSolutions() {
        return yNumberOfEliteNonCrossedSolutions;
    }

    public int getMaxNumberOfGenerations() {
        return maxNumberOfGenerations;
    }

    public long getMaxRunEvolutionDuration() {
        return maxRunEvolutionDuration;
    }

    public boolean getUseWeightsInDistance() {
        return useWeightsInDistance;
    }

    public boolean getUseStrickArenaBoundsValidation() {
        return useStrickArenaBoundsValidation;
    }
}
