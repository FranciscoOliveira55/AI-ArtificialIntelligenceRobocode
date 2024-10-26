# AI-ArtificialIntelligenceRobocode

This program implements several robots for use in Robocode:

  - **FiringWriterRobot**: Generates the dataset used to train the AI model for deciding the direction of the gun tower.
  - **SectionRiskWriterRobot**: Generates the dataset for training the AI model that determines where the robot should move.
  - **FiringIntelligentRobot**: Utilizes the AI model to decide the direction of the gun tower.
  - **SectionRiskIntelligentRobot**: Uses the AI model to determine where the robot should move.
  - **PathSolution**: Implements a genetic algorithm to find the safest path from point A to B.
  - **DiabeticBullets**: Generates the final intelligent robot that integrates all the above algorithms.

## Usage

Simply run the `App.java` file (or build the project).  
The generated robots will be in the `target/bins` folder.
