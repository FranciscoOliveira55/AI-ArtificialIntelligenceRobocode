package sampleRobots;

import impl.*;
import impl.Point;
import interf.IPoint;
import robocode.*;
import robocode.Robot;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;

import static utils.Utils.getDistance;

abstract class DiabeticBulletsAbstract extends AdvancedRobot {

    //List of variables for the Generic Algorithm
    protected static UIConfigurationArena conf;

    //List of points for the movement of the Robot
    private List<IPoint> movementPath;
    private int currentPointIndex = -1;
    private int currentFollowingPathRunIteration;
    private int followingPathRunMaxIteration;

    //List of variables for the firing of the Robot
    protected double maxGunAngleDeviation;

    @Override
    public void run() {
        super.run();

        //Inicialize the variables for the Genetic Algorithm
        {
            //Reads Generic Algorithm Path Configs //Put in RUN
            //String fileName = "TestyJson" + ".json";
            //File fileObject = this.getDataFile(fileName);
            PathSolution.readPathConfigs(null);

            conf = new UIConfigurationArena(
                    (int) this.getBattleFieldWidth(),
                    (int) this.getBattleFieldHeight(),
                    3
            );
            currentFollowingPathRunIteration = 0;
            followingPathRunMaxIteration = 40;
        }

        //Inicialize the variables for the firing of the robot
        {
            //The GunAngleDeviation can go between -10 and +10
            this.maxGunAngleDeviation = 6;
        }

        //Inicialize extra variables in run
        this.inicializeExtraVariablesInRun();


        while (true) {

            //Keeps turning the radar
            this.setTurnRadarRight(360);
            //Follow the path
            {
                //If i'm moving somewhere
                if (currentPointIndex >= 0) {
                    this.followCurrentPath();   //Keep going till the end of the path
                } else {
                    System.out.println("FinishedPath || CurrentPathRunIteration: " + currentFollowingPathRunIteration);
                    //Find a new point in the map
                    Point newBestPointInMap = this.findNewBestPointInMap();
                    //And set a path to it
                    this.setBestGeneticPathToPoint(newBestPointInMap);
                    currentFollowingPathRunIteration = 0;
                }
            }

            //If the path is too long, find a new one
            {
                if (currentFollowingPathRunIteration != 0 && currentFollowingPathRunIteration % followingPathRunMaxIteration == 0) {
                    System.out.println("PathTooLong ||CurrentPathRunIteration: " + currentFollowingPathRunIteration);
                    //Find a new point in the map
                    Point newBestPointInMap = this.findNewBestPointInMap();
                    //And set a path to it
                    this.setBestGeneticPathToPoint(newBestPointInMap);
                    currentFollowingPathRunIteration = 0;
                }
            }

            this.currentFollowingPathRunIteration++;

            this.doExtraThingsInRunCycle();

            this.execute();
        }
    }
    /**
     * Method used to inicialize extra variables in the run by child classes
     */
    protected void inicializeExtraVariablesInRun() {

    }
    /**
     * Method used to do extraThings in child classes without having to overide run
     */
    protected void doExtraThingsInRunCycle() {

    }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        //Build a rectangle around the enemy and regist it in UIConf as an obstacle
        {
            //Get the coordinates of the enemy in a point
            Point2D.Double ponto = getEnemyCoordinates(this, event.getBearing(), event.getDistance());
            ponto.x -= this.getWidth() * 2.5 / 2;
            ponto.y -= this.getHeight() * 2.5 / 2;

            //Builds a rectangue around the enemy
            Rectangle rect = new Rectangle((int) ponto.x, (int) ponto.y, (int) (this.getWidth() * 2.5), (int) (this.getHeight() * 2.5));

            //Overide the enemy in the hash with his new position
            conf.enemiesAsObstacles.put(event.getName(), rect);
        }

        //Calculate enemy coordinates
        Point2D.Double enemyCoordinates = getEnemyCoordinates(this, event.getBearing(), event.getDistance());
        //System.out.println("Enemy " + event.getName() + " spotted at " + enemyCoordinates.x + "," + enemyCoordinates.y + "\n");

