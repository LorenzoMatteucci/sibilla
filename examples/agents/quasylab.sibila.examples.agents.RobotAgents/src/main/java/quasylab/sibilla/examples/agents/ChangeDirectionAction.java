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

package quasylab.sibilla.examples.agents;

import org.apache.commons.math3.random.RandomGenerator;
import quasylab.sibilla.core.models.quasylab.sibilla.core.models.agents.AgentAction;

import java.util.Arrays;


public class ChangeDirectionAction implements AgentAction {
    private final String name;
    private final double dx;
    private final double dy;

    public ChangeDirectionAction(String name, double dx, double dy) {
        this.name = name;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double[] performAction(RandomGenerator rg, double[] currentState) {
        double[] nextState = Arrays.copyOf(currentState,currentState.length);
        nextState[RobotAgents.DIRX_VAR] = dx;
        nextState[RobotAgents.DIRY_VAR] = dy;
        return nextState;
    }
}
