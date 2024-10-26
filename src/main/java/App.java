import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.prediction.AbstractPrediction;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import hex.genmodel.easy.prediction.RegressionModelPrediction;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");


        Long statingTime = System.currentTimeMillis();
        long duration = 500;

        System.out.println("StartingTime: " + statingTime);

        while(System.currentTimeMillis()-statingTime <= duration){
            //System.out.println("CurrentTime: " + System.currentTimeMillis());
        }

        System.out.println("EndTime: " + System.currentTimeMillis());

    }
}
