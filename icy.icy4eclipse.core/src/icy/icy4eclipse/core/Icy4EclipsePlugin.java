/*
 * Copyright 2011 Nicolas Herv�.
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

import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.debug.core.sourcelookup.containers.DefaultSourceContainer;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaProjectSourceContainer;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Nicolas Herv� - n.herve@laposte.net
 * 
 */
public class Icy4EclipsePlugin extends AbstractUIPlugin implements Icy4EclipseCommonResources {
	private static Icy4EclipsePlugin plugin;

	public static void addIcyNature(IJavaProject project) throws CoreException {
		addNature(project.getProject(), ICY_NATURE_ID);
	}

	static void addNature(IProject proj, String nat) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();

		boolean hasNature = false;
		for (String n : prevNatures) {
			if (nat.equals(n)) {
				hasNature = true;
				break;
			}
		}

		if (!hasNature) {
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nat;
			description.setNatureIds(newNatures);
			proj.setDescription(description, null);
		}
	}

	static void checkIcyConfiguration() throws Icy4EclipseException {
		String hd = getIcyHomeDir();
		if ((hd == null) || (hd.length() == 0)) {
			throw new Icy4EclipseException("Check your parameters, you need to set the Icy home directory");
		}

		String dev = getIcyDeveloper();
		if ((dev == null) || (dev.length() == 0)) {
			throw new Icy4EclipseException("Check your parameters, you need to set the Icy developer login");
		}

		File setXML = new File(hd, ICY_SETTING_XML);
		File versXML = new File(hd, ICY_VERSION_XML);
		if (!(setXML.exists() && versXML.exists())) {
			throw new Icy4EclipseException("Check your parameters, unable to find standard Icy files (" + ICY_SETTING_XML + ", " + ICY_VERSION_XML + ") in directory : " + hd);
		}
	}

	static IcyProject create(IJavaProject javaProject) {
		IcyProject result = null;
		try {
			result = (IcyProject) javaProject.getProject().getNature(ICY_NATURE_ID);
			if (result != null)
				result.setJavaProject(javaProject);
		} catch (CoreException ex) {
			logException(ex);
		}
		return result;
	}

	public static String fromPluginnameToClassname(String pluginname) {
		String classname = pluginname;
		int sz = classname.length();
		char[] asChar = new char[sz];
		classname.getChars(0, sz, asChar, 0);
		int idx = 0;
		boolean ucNext = true;
		classname = "";
		while (idx < sz) {
			if (asChar[idx] == ' ') {
				ucNext = true;
			} else {
				if (ucNext) {
					classname += String.valueOf(asChar[idx]).toUpperCase();
					ucNext = false;
				} else {
					classname += asChar[idx];
				}
			}
			idx++;
		}
		return classname;
	}

	public static String fromPluginnameToPackagename(String pluginname) {
		String classname = pluginname;
		int sz = classname.length();
		char[] asChar = new char[sz];
		classname.getChars(0, sz, asChar, 0);
		int idx = 0;
		classname = "";
		while (idx < sz) {
			if (asChar[idx] != ' ') {
				classname += String.valueOf(asChar[idx]).toLowerCase();
			}
			idx++;
		}
		return classname;
	}

	public static String getFullPackageName(String devName, String subpackageName) {
		return ICY_PLUGINS_PACKAGE + "." + devName + "." + subpackageName;
	}

	public static Icy4EclipsePlugin getDefault() {
		return plugin;
	}

	public static IPreferenceStore getDefaultPreferenceStore() {
		return plugin.getPreferenceStore();
	}

	static String getIcyDeveloper() {
		IPreferenceStore pref = getDefaultPreferenceStore();
		return pref.getString(ICY4ECLIPSE_PREF_DEVELOPER_KEY);
	}

	static boolean loadIcyJar() {
		IPreferenceStore pref = getDefaultPreferenceStore();
		return pref.getBoolean(ICY4ECLIPSE_PREF_ICYJAR_KEY);
	}

	static String getIcyHomeDir() {
		IPath path = JavaCore.getClasspathVariable(ICY4ECLIPSE_HOME_VARIABLE);
		if (path == null) {
			return null;
		}
		return path.toOSString();
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(ICY4ECLIPSE_PLUGIN_ID, path);
	}

	public static void logError(String msg) {
		getDefault().log(msg, null, SWT.COLOR_RED);
	}

	public static void logException(Exception ex) {
		getDefault().log(ex.getClass().getName() + " : " + ex.getMessage(), ex, SWT.COLOR_RED);
	}

	public static void logInfo(String msg) {
		getDefault().log(msg, null, SWT.COLOR_BLACK);
	}

	static void removeIcyNature(IJavaProject project) {
		try {
			IProject proj = project.getProject();
			IProjectDescription description = proj.getDescription();
			String[] prevNatures = description.getNatureIds();

			int natureIndex = -1;
			for (int i = 0; i < prevNatures.length; i++) {
				if (prevNatures[i].equals(ICY_NATURE_ID)) {
					natureIndex = i;
					i = prevNatures.length;
				}
			}

			if (natureIndex != -1) {
				String[] newNatures = new String[prevNatures.length - 1];
				System.arraycopy(prevNatures, 0, newNatures, 0, natureIndex);
				System.arraycopy(prevNatures, natureIndex + 1, newNatures, natureIndex, prevNatures.length - (natureIndex + 1));
				description.setNatureIds(newNatures);
				proj.setDescription(description, null);
			}
		} catch (CoreException ex) {
			logException(ex);
		}
	}

	static void setIcyHomeDir(String hd) {
		try {
			JavaCore.setClasspathVariable(ICY4ECLIPSE_HOME_VARIABLE, new Path(hd), null);
		} catch (JavaModelException e) {
			logException(e);
		}
	}

	public static void startIcy() {
		try {
			getDefault().startIcyInternal(false, true);
		} catch (CoreException e) {
			logException(e);
		}
	}

	public static void startIcyDebug() {
		try {
			getDefault().startIcyInternal(true, true);
		} catch (CoreException e) {
			logException(e);
		}
	}

	public static void startIcyUpdate() {
		try {
			getDefault().startIcyInternal(false, false);
		} catch (CoreException e) {
			logException(e);
		}
	}

	private IcyTemplateLocator templateLocator;

	public Icy4EclipsePlugin() {
		super();
	}

	private List<IcyProject> computeOpenIcyProjectsList() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] allProjects = root.getProjects();

		ArrayList<IcyProject> tempList = new ArrayList<IcyProject>();

		for (int i = 0; i < allProjects.length; i++) {
			IProject project = allProjects[i];
			try {
				if ((project.isOpen()) && project.hasNature(ICY_NATURE_ID)) {
					IcyProject p = create((IJavaProject) project.getNature(JavaCore.NATURE_ID));
					tempList.add(p);
				}
			} catch (CoreException e) {
				logException(e);
			}
		}
		return tempList;

	}

	private List<File> getAllJarFiles(File root) {
		List<File> result = new ArrayList<File>();
		File[] temp = root.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toUpperCase().endsWith(".JAR");
			}
		});
		for (File f : temp) {
			if (f.isDirectory()) {
				result.addAll(getAllJarFiles(f));
			} else {
				result.add(f);
			}
		}
		return result;
	}

	String getIcyMainClass() {
		IPreferenceStore pref = getPreferenceStore();
		return pref.getString(ICY4ECLIPSE_PREF_MAINCLASS_KEY);
	}

	String getIcyMemory() {
		IPreferenceStore pref = getPreferenceStore();
		return pref.getString(ICY4ECLIPSE_PREF_MEMORY_KEY);
	}

	IcyTemplateLocator getTemplateLocator() {
		return templateLocator;
	}

	private void log(String msg, Exception ex, int color) {
		MessageConsole myConsole = null;
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (ICY4ECLIPSE_CONSOLE.equals(existing[i].getName())) {
				myConsole = (MessageConsole) existing[i];
			}
		}

		if (myConsole == null) {
			myConsole = new MessageConsole(ICY4ECLIPSE_CONSOLE, null);
			conMan.addConsoles(new IConsole[] { myConsole });
		}

		MessageConsoleStream out = myConsole.newMessageStream();
		out.setColor(Display.getCurrent().getSystemColor(color));
		out.println(msg);

		if (ex != null) {
			PrintStream ps = new PrintStream(out);
			ex.printStackTrace(ps);
		}

		myConsole.activate();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);

		context.registerService(IcyProjectTemplate.class.getName(), new DefaultIcyProjectTemplate(), null);

		plugin = this;
		templateLocator = new IcyTemplateLocator(context);
	}

	private void startIcyInternal(boolean debug, boolean bypassJarclassloader) throws CoreException {
		logInfo("Starting Icy - (debug = " + debug + ") (disableJCL = " + bypassJarclassloader + ")");

		// Check parameters
		try {
			checkIcyConfiguration();
		} catch (Icy4EclipseException e) {
			logException(e);
			return;
		}

		String hd = getIcyHomeDir();
		File pluginsDirectory = new File(hd, ICY_PLUGINS_DIR);
		List<IcyProject> icyProjects = computeOpenIcyProjectsList();
		List<String> classpath = new ArrayList<String>();

		// Add Icy jars to system class loader if needed
		if (loadIcyJar()) {
			for (String s : ICY_JARS) {
				classpath.add(hd + File.separator + s);
			}
		}

		if (bypassJarclassloader) {
			// Add Eclipse Icy projects to system class loader
			for (IcyProject p : icyProjects) {
				IJavaProject project = p.getJavaProject();
				for (String s : JavaRuntime.computeDefaultRuntimeClassPath(project)) {
					classpath.add(s);
				}
			}

			// Add plugins jars to system class loader
			IPreferenceStore pref = getPreferenceStore();
			String bypassJars = pref.getString(ICY4ECLIPSE_PREF_BYPASS_JARS_KEY);
			List<Pattern> bypassRegexp = null;
			if (bypassJars != null) {
				bypassJars = bypassJars.trim();
				if (bypassJars.length() > 0) {
					String[] bypass = bypassJars.split(":");
					bypassRegexp = new ArrayList<Pattern>();
					for (String r : bypass) {
						bypassRegexp.add(Pattern.compile("^(.*)" + r + "$", Pattern.CASE_INSENSITIVE));
					}
				}
			}

			if (pluginsDirectory.exists()) {
				List<File> jars = getAllJarFiles(pluginsDirectory);
				for (File f : jars) {
					boolean add = true;
					String path = f.getAbsolutePath();
					if (bypassRegexp != null) {
						for (Pattern p : bypassRegexp) {
							Matcher m = p.matcher(path);
							if (m.matches()) {
								logInfo(" - bypassing jar : " + p.pattern() + " -> " + path);
								add = false;
								break;
							}
						}
					}

					if (add) {
						classpath.add(path);
					}
				}
			}
		}

		// Other launch arguments
		StringBuffer programArguments = new StringBuffer();
		for (String arg : ICY_PRG_ARGS) {
			programArguments.append(" " + arg);
		}
		if (bypassJarclassloader) {
			programArguments.append(" " + ICY_BYPASS_JARCLASSLOADER_ARG);
		}

		StringBuffer jvmArguments = new StringBuffer();
		for (String arg : ICY_VM_ARGS) {
			jvmArguments.append(" " + arg);
		}
		jvmArguments.append(" -Xmx" + getIcyMemory() + "m");

		// Launching the JVM
		ILaunchConfigurationType launchType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfigurationWorkingCopy config = launchType.newInstance(null, ICY4ECLIPSE_LABEL);
		config.setAttribute(IDebugUIConstants.ATTR_PRIVATE, false);
		config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.eclipse.jdt.launching.sourceLocator.JavaSourceLookupDirector");

		if (bypassJarclassloader) {
			ISourceLookupDirector sourceLocator = new JavaSourceLookupDirector();
			ISourcePathComputer computer = DebugPlugin.getDefault().getLaunchManager().getSourcePathComputer("org.eclipse.jdt.launching.sourceLookup.javaSourcePathComputer");
			sourceLocator.setSourcePathComputer(computer);

			ArrayList<ISourceContainer> sourceContainers = new ArrayList<ISourceContainer>();

			if (!icyProjects.isEmpty()) {
				for (IcyProject ip : icyProjects) {
					IJavaProject project = ip.getJavaProject();
					sourceContainers.add(new JavaProjectSourceContainer(project));
				}

				Set<IPath> external = new HashSet<IPath>();

				for (IcyProject ip : icyProjects) {
					IJavaProject project = ip.getJavaProject();

					IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
					for (int ri = 0; ri < roots.length; ri++) {
						IPackageFragmentRoot root = roots[ri];
						if (root.isExternal()) {
							IPath location = root.getPath();
							if (external.contains(location)) {
								continue;
							}
							external.add(location);
						}
						sourceContainers.add(new PackageFragmentRootSourceContainer(root));
					}
				}
			}

			sourceContainers.add(new DefaultSourceContainer());

			sourceLocator.setSourceContainers((ISourceContainer[]) sourceContainers.toArray(new ISourceContainer[sourceContainers.size()]));
			sourceLocator.initializeParticipants();
			config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_MEMENTO, sourceLocator.getMemento());
		}

		ArrayList<String> classpathMementos = new ArrayList<String>();
		for (String s : classpath) {
			IRuntimeClasspathEntry cpEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(s));
			cpEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			classpathMementos.add(cpEntry.getMemento());
		}

		IPath path = new Path(JavaRuntime.JRE_CONTAINER);
		try {
			IClasspathEntry cpEntry = JavaCore.newContainerEntry(path);
			IRuntimeClasspathEntry rcpEntry = JavaRuntime.newRuntimeContainerClasspathEntry(cpEntry.getPath(), IRuntimeClasspathEntry.STANDARD_CLASSES);
			classpathMementos.add(rcpEntry.getMemento());
		} catch (CoreException ex) {
			logException(ex);
		}

		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpathMementos);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, programArguments.toString());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, jvmArguments.toString());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, getIcyMainClass());
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, getIcyHomeDir());

		logInfo(" - home dir   : " + getIcyHomeDir());
		logInfo(" - main class : " + getIcyMainClass());
		logInfo(" - prog args  : " + programArguments);
		logInfo(" - jvm args   : " + jvmArguments);
		logInfo(" - classpath  : " + classpath);

		config.launch(debug ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE, null);

	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
