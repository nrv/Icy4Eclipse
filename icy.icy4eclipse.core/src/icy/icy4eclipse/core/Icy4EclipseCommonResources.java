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

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 *
 */
public interface Icy4EclipseCommonResources {
	String ICY4ECLIPSE_PLUGIN_ID = "icy.icy4eclipse.core" ; 
	String ICY_NATURE_ID = ICY4ECLIPSE_PLUGIN_ID + ".icynature" ; 
	
	String[] ICY_JARS = {"icy.jar"};
	String[] ICY_VM_ARGS = {"-XX:CompileCommand=exclude,icy/image/IcyBufferedImage.createFrom"};
	String[] ICY_PRG_ARGS = {};
	String ICY_PLUGINS_DIR = "plugins";
	String ICY_PLUGINS_PACKAGE = "plugins";
	String ICY_SETTING_XML = "setting.xml";
	String ICY_VERSION_XML = "version.xml";
	String ICY_BYPASS_JARCLASSLOADER_ARG = "--disableJCL";
	
	String ICY4ECLIPSE_LABEL = "Icy";
	
	String ICY4ECLIPSE_PREF_HOME_LABEL = "Icy home directory";
	String ICY4ECLIPSE_PREF_HOME_KEY = "icy_main_directory";
	String ICY4ECLIPSE_HOME_VARIABLE = "ICY_HOME";
	
	String ICY4ECLIPSE_PREF_DEVELOPER_LABEL = "Developer login";
	String ICY4ECLIPSE_PREF_DEVELOPER_KEY = "icy_developer";

	String ICY4ECLIPSE_PREF_MAINCLASS_KEY = "icy_mainclass";
	String ICY4ECLIPSE_PREF_MAINCLASS_LABEL = "Icy main class";
	String ICY4ECLIPSE_PREF_MAINCLASS_DEFAULT = "icy.main.Icy";
	
	String ICY4ECLIPSE_PREF_MEMORY_KEY = "icy_memory";
	String ICY4ECLIPSE_PREF_MEMORY_LABEL = "Memory allocated for the VM";
	String ICY4ECLIPSE_PREF_MEMORY_DEFAULT = "300";
	
	String ICY4ECLIPSE_ISICYPROJECT_LABEL = "Is an Icy project";
	String ICY4ECLIPSE_FIXBUILDPATH_LABEL = "Fix buildpath";
	String ICY4ECLIPSE_CONSOLE = "Icy4Eclipse";
	
	String ICY4ECLIPSE_WIZ_TITLE = "Icy Plugin informations";
	String ICY4ECLIPSE_WIZ_DESC = "Please enter your new plugin informations";
	String ICY4ECLIPSE_WIZ_PLUGIN_NAME = "Plugin name";
	String ICY4ECLIPSE_WIZ_PACKAGE_NAME = "Subpackage name";
	String ICY4ECLIPSE_WIZ_HELP = "The java package created will be : ";
	String ICY4ECLIPSE_WIZ_TEMPLATE_LABEL = "Choose your plugin template";
}
