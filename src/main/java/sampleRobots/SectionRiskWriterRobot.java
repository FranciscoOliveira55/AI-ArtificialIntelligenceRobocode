package sampleRobots;

import impl.*;
import impl.Point;
import robocode.*;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

public class SectionRiskWriterRobot extends DiabeticBulletsAbstract {


    //List of variables for the SectionRiskWritterRobot Model
    protected DadosSectionRisk sectionData;

    private static RobocodeFileOutputStream fw; //problems closing the streams ... fix later
    private int currentWritteSectionRiskRunIteration;
    private int writteSectionRiskRunMaxIteration;


    /**
     * Method used to inicialize extra variables in the run by child classes
     */
    @Override
    protected void inicializeExtraVariablesInRun() {
        //Inicialize the variables for the SectionRiskWritterRobot Model
        {
            sectionData = new DadosSectionRisk(
                    this.getBattleFieldWidth(),
                    this.getBattleFieldHeight(),
                    5,
                    5
            );
            this.openDatasetFile("LocalizationProblemDataset", 1, true);
            currentWritteSectionRiskRunIteration = 0;
            writteSectionRiskRunMaxIteration = 10;
        }
    }

    /**
     * Method used to do extraThings in child classes without having to overide run
     */
    @Override
    protected void doExtraThingsInRunCycle() {
        //Write to section dataframe
        {
            //Every 10 iterations, write in file (was not hit) //would be better if it was time bounded
            if (currentWritteSectionRiskRunIteration != 0 && currentWritteSectionRiskRunIteration % writteSectionRiskRunMaxIteration == 0) {
                this.writeDatasetResult(sectionData, false, fw);
            }
        }
        this.currentWritteSectionRiskRunIteration++;
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


    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        //I want to write the current position of me and my enemies to file when i'm hit
        this.writeDatasetResult(sectionData, true, fw);
    }


    @Override
    public void onDeath(DeathEvent event) {
        super.onDeath(event);

        try {
            if (fw != null) {
                System.out.println("RobotDied # ClosingFile");
                fw.close();
                fw = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        super.onRoundEnded(event);

        try {
            if (fw != null) {
                System.out.println("RoundEnded # ClosingFile");
                fw.close();
                fw = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Opens the fw file
     *
     * @param fileName
     * @param numberOfFile
     * @param append
     */
    private void openDatasetFile(String fileName, int numberOfFile, boolean append) {
        try {
            if (fw != null) {
                System.out.println("Closing Previous File");
                fw.close();
            }

            String fullFileName = fileName + numberOfFile + ".csv";
            File fileObject = this.getDataFile(fullFileName);
            fw = new RobocodeFileOutputStream(fileObject.getAbsolutePath(), append);
            System.out.println("Writing to: " + fw.getName());

            //If file is doesn't exist or is empty write headers
            if (!fileObject.exists() || fileObject.length() == 0) {
                this.writeDatasetHeaders(sectionData, fw);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes headesrs in csv file
     *
     * @param dados
     * @param fw
     */
    public static void writeDatasetHeaders(DadosSectionRisk dados, RobocodeFileOutputStream fw) {
        System.out.println("Writting Headers");

        //Builds the line to write in the file
        StringBuilder sb = new StringBuilder();

        sb.append("time").append(",");

        //Adds the name of each section
        for (String sectionName : dados.getSectionNames()) {
            sb.append(sectionName).append(",");
        }

        sb.append("myName").append(",");
        sb.append("myVelocity").append(",");
        sb.append("myHeading").append(",");
        sb.append("myX").append(",");
        sb.append("myY").append(",");
        sb.append("mySection").append(",");

        sb.append("averageEnemyDistance").append(","); //averageDistance

        sb.append("gotHit").append("\n");

        try {
            //Writes in the file
            fw.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes result in csv file
     *
     * @param sectionData
     * @param gotHit
     */
    public static void writeDatasetResult(DadosSectionRisk sectionData, boolean gotHit, RobocodeFileOutputStream fw) {
        //Builds the line to write in the file
        StringBuilder sb = new StringBuilder();

        sb.append(sectionData.getTime()).append(",");

        //Adds the number of enemies in each section
        for (String sectionName : sectionData.getSectionNames()) {
            sb.append(sectionData.getNumberOfEnemiesInSection(sectionName)).append(",");
        }

        sb.append(sectionData.getMyName()).append(",");
        sb.append(sectionData.getMyVelocity()).append(",");
        sb.append(sectionData.getMyHeading()).append(",");
        sb.append(sectionData.getMyX()).append(",");
        sb.append(sectionData.getMyY()).append(",");

        sb.append(sectionData.getSectionName(sectionData.getMyX(), sectionData.getMyY())).append(","); //get my sectionName

        sb.append(
                sectionData.getAverageEnemyDistance(
                        new Point(
                                sectionData.getMyX().intValue(),
                                sectionData.getMyY().intValue()
                        )
                )
        ).append(","); //getAverageEnemyDistance

        if (gotHit)
            sb.append(1).append("\n");
        else
            sb.append(0).append("\n");

        try {
            //Writes in the file
            if (fw != null) {
                fw.write(sb.toString().getBytes());
            } else {
                System.err.println("WARNING: File is closed, can't write to it");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