        //If the gun is cold, shoot the enemy
        Bullet firedBullet = null;
        Double gunAngleDeviation = 0.0;
        {
            if (this.getGunHeat() == 0) {
                //Stop moving if i'm about to shoot
                {
                    this.setAhead(0);
                    this.setTurnRight(0);
                    this.setTurnGunRight(0);
                }

                //Aim the gun at the enemy
                {            // Calculate the absolute bearing of the enemy (heading relative to north)
                    double absoluteBearing = this.getHeading() + event.getBearing();
                    // Normalize the absolute bearing to be within -180 to 180 degrees
                    double gunTurnAmount = robocode.util.Utils.normalRelativeAngleDegrees(absoluteBearing - this.getGunHeading());
                    // Turn the gun to the correct angle
                    turnGunRight(gunTurnAmount);
                }
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
                }

                //Give the gun a small angle deviation so that it's aimed to the future positon of the enemy (and not the current one)
                {
                    gunAngleDeviation = this.calculateGunAngleDeviation(firingData);
                    firingData.setGunAngleDeviation(gunAngleDeviation);
                    turnGunRight(gunAngleDeviation);
                }

                //Fires on the enemy
                firedBullet = fireBullet(3);
            }
        }

        this.doExtraThingsInOnScanned(event, firedBullet, gunAngleDeviation, enemyCoordinates);
    }

    /**
     * Method used to do extraThings in child classes without having to overide onScanned
     */
    protected void doExtraThingsInOnScanned(ScannedRobotEvent event, Bullet firedBullet, Double gunAngleDeviation, Point2D.Double enemyCoordinates) {

    }


    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);

        //Removes the enemy from the enemies rectangle hash
        conf.enemiesAsObstacles.remove(event.getName());

        this.doExtraThingsInOnRobotDeath(event);
    }
    /**
     * Method used to do extraThings in child classes without having to overide onRobotDeath
     */
    protected void doExtraThingsInOnRobotDeath(RobotDeathEvent event) {

    }


    @Override
    public void onHitWall(HitWallEvent event) {
        //Find other point in the map and make a new path till it
        this.setBestGeneticPathToPoint(
                this.findNewBestPointInMap()
        );
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        //Find other point in the map and make a new path till it
        this.setBestGeneticPathToPoint(
                this.findNewBestPointInMap()
        );
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);

        //Uses the genetic algorithm to set a path between the current point and the clicked point
        this.setBestGeneticPathToPoint(new Point(e.getX(), e.getY()));
    }


    /**
     * Follow current path, when i get to the end of the path, then replace currentPointIndex with -1
     */
    private void followCurrentPath() {
        //Gets current point
        IPoint currentPoint = movementPath.get(currentPointIndex);
        //If i'm very close to the point
        if (getDistance(this, currentPoint.getX(), currentPoint.getY()) < 2) {
            //I go to the next one
            currentPointIndex++;
            //If i'm in the end of the path, then, replace current point with -1
            if (currentPointIndex >= movementPath.size()) {
                currentPointIndex = -1;
            }
        }
        advancedRobotGoTo(this, currentPoint.getX(), currentPoint.getY());
    }

    /**
     * Find a new best point to be in the map (and find the best path to it)
     */
    protected Point findNewBestPointInMap() {
        Random rand = new Random();
        int newBestPointInMapX = rand.nextInt(conf.getBorderMargin(), conf.getWidth() - conf.getBorderMargin());
        int newBestPointInMapY = rand.nextInt(conf.getBorderMargin(), conf.getHeight() - conf.getBorderMargin());

        return new Point(newBestPointInMapX, newBestPointInMapY);
    }

    /**
     * Sets a new best genetic path to the given point
     */
    private void setBestGeneticPathToPoint(Point endPoint) {
        //Sets the start point to the current robot position
        conf.setStart(new Point((int) this.getX(), (int) this.getY()));
        //Sets the end point to the given coordinates
        conf.setEnd(endPoint);

        try {
            //Gives UIConfigs to the Generic Algorithm
            PathSolution.setUIConfigs(conf);

            //Finds the best pathSolution
            PathSolution bestSolution = PathSolution.runEvolution();
            //If the retured path, is valid
            if (bestSolution.getFitnessScore() != -1) {
                //Sets the chromosome (the path) as the new robot path
                movementPath = bestSolution.getChromosome();
                //Sets currentPoint index to 0
                currentPointIndex = 0;
            } else {
                System.out.println("Genetic path, couldnt find valid solution");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Decides what AngleDeviation to give the gun
     *
     * @param firingData
     * @return
     */
    protected Double calculateGunAngleDeviation(DadosFiringRobot firingData) {
        return new Random().nextDouble(-maxGunAngleDeviation, maxGunAngleDeviation + 1);
    }


    /**
     * ******** TODO: Necessário selecionar a opção Paint na consola do Robot *******
     *
     * @param g
     */
    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        g.setColor(Color.RED);
        conf.enemiesAsObstacles.values().stream().forEach(x -> g.drawRect(x.x, x.y, (int) x.getWidth(), (int) x.getHeight()));

        if (movementPath != null) {
            for (int i = 1; i < movementPath.size(); i++)
                drawThickLine(g, movementPath.get(i - 1).getX(), movementPath.get(i - 1).getY(), movementPath.get(i).getX(), movementPath.get(i).getY(), 2, Color.green);
        }
    }

    /**
     * Devolve as coordenadas de um alvo
     *
     * @param robot    o meu robot
     * @param bearing  ângulo para o alvo, em graus
     * @param distance distância ao alvo
     * @return coordenadas do alvo
     */
    public static Point2D.Double getEnemyCoordinates(Robot robot, double bearing, double distance) {
        double angle = Math.toRadians((robot.getHeading() + bearing) % 360);

        return new Point2D.Double((robot.getX() + Math.sin(angle) * distance), (robot.getY() + Math.cos(angle) * distance));
    }

    private void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {

        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;

        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (thickness) / (2 * lineLength);

        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }

    /**
     * Dirige o robot (AdvancedRobot) para determinadas coordenadas
     *
     * @param robot o meu robot
     * @param x     coordenada x do alvo
     * @param y     coordenada y do alvo
     */
    public static void advancedRobotGoTo(AdvancedRobot robot, double x, double y) {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.setTurnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.setAhead(distance);
        else
            robot.setBack(distance);
        robot.execute();
    }


}
