package maps;

import impl.PathSolution;
import impl.UIConfigurationArena;
import interf.IUIConfiguration;
import viewer.PathViewer;
import impl.Point;
import interf.IPoint;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Exemplo que mostra como desenhar um caminho no visualizador.
 */
public class PathDrawingSample
{
    public static IUIConfiguration conf;

    public static void main(String args[]) throws InterruptedException, Exception {

        //Test genetic algorithm to find path
        //Reads UIConfigs and PathContigs
        conf = Maps.getMap(1);
        PathSolution.readPathConfigs(null);
        PathSolution.setUIConfigs((UIConfigurationArena) conf);
        PathSolution bestSolution = PathSolution.runEvolution();
        DisplayPathViewer(bestSolution, conf);


        //TeacherExample();
    }


    /**
     * Displays Given Solution on PathViewer
     */
    private static void DisplayPathViewer(PathSolution bestSolution, IUIConfiguration conf){
        PathViewer pv = new PathViewer(conf);
        pv.setFitness(bestSolution.getFitnessScore()); //TODO: substituir pelo valor de fitness (opcional)
        pv.setStringPath(bestSolution.toString()); //TODO: substituir pelo caminho (opcional)
        pv.paintPath(bestSolution.getChromosome());
    }

    public static void TeacherExample() throws InterruptedException, Exception {
        Random rand = new Random();
        //O ID do mapa a usar (ver Maps.java)
        int map_id = 1;

        conf = Maps.getMap(map_id);
        //Solution is a List of IPoint
        List<IPoint> solution = new ArrayList<>();

        //TODO:Substituir solução manual pelo algoritmo genético
        solution.add(conf.getStart());
        int size = rand.nextInt(5); //cria um caminho aleatório com no máximo 5 nós intermédios (excetuando início e fim)
        for (int i=0;i<size;i++)
            solution.add(new Point(rand.nextInt(conf.getWidth()),rand.nextInt(conf.getHeight())));
        solution.add(conf.getEnd());    

        //exemplo de determinar se a solução é válida ou não
        //pode ser útil para o algoritmo genético...
        int conta = 0;
        //para cada segmento do caminho
        for (int i=0;i<solution.size()-1;i++){
            Point2D.Double p1 = new Point2D.Double(solution.get(i).getX(), solution.get(i).getY()); 
            Point2D.Double p2 = new Point2D.Double(solution.get(i+1).getX(), solution.get(i+1).getY()); 
            Line2D.Double line = new Line2D.Double(p1, p2);

            //para cada obstáculo no caminho
            for (int j=0;j<conf.getObstacles().size();j++)
                if (conf.getObstacles().get(j).intersectsLine(line))
                    conta++;
        }
        if(conta == 0)
            System.out.println("Solução válida!");
        else
            System.out.println("Solução inválida: o caminho interseta obstáculos " +conta+ " vezes!");
        
        //Mecanismo de visualização pode ser usado durante o algoritmo genético
        //para ver evolução ao longo do tempo

        //Shows Best Solution
        PathViewer pv = new PathViewer(conf);
        pv.setFitness(999999); //TODO: substituir pelo valor de fitness (opcional)
        pv.setStringPath("lalalalala"); //TODO: substituir pelo caminho (opcional)
        pv.paintPath(solution);

    }
}
