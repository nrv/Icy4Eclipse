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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public class IcyProject extends PlatformObject implements IProjectNature, Icy4EclipseCommonResources {
	protected IProject project;
	protected IJavaProject javaProject;

	@Override
	public void configure() throws CoreException {

	}

	@Override
	public void deconfigure() throws CoreException {

	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject arg0) {
		project = arg0;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	void fixBuildpath() {
		try {
			IClasspathEntry[] entries = javaProject.getRawClasspath();

			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				String path = entry.getPath().toOSString();
				if ((entry.getEntryKind() != IClasspathEntry.CPE_VARIABLE) && (path.startsWith(Icy4EclipsePlugin.getIcyHomeDir()))) {
					String npath = path.substring(Icy4EclipsePlugin.getIcyHomeDir().length());
					IPath ipath = new Path(ICY4ECLIPSE_HOME_VARIABLE + npath);
					entries[i] = JavaCore.newVariableEntry(ipath, null, null);
					Icy4EclipsePlugin.logInfo("fixBuildpath : " + path + " -> " + ipath.toOSString());
				}
			}

			javaProject.setRawClasspath(entries, null);
		} catch (JavaModelException e) {
			Icy4EclipsePlugin.logException(e);
		}
	}

}
