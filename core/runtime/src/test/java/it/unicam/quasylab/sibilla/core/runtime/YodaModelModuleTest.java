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

package it.unicam.quasylab.sibilla.core.runtime;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class YodaModelModuleTest {


    @Test
    void shouldLoadYodaModule() throws CommandExecutionException {
        assertNotNull(getRuntimeWithModule());
    }

    @Test
    void shouldLoadResource() throws CommandExecutionException {
        assertNotNull(getResource("yoda/robotAgent2.yoda"));
    }


    @Test
    void robotAgentScenario() throws CommandExecutionException {
        SibillaRuntime sr = getRuntimeWithModule();
        sr.load( getResource("yoda/robotAgent2.yoda"));
        sr.setDt(0.1);
        sr.setDeadline(100);
        sr.setReplica(1);
        sr.simulate("test");
    }

    private URL getResource(String name) {
        return getClass().getClassLoader().getResource(name);
    }

    private SibillaRuntime getRuntimeWithModule() throws CommandExecutionException {
        SibillaRuntime sr = new SibillaRuntime();
        sr.loadModule(YodaModelModule.MODULE_NAME);
        return sr;
    }

}