package icy.icy4eclipse.templates;

import icy.icy4eclipse.core.IcyProjectTemplate;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class TemplatesActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		ClassLoader cl = getClass().getClassLoader();
		Package packageToSearch = this.getClass().getPackage();
		String packageName = packageToSearch.getName();
		String packagePath = packageName.replace('.', '/');
		Enumeration<URL> urls = cl.getResources(packagePath);
		while (urls.hasMoreElements()) {
			URL u = urls.nextElement();
			if ("bundleresource".equals(u.getProtocol().toLowerCase())) {
				u = FileLocator.toFileURL(u);
			}
			final String urlPath = URLDecoder.decode(u.getFile(), "UTF-8");
			final String protocol = u.getProtocol().toLowerCase();

			if ("file".equals(protocol)) {
				final File dir = new File(urlPath);
				for (File file : dir.listFiles()) {
					String classname = file.getName();
					if (classname.toLowerCase().endsWith(".class")) {
						classname = classname.substring(0, classname.length() - 6);
						addTemplateAsService(context, packageName + "." + classname);
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addTemplateAsService(BundleContext context, String classname) {
		try {
			Class clazz = Class.forName(classname);
			Class templateClazz = clazz.asSubclass(IcyProjectTemplate.class);
			IcyProjectTemplate template = (IcyProjectTemplate) templateClazz.newInstance();
			context.registerService(IcyProjectTemplate.class.getName(), template, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			// ignore
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
	}
}
