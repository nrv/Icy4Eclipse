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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public class Icy4EclipsePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, Icy4EclipseCommonResources {
	public Icy4EclipsePreferencePage() {
		super();
		setPreferenceStore(Icy4EclipsePlugin.getDefaultPreferenceStore());
	}

	private DirectoryFieldEditor icyHome;
	private StringFieldEditor icyDeveloper;
	private StringFieldEditor icyMemory;
	private StringFieldEditor icyMainClass;
	private BooleanFieldEditor icyJar;

	@Override
	public void init(IWorkbench arg0) {
		if (!(getPreferenceStore().contains(ICY4ECLIPSE_PREF_MEMORY_KEY)) || (getPreferenceStore().getString(ICY4ECLIPSE_PREF_MEMORY_KEY).length() == 0) ) {
			getPreferenceStore().setValue(ICY4ECLIPSE_PREF_MEMORY_KEY, ICY4ECLIPSE_PREF_MEMORY_DEFAULT);
		}
		
		if (!(getPreferenceStore().contains(ICY4ECLIPSE_PREF_MAINCLASS_KEY)) || (getPreferenceStore().getString(ICY4ECLIPSE_PREF_MAINCLASS_KEY).length() == 0) ) {
			getPreferenceStore().setValue(ICY4ECLIPSE_PREF_MAINCLASS_KEY, ICY4ECLIPSE_PREF_MAINCLASS_DEFAULT);
		}
		
		if (!(getPreferenceStore().contains(ICY4ECLIPSE_PREF_ICYJAR_KEY)) || (getPreferenceStore().getString(ICY4ECLIPSE_PREF_ICYJAR_KEY).length() == 0) ) {
			getPreferenceStore().setValue(ICY4ECLIPSE_PREF_ICYJAR_KEY, ICY4ECLIPSE_PREF_ICYJAR_DEFAULT);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Group homeGroup = new Group(parent, SWT.NONE);
		homeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		icyHome = new DirectoryFieldEditor(ICY4ECLIPSE_PREF_HOME_KEY, ICY4ECLIPSE_PREF_HOME_LABEL, homeGroup);
		icyHome.setStringValue(Icy4EclipsePlugin.getIcyHomeDir());
		
		Group devGroup = new Group(parent, SWT.NONE);
		devGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		icyDeveloper = new StringFieldEditor(ICY4ECLIPSE_PREF_DEVELOPER_KEY, ICY4ECLIPSE_PREF_DEVELOPER_LABEL, devGroup);
		icyDeveloper.setPreferenceStore(getPreferenceStore());
		icyDeveloper.load();

		Group memoryGroup = new Group(parent, SWT.NONE);
		memoryGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		icyMemory = new StringFieldEditor(ICY4ECLIPSE_PREF_MEMORY_KEY, ICY4ECLIPSE_PREF_MEMORY_LABEL, memoryGroup);
		icyMemory.setPreferenceStore(getPreferenceStore());
		icyMemory.load();
		
		Group classGroup = new Group(parent, SWT.NONE);
		classGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		icyMainClass = new StringFieldEditor(ICY4ECLIPSE_PREF_MAINCLASS_KEY, ICY4ECLIPSE_PREF_MAINCLASS_LABEL, classGroup);
		icyMainClass.setPreferenceStore(getPreferenceStore());
		icyMainClass.load();
		
		Group jarGroup = new Group(parent, SWT.NONE);
		jarGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		icyJar = new BooleanFieldEditor(ICY4ECLIPSE_PREF_ICYJAR_KEY, ICY4ECLIPSE_PREF_ICYJAR_LABEL, jarGroup);
		icyJar.setPreferenceStore(getPreferenceStore());
		icyJar.load();

		return parent;
	}

	@Override
	public boolean performOk() {
		Icy4EclipsePlugin.setIcyHomeDir(icyHome.getStringValue());
		icyMemory.store();
		icyDeveloper.store();
		icyMainClass.store();
		icyJar.store();

		return true;
	}

}
