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

package it.unicam.quasylab.sibilla.core.models.yoda;

import it.unicam.quasylab.sibilla.core.simulator.util.WeightedStructure;
import org.apache.commons.math3.random.RandomGenerator;

/**
 * The class <code>YodaAgent</code> represents
 * the agents available in the simulation
 * Each one has the following components:
 * <ul>
 *     <li>a unique identifier</li>
 *     <li>a unique name string</li>
 *     <li>a local state containing the information known to the agent</li>
 *     <li>a external state containing the information unknown to the agent</li>
 *     <li>a set of observations done by the agent</li>
 *     <li>a behaviour that select the actions</li>
 *     <li>a OmegaFunction to determine the observations</li>
 *     <li>a AgentInfoUpdateFunction to update the external state of the agent</li>
 * </ul>
 *
 */
public final class YodaAgent {

    private final int identifier;
    private final String name;
    private YodaVariableMapping agentKnowledge;
    private YodaVariableMapping agentInformation;
    private YodaVariableMapping agentObservations;
    private final YodaBehaviour agentBehaviour;
    private final OmegaFunction omegaFunction;
    private final AgentInfoUpdateFunction agentInfoUpdateFunction;

    public YodaAgent(int identifier,
                     String name,
                     YodaVariableMapping agentKnowledge,
                     YodaVariableMapping agentInformation,
                     YodaVariableMapping agentObservations,
                     YodaBehaviour agentBehaviour,
                     OmegaFunction omegaFunction,
                     AgentInfoUpdateFunction agentInfoUpdateFunction) {
        this.identifier = identifier;
        this.name = name;
        this.agentKnowledge = agentKnowledge;
        this.agentInformation = agentInformation;
        this.agentObservations = agentObservations;
        this.agentBehaviour = agentBehaviour;
        this.omegaFunction = omegaFunction;
        this.agentInfoUpdateFunction = agentInfoUpdateFunction;
    }

    /**
     * This method returns the identifier of the agent
     *
     * @return the identifier of the agent
     */
    public int getIdentifier() {
        return identifier;
    }

    /**
     * This method returns the name of the agent
     *
     * @return the name of the agent
     */
    public String getName() {
        return name;
    }

    /**
     * This method returns the agent local state
     *
     * @return the agent local state
     */
    public YodaVariableMapping getAgentKnowledge(){
        return agentKnowledge;
    }

    /**
     * This method returns the global information of the agent
     *
     * @return the global information of the agent
     */
    public YodaVariableMapping getAgentInformation() {
        return agentInformation;
    }

    /**
     * This method returns the agent observations
     *
     * @return the agent observations
     */
    public YodaVariableMapping getAgentObservations() {
        return agentObservations;
    }

    /**
     * This method returns the agent behaviour
     *
     * @return the agent behaviour
     */
    public YodaBehaviour getAgentBehaviour() {
        return agentBehaviour;
    }

    /**
     * This method makes an agent step
     *
     * @param rg a random generator
     */
    public void step(RandomGenerator rg){
        WeightedStructure<YodaAction> actionSet = agentBehaviour.evaluate(rg, agentKnowledge, agentObservations);
        YodaAction selectedAction = agentBehaviour.selectAction(rg, actionSet);
        updateKnowledge(rg, selectedAction);
    }

    /**
     * This method updates the state of this agent
     *
     * @param rg a random generator
     * @param action the action that the agent should perform
     */
    public void updateKnowledge(RandomGenerator rg, YodaAction action){
        YodaVariableMapping newState = action.performAction(rg, agentKnowledge);
        agentKnowledge = newState;
    }

    /**
     * This method updates the global information of this agents
     *
     * @param rg a random generator
     */
    public void updateInfo(RandomGenerator rg){
        agentInformation = agentInfoUpdateFunction.compute(rg, this.agentKnowledge, this.agentInformation);
    }

    /**
     * This method updates the agent's observations set
     *
     * @param rg a random generator
     * @param system a YodaSystem
     */
    void computeObservations(RandomGenerator rg, YodaSystem system) {
        this.agentObservations = omegaFunction.compute(rg, system, this);
    }

    public YodaAgent next(RandomGenerator rg, YodaSystemState<?> state) {
        YodaVariableMapping newObservations = this.omegaFunction.compute(rg, state, this);
        WeightedStructure<YodaAction> actionSet = this.agentBehaviour.evaluate(rg, this.agentKnowledge, newObservations);
        YodaAction selectedAction = this.agentBehaviour.selectAction(rg, actionSet);
        YodaVariableMapping newKnowledge = selectedAction.performAction(rg, this.agentKnowledge);
        YodaVariableMapping newInfo = this.agentInfoUpdateFunction.compute(rg, newKnowledge, this.agentInformation);
        return new YodaAgent(this.identifier, this.name, newKnowledge, newInfo, newObservations, this.agentBehaviour, this.omegaFunction, this.agentInfoUpdateFunction);
    }
}
