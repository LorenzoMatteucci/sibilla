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
/**
 * 
 */
package it.unicam.quasylab.sibilla.core.simulator.util;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;


/**
 * @author loreti
 *
 */
public class ModelPublisher {
	
	private final String outputDirectory;
	private final ClassLoader classLoader;
	
	public ModelPublisher( String outputDirectory ) throws MalformedURLException {
		this.outputDirectory = outputDirectory;
		this.classLoader = new URLClassLoader(
				new URL[] { new File(outputDirectory).toURI().toURL() } ,
				Thread.currentThread().getContextClassLoader()
		);
	}
	
	public boolean buildClass(File ... files) {		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(files);
		return compiler.getTask(null, fileManager, null, Arrays.asList("-d",outputDirectory), null, compilationUnits).call();
	}
	
	public <T> T loadModel(String name, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		return clazz.cast(classLoader.loadClass(name).getDeclaredConstructor().newInstance());
	}
}
