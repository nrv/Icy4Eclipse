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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 *
 */
public class IcyProjectPropertyPage extends PropertyPage implements IWorkbenchPreferencePage, Icy4EclipseCommonResources {

	private Button isIcyProjectCheck;

	@Override
	protected Control createContents(Composite parent) {
		Composite isIcyProjectGroup = new Composite(parent, SWT.NONE);
		isIcyProjectGroup.setLayout(new GridLayout(3, false));
		isIcyProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		isIcyProjectCheck = new Button(isIcyProjectGroup, SWT.CHECK | SWT.LEFT);
		isIcyProjectCheck.setText(ICY4ECLIPSE_ISICYPROJECT_LABEL);
		isIcyProjectCheck.setEnabled(true);
		
		try {		
			isIcyProjectCheck.setSelection(getJavaProject().getProject().hasNature(ICY_NATURE_ID));
		} catch (CoreException ex) {
			Icy4EclipsePlugin.logException(ex);	
		}
		
		Composite fixBuildpathGroup = new Composite(parent, SWT.NONE);
		fixBuildpathGroup.setLayout(new GridLayout(3, false));
		fixBuildpathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Button btFixBuildpath = new Button(fixBuildpathGroup, SWT.PUSH);
		btFixBuildpath.setText(ICY4ECLIPSE_FIXBUILDPATH_LABEL);
		btFixBuildpath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				performOk();
				try {
					IcyProject p = getIcyProject();
					if (p != null) {
						p.fixBuildpath();
					}
				} catch (CoreException e1) {
					Icy4EclipsePlugin.logException(e1);
				}
			}
		});	
		
		return isIcyProjectGroup;
	}
	
	@Override
	public boolean performOk() {
		try {
			if(isIcyProjectChecked()) {		
				Icy4EclipsePlugin.addIcyNature(getJavaProject());
			} else {
				Icy4EclipsePlugin.removeIcyNature(getJavaProject());
			}
		} catch (Exception ex) {
			Icy4EclipsePlugin.logException(ex);	
		}
		
		return true;
	}
	
	protected IJavaProject getJavaProject() throws CoreException {
		IProject project = (IProject) (this.getElement().getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}
	
	protected IcyProject getIcyProject() throws CoreException {
		return Icy4EclipsePlugin.create(getJavaProject());
	}
	
	public boolean isIcyProjectChecked() {
		return isIcyProjectCheck.getSelection();
	}

	@Override
	public void init(IWorkbench arg0) {
		
	}
	

}
