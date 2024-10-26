package sampleRobots;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;
import impl.DadosFiringRobot;

import java.io.IOException;

public class FiringIntelligentRobot extends DiabeticBulletsAbstract {

    //List of variables for the FiringInteligentRobot
    private EasyPredictModelWrapper firingModel;

    /**
     * Method used to inicialize extra variables in the run by child classes
     */
    @Override
    protected void inicializeExtraVariablesInRun() {
        //Inicialize the variables for the FiringInteligentRobot
        {
            //loadPredictiveModel
            this.loadFiringPredictiveModel("GBM_grid_1_AutoML_1_20240603_204907_model_23.zip");
        }
    }

    private void loadFiringPredictiveModel(String modelFileName) {
        try {
            System.out.println("Reading model from folder: " + getDataDirectory());
            String modelPath = getDataDirectory() + "\\" + modelFileName;

            //Load the model from the bins to memory
            firingModel = new EasyPredictModelWrapper(
                    MojoModel.load(modelPath)
            );
            System.out.println("Firing Model successfully loaded: " + modelPath);
            System.out.println("Firing Model name: " + modelFileName);
            System.out.println("Firing Model category: " + firingModel.getModelCategory());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Calculates the best gun angle deviation
     *
     * @return
     */
    @Override
    protected Double calculateGunAngleDeviation(DadosFiringRobot firingData) {
        //For the given situation, i wanna ask the model what is the best angle deviation in order to me to hit the enemy
        double bestAngleDeviation = 0;
        double bestAngleDeviationChance = 0;

        //For each angle variation, from -10 till +10
        for (double proposedAngle = -maxGunAngleDeviation; proposedAngle <= maxGunAngleDeviation; proposedAngle++) {
            //Ask the model for a prediction using this angle deviation
            double prediction = this.askModelIfIHitTheEnemyWithThisAngleDeviation(firingData, proposedAngle);

            //Check if it is the best prediction till now
            if (bestAngleDeviationChance < prediction) {
                bestAngleDeviation = proposedAngle;
                bestAngleDeviationChance = prediction;
            }
        }
        //In the end, returns the best angle
        //System.out.println("BestAngle: " + bestAngleDeviation);
        return bestAngleDeviation;
    }

    /**
     * Ask model if i hit the enemy with the given proposed angle deviation
     *
     * @param firingData
     * @param proposedAngle
     * @return
     */
    private double askModelIfIHitTheEnemyWithThisAngleDeviation(DadosFiringRobot firingData, double proposedAngle) {
        RowData row = new RowData();

        //Adds firing data variables
        row.put("time", firingData.getTime().doubleValue());
        //My enemy variables
        row.put("enemyVelocity", firingData.getEnemyVelocity());
        row.put("enemyHeading", firingData.getEnemyHeading());
        row.put("enemyX", firingData.getEnemyX());
        row.put("enemyY", firingData.getEnemyY());
        //My variables
        row.put("myVelocity", firingData.getMyVelocity());
        row.put("myHeading", firingData.getMyHeading());
        row.put("myGunHeading", firingData.getMyGunHeading());
        row.put("myX", firingData.getMyX());
        row.put("myY", firingData.getMyY());
        //Both robbots variables
        row.put("distance", firingData.getDistance());
        row.put("bearing", firingData.getBearing());
        //Proposed angle
        row.put("gunAngleDeviation", proposedAngle);

        try {
            //Uses the row to predict if  hit the enemy or not
            if ((firingModel.getModelCategory().toString()).equals("Regression")) {
                RegressionModelPrediction p = firingModel.predictRegression(row);
                //System.out.println("Will I hit the enemy? -> " + p.value);
                //Return prediction
                return p.value;
            } else {
                BinomialModelPrediction p = firingModel.predictBinomial(row);
                //System.out.println("Will I hit the enemy? -> " + p.label);

                //for (double classProbability : p.classProbabilities) {
                //    System.out.println("ClassProbability# :" + classProbability);
                //}
                //System.out.println("Probability of hitting the enemy: " + p.classProbabilities[1]);
                //System.out.println("Probability of not hitting the enemy: " + p.classProbabilities[0]);

                //Return probability of me hitting the enemy
                return p.classProbabilities[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1.0;
    }


}
