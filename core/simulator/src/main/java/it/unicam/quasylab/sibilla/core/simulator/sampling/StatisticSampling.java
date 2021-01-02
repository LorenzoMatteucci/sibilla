/*
 * Sibilla:  a Java framework designed to support analysis of Collective
 * Adaptive Systems.
 *
 *             Copyright (C) 2020.
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.unicam.quasylab.sibilla.core.simulator.sampling;

import it.unicam.quasylab.sibilla.core.models.MeasureFunction;
import it.unicam.quasylab.sibilla.core.models.State;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.function.Function;


/**
 * @author loreti
 *
 */
public class StatisticSampling<S extends State> implements SamplingFunction<S> {

	private SummaryStatistics[] data;
	private Measure<S> measure;
	private double last_measure;
	private double dt;
	private double next_time;
	private int current_index;
	private double new_measure;

	public StatisticSampling(int samples, double dt, Measure<S> measure) {
		this.data = new SummaryStatistics[samples];
		this.measure = measure;
		this.dt = dt;
		init();
	}

	private void init() {
		for (int i = 0; i < data.length; i++) {
			data[i] = new SummaryStatistics();
		}
	}

	@Override
	public void sample(double time, S context) {
		this.new_measure = measure.measure(context);
		if ((time >= this.next_time) && (this.current_index < this.data.length)) {
			recordMeasure(time);
		} else {
			this.last_measure = this.new_measure;
		}
	}

	private void recordMeasure(double time) {
		while ((this.next_time<time)&&(this.current_index<this.data.length)) {
			this.recordSample();
		} 
		this.last_measure = this.new_measure;		
		if (this.next_time == time) {
			this.recordSample();
		}
	}
	
	private void recordSample() {
		this.data[this.current_index].addValue(this.last_measure);
		this.current_index++;
		this.next_time += this.dt;
	}
	

	@Override
	public void end(double time) {
		while (this.current_index < this.data.length) {
			this.data[this.current_index].addValue(this.last_measure);
			this.current_index++;
			this.next_time += this.dt;
		}
	}

	@Override
	public void start() {
		this.current_index = 0;
		this.next_time = 0;
	}
	
	public String getName() {
		return measure.getName();
	}

	@Override
	public void printTimeSeries(Function<String, String> nameFunction) throws FileNotFoundException {
		printTimeSeries(nameFunction,';');
	}

	@Override
	public void printTimeSeries(Function<String, String> nameFunction, char separator) throws FileNotFoundException {
		printTimeSeries(nameFunction,separator,0.05);
	}

	@Override
	public void printTimeSeries(Function<String, String> nameFunction, char separator, double significance) throws FileNotFoundException {
		String fileName = nameFunction.apply(this.getName());
		PrintStream out = new PrintStream(fileName);
		double time = 0.0;
		for (int i = 0; i < this.data.length; i++) {
			double ci = getConfidenceInterval(i,significance);
			out.println(""+time + separator 
					+ this.data[i].getMean() 
					+ separator + ci);
			time += dt;
		}
		out.close();
	}
	
	
	private double getConfidenceInterval(int i, double significance) {
		TDistribution tDist = new TDistribution(this.data[i].getN());
		double a = tDist.inverseCumulativeProbability(1.0 -significance/2);
		return a*this.data[i].getStandardDeviation() / Math.sqrt(this.data[i].getN());
	}

	@Override
	public LinkedList<SimulationTimeSeries> getSimulationTimeSeries( int replications ) {
		SimulationTimeSeries stt = new SimulationTimeSeries(measure.getName(), dt, replications, data);
		LinkedList<SimulationTimeSeries> toReturn = new LinkedList<>();
		toReturn.add(stt);
		return toReturn;
	}

	public int getSize() {
		return data.length;
	}
	
	public static <S extends State> StatisticSampling<S> measure( String name, int samplings, double deadline, MeasureFunction<S> m) {
		return new StatisticSampling<S>(samplings, deadline/samplings, 
				new Measure<S>() {

			@Override
			public double measure(S t) {
				return m.apply( t );
			}

			@Override
			public String getName() {
				return name;
			}

		});
		
	}

}