/*
 * Copyright 2011 Nicolas Hervé.
 * 
 * This file is part of Icy4Eclipse.
 * 
 * Icy4Eclipse is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Icy4Eclipse is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Icy4Eclipse. If not, see <http://www.gnu.org/licenses/>.
 */

package icy.icy4eclipse.core;

import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public interface IcyProjectTemplate {
	String getTemplateName();
	String getTemplateDescription();
	List<IClasspathEntry> getSpecificClasspathEntries();
	String getPluginMainClassImplementation(String pluginName, String packageName, String className);
}
