
package sampleRobots;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;
import impl.DadosSectionRisk;
import impl.Point;
import robocode.Bullet;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;

public class SectionRiskIntelligentRobot extends DiabeticBulletsAbstract {

    //List of variables for the SectionRiskInteligentRobot
    private EasyPredictModelWrapper localizationModel;

    private DadosSectionRisk sectionData;
    private double sectionGetHitMinimumRiskDifferenceToMove;
    private boolean moveToSectionCenter;

    /**
     * Method used to inicialize extra variables in the run by child classes
     */
    @Override
    protected void inicializeExtraVariablesInRun() {
        //Inicialize the variables for the SectionRiskInteligentRobot
        {
            //loadPredictiveModel
            this.loadLocalizationPredictiveModel("GBM_grid_1_AutoML_1_20240603_204925_model_35.zip");

            sectionData = new DadosSectionRisk(
                    this.getBattleFieldWidth(),
                    this.getBattleFieldHeight(),
                    5,
                    5
            );

            sectionGetHitMinimumRiskDifferenceToMove = 0.05;
            moveToSectionCenter = false;
        }
    }

    /**
     * Method used to do extraThings in child classes without having to overide onScanned
     */
    @Override
    protected void doExtraThingsInOnScanned(ScannedRobotEvent event, Bullet firedBullet, Double gunAngleDeviation, Point2D.Double enemyCoordinates) {
        //Registers the enemy in a section, as well as my data
        {
            //Registers the position of the enemy in the sections
            sectionData.registerEnemyPosition(event.getName(), enemyCoordinates.x, enemyCoordinates.y);

            //Registers myMomentaryData
            sectionData.setMomentaryData(
                    event.getTime(),
                    this.getName(),
                    this.getVelocity(),
                    this.getHeading(),
                    this.getX(),
                    this.getY()
            );
        }
    }

    /**
     * Method used to do extraThings in child classes without having to overide onRobotDeath
     */
    @Override
    protected void doExtraThingsInOnRobotDeath(RobotDeathEvent event) {
        //Removes the enemy from the sections
        sectionData.removeEnemy(event.getName());
    }


    /**
     * Loads the predictive model for the localization problem
     * @param modelFileName
     */
    private void loadLocalizationPredictiveModel(String modelFileName) {
        try {
            System.out.println("Reading model from folder: " + getDataDirectory());
            String modelPath = getDataDirectory() + "\\" + modelFileName;

            //Load the model from the bins to memory
            localizationModel = new EasyPredictModelWrapper(
                    MojoModel.load(modelPath)
            );
            System.out.println("Model successfully loaded: " + modelPath);
            System.out.println("Model name: " + modelFileName);
            System.out.println("Model category: " + localizationModel.getModelCategory());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the safest section in the map and return the center point of it
     *
     * @return
     */
    @Override
    protected Point findNewBestPointInMap() {
        //For each section, i wanna ask the model for a prediction and save it
        //Inicializes the variable to store the safety of each section
        HashMap<Rectangle, Double> sectionRisk = new HashMap<>();
        String safestSection = "";
        double safestSectionLowestRisk = 2.0;

        //Asks the model for a prediction for each section
        for (String section : sectionData.getSectionNames()) {
            double prediction = this.askModelIfIGetHitInASection(section);
            //Stores the safety of each section as a rectangle
            sectionRisk.put(sectionData.getSectionAsRectangle(section), prediction);

            //Check if it is the safest section till now
            if (prediction < safestSectionLowestRisk) {
                safestSectionLowestRisk = prediction;
                safestSection = section;
            }
        }
        //Stores the safety of each section in the UIConfig for the Genetic Algorithm
        conf.setSectionSafety(sectionRisk);

        //Check the safety of both my current section and the safest one
        String myCurrentSection = sectionData.getSectionName(this.getX(), this.getY());
        double myCurrentSectionLowestRisk = sectionRisk.get(sectionData.getSectionAsRectangle(myCurrentSection));

        //return decided section
        return this.decideBetweenCurrentSectionAndSafestSection(myCurrentSection, myCurrentSectionLowestRisk, safestSection, safestSectionLowestRisk);
    }

    /**
     * Decide if the difference risk is worth me moving
     *
     * @param myCurrentSection
     * @param myCurrentSectionRisk
     * @param safestSection
     * @param safestSectionRisk
     * @return
     */
    private Point decideBetweenCurrentSectionAndSafestSection(String myCurrentSection, double myCurrentSectionRisk, String safestSection, double safestSectionRisk) {
        //Generates a new point from the safest section and returns it
        System.out.println("MyCurrentSection: " + myCurrentSection + " WithLowestRisk: " + myCurrentSectionRisk);
        System.out.println("SafestSection: " + safestSection + " WithLowestRisk: " + safestSectionRisk);

        //Calculates the safety difference
        double riskDifference = myCurrentSectionRisk - safestSectionRisk;

        //If the difference is significant
        if (riskDifference >= this.sectionGetHitMinimumRiskDifferenceToMove) {
            //Then, i'm moving to the new section
            System.out.println("SectionRiskDifference: " + riskDifference + " MovingTo: " + safestSection);
            return sectionData.getSectionPoint(safestSection, this.moveToSectionCenter);
        } else {
            //I'm staying in my current section
            System.out.println("SectionRiskDifference: " + riskDifference + " StayingIn: " + myCurrentSection);
            return sectionData.getSectionPoint(myCurrentSection, this.moveToSectionCenter);
        }
    }

    /**
     * Ask model if i get hit in a given section
     *
     * @param mySectionName
     * @return
     */
    private double askModelIfIGetHitInASection(String mySectionName) {
        RowData row = new RowData();

        row.put("time", sectionData.getTime().doubleValue());

        //Add each section with the number of enemies (Order Is Important (I think))
        for (int i = 0; i<sectionData.getSectionNames().size(); i++) {
            String sectionName = sectionData.getSectionNames().get(i);
            row.put(sectionName, (double) sectionData.getNumberOfEnemiesInSection(sectionName));
        }
        //Adds my robot variables
        row.put("myVelocity", sectionData.getMyVelocity());
        row.put("myHeading", sectionData.getMyHeading());

        Point sectionPoint = sectionData.getSectionPoint(mySectionName, moveToSectionCenter);
        row.put("myX", (double) sectionPoint.getX());
        row.put("myY", (double) sectionPoint.getY());

        row.put("mySection", mySectionName);

        try {
            //Uses the row to predict if i get hit or not
            if ((localizationModel.getModelCategory().toString()).equals("Regression")) {
                RegressionModelPrediction p = localizationModel.predictRegression(row);
                //System.out.println("Will I get hit? -> " + p.value);
                //Return prediction
                return p.value;
            } else {
                BinomialModelPrediction p = localizationModel.predictBinomial(row);
                System.out.println("Will I get hit? -> " + p.label);

                //for (double classProbability : p.classProbabilities) {
                //    System.out.println("ClassProbability# :" + classProbability);
                //}
                //System.out.println("Probability of getting hit: " + p.classProbabilities[1]);
                //System.out.println("Probability of not getting hit: " + p.classProbabilities[0]);

                //Return probability of me getting hit
                return p.classProbabilities[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1.0;
    }


}

