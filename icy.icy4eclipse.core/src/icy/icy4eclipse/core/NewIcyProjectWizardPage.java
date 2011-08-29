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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public class NewIcyProjectWizardPage extends WizardPage implements Icy4EclipseCommonResources, ModifyListener {
	private final static String EMPTY_PACKAGE_NAME = "---";
	private Composite composite;
	private Text txtPluginName;
	private Text txtPackageName;
	private Label lbPackage;
	private boolean pluginNameCurrentlyModified;
	private boolean packageModifiedManually;
	private List<Button> radioButtons;
	
	public NewIcyProjectWizardPage() {
		super(ICY4ECLIPSE_WIZ_TITLE);
		setTitle(ICY4ECLIPSE_WIZ_TITLE);
		setDescription(ICY4ECLIPSE_WIZ_DESC);
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		try {
			packageModifiedManually = false;
			pluginNameCurrentlyModified = false;

			Icy4EclipsePlugin.checkIcyConfiguration();

			composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			lbPackage = new Label(composite, SWT.BORDER);
			lbPackage.setText(ICY4ECLIPSE_WIZ_HELP + EMPTY_PACKAGE_NAME);

			Group group = new Group(composite, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			group.setLayout(layout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			new Label(group, SWT.NONE).setText(ICY4ECLIPSE_WIZ_PLUGIN_NAME);
			txtPluginName = new Text(group, SWT.BORDER);
			txtPluginName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txtPluginName.addModifyListener(this);

			new Label(group, SWT.NONE).setText(ICY4ECLIPSE_WIZ_PACKAGE_NAME);
			txtPackageName = new Text(group, SWT.BORDER);
			txtPackageName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txtPackageName.addModifyListener(this);

			new Label(composite, SWT.NONE).setText(ICY4ECLIPSE_WIZ_TEMPLATE_LABEL);
			Group templates = new Group(composite, SWT.NONE);
			GridLayout layoutt = new GridLayout();
			layoutt.numColumns = 1;
			templates.setLayout(layoutt);
			templates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			radioButtons = new ArrayList<Button>();
			IcyTemplateLocator locator = Icy4EclipsePlugin.getDefault().getTemplateLocator();
			locator.init();
			for (IcyProjectTemplate t : locator) {
				Button b = new Button(templates, SWT.RADIO);
				b.setText(t.getTemplateName() + " : " + t.getTemplateDescription());
				b.setData(t.getTemplateName());
				if (t instanceof DefaultIcyProjectTemplate) {
					b.setSelection(true);
				}
				radioButtons.add(b);
			}

			setControl(composite);
		} catch (Icy4EclipseException e) {
			setErrorMessage(e.getMessage());
			setControl(parent);
		}
	}

	public String getPluginName() {
		return txtPluginName.getText();
	}

	public String getSubpackageName() {
		return txtPackageName.getText();
	}
	
	public String getTemplateName() {
		for (Button b : radioButtons) {
			if (b.getSelection()) {
				return (String)b.getData();
			}
		}
		return null;
	}

	@Override
	public void modifyText(ModifyEvent event) {
		if (event.getSource() == txtPluginName) {
			if (txtPluginName.getText().length() > 0) {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IProject project = root.getProject(txtPluginName.getText());
				if (project.exists()) {
					setErrorMessage("Project " + txtPluginName.getText() + " already exists");
					setPageComplete(false);
					return;
				}
			}
			pluginNameCurrentlyModified = true;
			if (!packageModifiedManually) {
				txtPackageName.setText(Icy4EclipsePlugin.fromPluginnameToPackagename(txtPluginName.getText()));
			}
			pluginNameCurrentlyModified = false;
		} else if (event.getSource() == txtPackageName) {
			if (!pluginNameCurrentlyModified) {
				packageModifiedManually = true;
			}
			if (txtPackageName.getText().length() > 0) {
				lbPackage.setText(ICY4ECLIPSE_WIZ_HELP + Icy4EclipsePlugin.getFullPackageName(Icy4EclipsePlugin.getIcyDeveloper(), txtPackageName.getText()));
			} else {
				lbPackage.setText(ICY4ECLIPSE_WIZ_HELP + EMPTY_PACKAGE_NAME);
			}
			composite.layout();
		}

		setErrorMessage(null);
		setPageComplete((txtPluginName.getText().length() > 0) && (txtPackageName.getText().length() > 0));
	}

}