package sampleRobots;

import impl.DadosFiringRobot;
import robocode.*;

import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FiringWriterRobot extends DiabeticBulletsAbstract {

    //Variables for the Firing Writter robot
    private HashMap<Bullet, DadosFiringRobot> bulletsInAir;
    private static RobocodeFileOutputStream fw;


    /**
     * Method used to inicialize extra variables in the run by child classes
     */
    @Override
    protected void inicializeExtraVariablesInRun() {
        //Inicialize the variables for the FiringWritterRobot
        {
            bulletsInAir = new HashMap<>();
            this.openDatasetFile("FiringProblemDataset", 1, true);
        }
    }


    /**
     * Method used to do extraThings in child classes without having to overide onScanned
     */
    @Override
    protected void doExtraThingsInOnScanned(ScannedRobotEvent event, Bullet firedBullet, Double gunAngleDeviation, Point2D.Double enemyCoordinates) {

        //If i fired
        if (firedBullet != null) {

            //Stores the data of the firing in firingData
            DadosFiringRobot firingData;
            {
                firingData = new DadosFiringRobot(
                        event.getTime(),
                        event.getName(),
                        event.getVelocity(),
                        event.getHeading(),
                        event.getEnergy(),
                        enemyCoordinates.x,
                        enemyCoordinates.y,
                        this.getName(),
                        this.getVelocity(),
                        this.getHeading(),
                        this.getEnergy(),
                        this.getGunHeading(),
                        this.getX(),
                        this.getY(),
                        event.getDistance(),
                        event.getBearing()
                );
                firingData.setGunAngleDeviation(gunAngleDeviation);
            }

            //Stores the firingData and firedBullet in the hash
            bulletsInAir.put(firedBullet, firingData);
        }
    }


    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);

        //Checks if i hitted who i was supposed to, and write in the dataSet
        {
            //Gets bullet from the hash
            DadosFiringRobot firingData = bulletsInAir.get(event.getBullet());
            try {
                //Checks if i hitted who i was supposed to
                if (event.getName().equals(event.getBullet().getVictim())) {
                    //Writes the information
                    this.writeDatasetResult(firingData, true, fw);
                } else {
                    //Writes the information
                    this.writeDatasetResult(firingData, false, fw);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Removes bullet from the hash
            bulletsInAir.remove(event.getBullet());
        }
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        super.onBulletMissed(event);
        //Write it on dataSet
        {
            DadosFiringRobot firingData = bulletsInAir.get(event.getBullet());
            try {
                this.writeDatasetResult(firingData, false, fw);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bulletsInAir.remove(event.getBullet());
        }
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        super.onBulletHitBullet(event);
        //Write it on dataSet
        {
            DadosFiringRobot firingData = bulletsInAir.get(event.getBullet());
            try {
                this.writeDatasetResult(firingData, false, fw);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bulletsInAir.remove(event.getBullet());
        }
    }


    @Override
    public void onDeath(DeathEvent event) {
        super.onDeath(event);

        try {
            if (fw != null){
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
            if (fw != null){
                System.out.println("RoundEnded # ClosingFile");
                fw.close();
                fw = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
                this.writeDatasetHeaders(fw);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDatasetHeaders(RobocodeFileOutputStream fw) {
        System.out.println("Writting Headers");


        //Builds the line to write in the file
        StringBuilder sb = new StringBuilder();

        sb.append("time").append(",");

        sb.append("enemyName").append(",");
        sb.append("enemyVelocity").append(",");
        sb.append("enemyHeading").append(",");
        sb.append("enemyEnergy").append(",");
        sb.append("enemyX").append(",");
        sb.append("enemyY").append(",");

        sb.append("myName").append(",");
        sb.append("myVelocity").append(",");
        sb.append("myHeading").append(",");
        sb.append("myEnergy").append(",");
        sb.append("myGunHeading").append(",");
        sb.append("myX").append(",");
        sb.append("myY").append(",");

        sb.append("distance").append(",");
        sb.append("bearing").append(",");
        sb.append("gunAngleDeviation").append(",");

        sb.append("hittedEnemy").append("\n");

        try {
            //Writes in the file
            System.out.println(sb);
            fw.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes result in csv file
     *
     * @param firingData
     * @param hittedEnemy
     */
    private static void writeDatasetResult(DadosFiringRobot firingData, boolean hittedEnemy, RobocodeFileOutputStream fw) {
        //Builds the line to write in the file
        StringBuilder sb = new StringBuilder();

        sb.append(firingData.getTime()).append(",");

        sb.append(firingData.getEnemyName()).append(",");
        sb.append(firingData.getEnemyVelocity()).append(",");
        sb.append(firingData.getEnemyHeading()).append(",");
        sb.append(firingData.getEnemyEnergy()).append(",");
        sb.append(firingData.getEnemyX()).append(",");
        sb.append(firingData.getEnemyY()).append(",");

        sb.append(firingData.getMyName()).append(",");
        sb.append(firingData.getMyVelocity()).append(",");
        sb.append(firingData.getMyHeading()).append(",");
        sb.append(firingData.getMyEnergy()).append(",");
        sb.append(firingData.getMyGunHeading()).append(",");
        sb.append(firingData.getMyX()).append(",");
        sb.append(firingData.getMyY()).append(",");

        sb.append(firingData.getDistance()).append(",");
        sb.append(firingData.getBearing()).append(",");
        sb.append(firingData.getGunAngleDeviation()).append(",");

        if (hittedEnemy){
            sb.append(1).append("\n");
        }
        else{
            sb.append(0).append("\n");
        }

        try {
            //Writes in the file
            if (fw != null){
                fw.write(sb.toString().getBytes());
            }else{
                System.err.println("WARNING: File is closed, can't write to it");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
