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

package it.unicam.quasylab.sibilla.core.models.quasylab.sibilla.core.models.agents;

public interface AgentModelBuilder<W extends World> {

    W getWorld();

    void initialiseWorld();

    int getNumberOfAgents();

    SystemEnvironment<W> getEnvironment();

    OmegaFunction getOmegaFunction(int i);

    AgentBehaviour getAgentBehaviour(int i);

    VariableMapping getAgentState(W world, int i);

    VariableMapping getAgentInfo(W world, int i);


    default AgentModel<W> getAgentModel() {
        int n = getNumberOfAgents();
        AgentBehaviour[] agents = new AgentBehaviour[n];
        OmegaFunction[] omegaFunctions = new OmegaFunction[n];
        for(int i=0 ; i<n; i++) {
            agents[i] = getAgentBehaviour(i);
            omegaFunctions[i] = getOmegaFunction(i);
        }
        SystemEnvironment<W> environment = getEnvironment();
        return new AgentModel<>(agents,omegaFunctions,environment);
    }

    default SystemState<W> getState() {
        W world = getWorld();
        return getState(world);
    }

    default SystemState<W> getState(W world) {
        int n = getNumberOfAgents();
        VariableMapping[] agentInfo = new VariableMapping[n];
        VariableMapping[] localState = new VariableMapping[n];
        for(int i=0 ; i<n ; i++) {
            agentInfo[i] = getAgentInfo(world, i);
            localState[i] = getAgentState(world, i);
        }
        return new SystemState<>(world,agentInfo,localState);
    }

}
