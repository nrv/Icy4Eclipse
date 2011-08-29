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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 *
 */
public class NewIcyProjectWizard extends Wizard implements IWorkbenchWizard, Icy4EclipseCommonResources {
	private NewIcyProjectWizardPage myPage;

	@Override
	public void addPages() {
		myPage = new NewIcyProjectWizardPage();
		addPage(myPage);

		super.addPages();
	}

	@Override
	public boolean performFinish() {
		String pluginName = myPage.getPluginName();
		String devName = Icy4EclipsePlugin.getIcyDeveloper();
		String subpackageName = myPage.getSubpackageName();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(pluginName);
		
		String templateName = myPage.getTemplateName();
		IcyTemplateLocator locator = Icy4EclipsePlugin.getDefault().getTemplateLocator();
		locator.init();
		IcyProjectTemplate template = locator.getTemplate(templateName);
		if (template == null) {
			Icy4EclipsePlugin.logError("Unable to find a template ("+templateName+")");
			return false;
		}

		try {
			// Project creation
			project.create(null);
			if (!project.isOpen()) {
				project.open(null);
			}
		} catch (CoreException e) {
			Icy4EclipsePlugin.logException(e);
			return false;
		}

		try {
			// Natures
			Icy4EclipsePlugin.addNature(project, JavaCore.NATURE_ID);
			IJavaProject javaProject = JavaCore.create(project);
			Icy4EclipsePlugin.addIcyNature(javaProject);

			// Folder
			IFolder src = project.getFolder("src");
			if (!src.exists()) {
				src.create(true, true, null);
			}

			// Classpath
			List<IClasspathEntry> classpath = new ArrayList<IClasspathEntry>();
			classpath.add(JavaCore.newSourceEntry(src.getFullPath()));
			for (String e : ICY_JARS) {
				classpath.add(JavaCore.newVariableEntry(new Path(ICY4ECLIPSE_HOME_VARIABLE + "/" + e), null, null));
			}
			List<IClasspathEntry> templateEntries = template.getSpecificClasspathEntries();
			if (templateEntries != null) {
				classpath.addAll(templateEntries);
			}
			IClasspathEntry[] jreEntries = PreferenceConstants.getDefaultJRELibrary();
			for (IClasspathEntry cpe : jreEntries) {
				classpath.add(cpe);
			}
			javaProject.setRawClasspath(classpath.toArray(new IClasspathEntry[classpath.size()]), null);

			// Default plugin java file
			String packageName = Icy4EclipsePlugin.getFullPackageName(devName, subpackageName);
			IPackageFragmentRoot srcRoot = javaProject.getPackageFragmentRoot(src);
			IPackageFragment pack = srcRoot.createPackageFragment(packageName, false, null);

			String className = Icy4EclipsePlugin.fromPluginnameToClassname(pluginName);
			
			pack.createCompilationUnit(className + ".java", template.getPluginMainClassImplementation(pluginName, packageName, className), false, null);
			
			// Compile
			project.build(IncrementalProjectBuilder.FULL_BUILD , null);

		} catch (CoreException e) {
			Icy4EclipsePlugin.logException(e);
			try {
				project.delete(true, true, null);
			} catch (CoreException e1) {
				Icy4EclipsePlugin.logException(e1);
			}
			return false;
		}

		return true;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {

	}

}
