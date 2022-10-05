package it.unicam.quasylab.sibilla.core.optimization.sampling;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Full Factorial Sampling
 *
 * @author      Lorenzo Matteucci
 */
@SuppressWarnings({"UnusedDeclaration"})
class FullFactorialSamplingTest {
    @Test
    void testNumberOfSamplesOverContinuousInterval() {
        HyperRectangle hr = new HyperRectangle(
                new Interval(1.0,10.0),
                new Interval(1.0,10.0),
                new Interval(1.0,10.0),
                new Interval(1.0,10.0)
        );
        int numberOfSamplesPerDimension = 3;
        Table sampleSet = new FullFactorialSampling().getSampleTable(3,hr);
        assertEquals(
                sampleSet.rowCount(),
                Math.pow(numberOfSamplesPerDimension, hr.getDimensionality())
        );
    }

    @Test
    void testNumberOfSamplesOverMixedInterval() {
        HyperRectangle hrMixed = new HyperRectangle(
                new Interval("v1",1,25,false),
                new Interval("v2",-40,65),
                new Interval("v3",-130,200),
                new Interval("v4",-1,1,false)
        );
        HyperRectangle hrAllContinuous = new HyperRectangle(
                new Interval("v1",1,25),
                new Interval("v2",-40,65),
                new Interval("v3",-130,200),
                new Interval("v4",-1,1)
        );
        int numberOfSamplesPerDimension = 3;
        Table sampleSetMix = new FullFactorialSampling().getSampleTable(3,hrMixed);
        Table sampleSetCont = new FullFactorialSampling().getSampleTable(3,hrAllContinuous);
        assertTrue(sampleSetCont.rowCount() > sampleSetMix.rowCount());
    }

}