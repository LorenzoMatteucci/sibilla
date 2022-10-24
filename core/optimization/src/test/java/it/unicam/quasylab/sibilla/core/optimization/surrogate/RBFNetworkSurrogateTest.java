package it.unicam.quasylab.sibilla.core.optimization.surrogate;

import it.unicam.quasylab.sibilla.core.optimization.sampling.HyperRectangle;
import it.unicam.quasylab.sibilla.core.optimization.sampling.Interval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RBFNetworkSurrogateTest {

    @Test
    void testNet(){

        //sin(5x)*cos(5y)/5
        Function<Map<String,Double>,Double> funToOpt = map -> Math.sin(5*map.get("x")) *Math.cos(5*map.get("y"))/5;

        HyperRectangle searchSpace = new HyperRectangle(
                new Interval("x",-2.0,2.0),
                new Interval("y",-2.0,2.0)
        );

        TrainingSet ts = new TrainingSet(searchSpace,"lhs",10000,funToOpt);

        RBFNetworkSurrogate rbfNetworkSurrogate = new RBFNetworkSurrogate();

        rbfNetworkSurrogate.fit(ts);

        Map<String,Double> anInput = new HashMap<>();
        anInput.put("x",1.0);
        anInput.put("y",1.0);

        double res = rbfNetworkSurrogate.predict(new Double[]{1.0,1.0});

        System.out.println(res);
        System.out.println(funToOpt.apply(anInput));
        System.out.println(rbfNetworkSurrogate.getInSampleMetrics());
    }

}