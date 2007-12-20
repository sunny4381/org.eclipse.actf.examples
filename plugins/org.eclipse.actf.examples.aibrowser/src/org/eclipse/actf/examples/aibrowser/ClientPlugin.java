/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.examples.aibrowser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.actf.ai.xmlstore.XMLStorePlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class ClientPlugin extends AbstractUIPlugin {
    private static final Path configPath = new Path("config");

	//The shared instance.
	private static ClientPlugin plugin;
    
    private IWebBrowser webBrowser; 
    
    private BundleContext _context;
    
	/**
	 * The constructor.
	 */
	public ClientPlugin() {
		plugin = this;
	}
    
    private File getLocalPluginDir() {
        URL url = FileLocator.find(getBundle(), configPath, null);
        try {
            url = FileLocator.toFileURL(url);
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), null);
            return new File(uri);
        } catch (IOException e) {
            return null;
        } catch (URISyntaxException e) {
            return null;
        }
    }

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
        this._context = context;
        File configDirFile = getLocalPluginDir();
        if (configDirFile == null) return;
        XMLStorePlugin.getDefault().addSystemStore(configDirFile);
	}
    
    public BundleContext getContext(){
        return this._context;
    }

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ClientPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.actf.examples.aibrowser", path);
	}
    
    void setWebBrowser(IWebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }
    
    public IWebBrowser getWebBrowser() {
        return webBrowser;
    }
    
    public static String getResourceString(String key) {
        ResourceBundle bundle = Platform.getResourceBundle(getDefault().getBundle());
        try {
            return (null != bundle) ? bundle.getString(key) : key;
        } catch (MissingResourceException mre) {
            return "???" + key + "???";
        }
    }
    
    public static String formatResourceString(String key, String arg) {
        String args[] = {arg};
        
        return formatResourceString(key, ((String[]) (args)));
    }

    public static String formatResourceString(String key, String[] args) {
        String pattern = getResourceString(key);
        
        return MessageFormat.format(pattern, (Object[])args);
    }
    
    private Bundle getBundle(String id) {
        Bundle[] bundles = _context.getBundles();
        for(int i = 0; i < bundles.length; i++) {
            if (bundles[i].getSymbolicName().equals(id)) {
                return bundles[i];
            }
        }
        return null;
    }

    public String getHelpFileURI(String pluginId, String path) {
        Bundle bundle = ClientPlugin.getDefault().getBundle(pluginId);
        String nlStr = Locale.getDefault().getLanguage();
        URL url = bundle.getResource("nl/"+nlStr+"/"+path);
        if (url == null) {
            url = bundle.getResource(path);
        }
        if (url == null)
            return "";
        try {
            url = FileLocator.resolve(url);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return url.toString();
    }
}
