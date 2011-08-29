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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author Nicolas Hervé - n.herve@laposte.net
 * 
 */
public class IcyTemplateLocator implements Iterable<IcyProjectTemplate> {
	private BundleContext context;
	private Map<String, IcyProjectTemplate> templates;

	public IcyTemplateLocator(BundleContext context) {
		super();
		this.context = context;
	}

	public IcyProjectTemplate getTemplate(String key) {
		return templates.get(key);
	}

	public void init() {
		if (templates == null) {
			try {
				templates = new HashMap<String, IcyProjectTemplate>();
				ServiceReference<?>[] srs = context.getServiceReferences(IcyProjectTemplate.class.getName(), null);
				for (ServiceReference<?> sr : srs) {
					IcyProjectTemplate template = (IcyProjectTemplate) context.getService(sr);
					templates.put(template.getTemplateName(), template);
				}
			} catch (InvalidSyntaxException e) {
				Icy4EclipsePlugin.logException(e);
			}
		}
	}

	@Override
	public Iterator<IcyProjectTemplate> iterator() {
		return templates.values().iterator();
	}

	public int size() {
		return templates.size();
	}

}
