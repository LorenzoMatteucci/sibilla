/*
 * Sibilla:  a Java framework designed to support analysis of Collective
 * Adaptive Systems.
 *
 * Copyright (C) 2020.
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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

/**
 * 
 */
package it.unicam.quasylab.sibilla.examples.pm.le;

import it.unicam.quasylab.sibilla.core.models.pm.PopulationModel;
import it.unicam.quasylab.sibilla.core.models.pm.PopulationState;
import it.unicam.quasylab.sibilla.core.simulator.SimulationEnvironment;
import it.unicam.quasylab.sibilla.core.simulator.sampling.SamplingFunction;

import java.io.FileNotFoundException;


/**
 * @author loreti
 *
 */
public class Main {

	
	public final static int SAMPLINGS = 100;
	public final static double DEADLINE = 600;
	public static final int REPLICA = 1000;
	public final static int TASKS = 5;
	
	
	public static void main(String[] argv) throws FileNotFoundException, InterruptedException {
		LeaderElectionDefinition def = new LeaderElectionDefinition();
		SimulationEnvironment simulator = new SimulationEnvironment();
		PopulationModel model = def.createModel();
		SamplingFunction<PopulationState> collection = model.getSamplingFunction(SAMPLINGS,DEADLINE/SAMPLINGS);
		simulator.simulate(model,def.state(),collection,REPLICA,DEADLINE);
		collection.printTimeSeries("data","le_",".data");
	}
	

}
