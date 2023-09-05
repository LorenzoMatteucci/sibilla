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

package it.unicam.quasylab.sibilla.core.models.slam;

import it.unicam.quasylab.sibilla.core.models.slam.agents.SlamAgent;
import it.unicam.quasylab.sibilla.core.models.slam.agents.AgentMessage;

import java.util.List;

/**
 * Instances of this class represent the result of an agent step. This consists of a list of messages
 * and the agent that has performed the step.
 */
public class ActivityResult {

    private final List<AgentMessage> sentMessages;
    private final SlamAgent involvedAgent;

    /**
     * Creates a new instance of agent results.
     *
     * @param sender agent performing the step.
     * @param sentMessages list of sent messages.
     */
    public ActivityResult(SlamAgent sender, List<AgentMessage> sentMessages) {
        this.involvedAgent = sender;
        this.sentMessages = sentMessages;
    }

    /**
     * Returns the list of sent messages.
     *
     * @return the list of sent messages.
     */
    public List<AgentMessage> getSentMessages() {
        return sentMessages;
    }

    /**
     * Returns the agent performing the step.
     *
     * @return the agent performing the step.
     */
    public SlamAgent getAgent() {
        return involvedAgent;
    }


}
