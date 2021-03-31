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

import java.io.Serializable;

/**
 * Indicates a condition or an event that we can observe in a state.
 *
 * @param <T>
 */
@FunctionalInterface
public interface StatePredicate<T> extends Serializable {

    StatePredicate<Object> TRUE = s -> true;

    /**
     * Checks if a state satisfies the predicate.
     *
     * @param t a state
     * @return true if the state <code>t</code> satisfies the predicate.
     */
    boolean check(T t);


}
