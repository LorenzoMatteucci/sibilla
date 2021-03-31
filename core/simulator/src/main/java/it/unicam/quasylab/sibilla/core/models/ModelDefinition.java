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

package it.unicam.quasylab.sibilla.core.models;

/**
 * This interface implements a factory that can be used to build a model according
 * to some parameters.
 *
 * @param <S>
 */
public interface ModelDefinition<S extends State> {

    /**
     * Returns the number of parameters needed to build default initial state.
     *
     * @return the number of parameters needed to build default initial state.
     */
    int stateArity();

    /**
     * Returns the number of parameters needed to build initial state <code>name</code>.
     *
     * @return the number of parameters needed to build initial state <code>name</code>.
     */
    int stateArity(String name);

    /**
     * Returns the number of parameters needed to build a model.
     *
     * @return the number of parameters needed to build a model.
     */
    default String[] getModelParameters() {
        return new String[0];
    }

    /**
     * Sets the value of parameter <code>name</code>. An {@link IllegalArgumentException} is
     * thrown if the parameter is unknowns.
     *
     * @param name name of parameter to set.
     * @param value value of parameter.
     */
    default void setParameter(String name, double value) {
        throw new IllegalArgumentException(String.format("Species %s is unknown!",name));
    }

    /**
     * Returns the array of possible initial states defined in the model.
     *
     * @return the array of possible initial states defined in the model.
     */
    String[] states();

    /**
     * Create the state with the given name by using the given parameters.
     *
     * @param parameters parameters to use in state creation.
     * @return the default state associated the given parameters.
     */
    S state(String name, double ... parameters);

    /**
     * Create the default state (that is the first one in the array) with
     * the given parameters.
     *
     * @param parameters parameters to use in state creation.
     * @return the default state associated the given parameters.
     */
    S state(double ... parameters);


    /**
     * Creates a new {@link Model}.
     *
     * @return a model built from a given set of parameters.
     */
    Model<S> createModel();


}
