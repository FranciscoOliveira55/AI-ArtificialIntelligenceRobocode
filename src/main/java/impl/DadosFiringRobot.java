package impl;

/**
 * Classe usada para guardar os dados dos robots inimigos, quando sou atingido
 */
public class DadosFiringRobot {
    private Long time; //tempo atual

    private String enemyName; //nome do inimigo
    private Double enemyVelocity; //velocidade do inimigo
    private Double enemyHeading;//angulo do inimigo em relacao ao norte
    private Double enemyEnergy; //energia do inimigo
    private Double enemyX;
    private Double enemyY;

    private String myName;//nome do meu tank
    private Double myVelocity; //the velocity of my tank
    private Double myHeading; //meu angulo em relacao ao note
    private Double myEnergy;
    private Double myGunHeading; // angulo da minha arma em relacao ao norte
    private Double myX;
    private Double myY;

    private Double distance; //distancia entre mim e o inimigo
    private Double bearing; //angulo entre mim e o inimigo
    private Double gunAngleDeviation; //the deviation i give to the gun

    public DadosFiringRobot(Long time,
                            String enemyName,
                            Double enemyVelocity,
                            Double enemyHeading,
                            Double enemyEnergy,
                            Double enemyX,
                            Double enemyY,
                            String myName,
                            Double myVelocity,
                            Double myHeading,
                            Double myEnergy,
                            Double myGunHeading,
                            Double myX,
                            Double myY,
                            Double distance,
                            Double bearing
    ) {
        this.time = time;
        this.enemyName = enemyName;
        this.enemyVelocity = enemyVelocity;
        this.enemyHeading = enemyHeading;
        this.enemyEnergy = enemyEnergy;
        this.enemyX = enemyX;
        this.enemyY = enemyY;
        this.myName = myName;
        this.myVelocity = myVelocity;
        this.myHeading = myHeading;
        this.myEnergy = myEnergy;
        this.myGunHeading = myGunHeading;
        this.myX = myX;
        this.myY = myY;
        this.distance = distance;
        this.bearing = bearing;
    }

    public Double getMyVelocity() {
        return myVelocity;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public Double getEnemyVelocity() {
        return enemyVelocity;
    }

    public Double getEnemyHeading() {
        return enemyHeading;
    }

    public Double getEnemyEnergy() {
        return enemyEnergy;
    }

    public Double getEnemyX() {
        return enemyX;
    }

    public Double getEnemyY() {
        return enemyY;
    }

    public String getMyName() {
        return myName;
    }

    public Double getMyHeading() {
        return myHeading;
    }

    public Double getMyEnergy() {
        return myEnergy;
    }

    public Double getMyGunHeading() {
        return myGunHeading;
    }

    public Double getMyX() {
        return myX;
    }

    public Double getMyY() {
        return myY;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getBearing() {
        return bearing;
    }

    public Long getTime() {
        return time;
    }

    public Double getGunAngleDeviation() {
        return gunAngleDeviation;
    }

    public void setGunAngleDeviation(Double gunAngleDeviation) {
        this.gunAngleDeviation = gunAngleDeviation;
    }
}
