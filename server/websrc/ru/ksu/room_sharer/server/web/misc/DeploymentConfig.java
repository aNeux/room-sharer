package ru.ksu.room_sharer.server.web.misc;

import ru.ksu.room_sharer.server.AppConfig;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;

public class DeploymentConfig
{
	protected AppConfig appConfig;
	protected String configFileName, appRoot, root, appContextPath, logsDir;
	
	public DeploymentConfig(AppConfig appConfig)
	{
		this.appConfig = appConfig;
		
		configureAppRoot();
		configureAppContextPath();
		configureRoot(new File(System.getProperty("user.dir")));
		
		if (configFileName == null || root == null) {
			System.err.println("Could not find '" + appConfig.getCfgDir() + "' directory, using default one");
			configFileName = appConfig.getConfigFilePath();
			if (!new File(configFileName).isAbsolute())
				configFileName = appRoot + configFileName;
			root = appRoot;
		}
	}
	
	private void configureAppRoot()
	{
		appRoot = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF");
		if (!appRoot.endsWith(File.separator))
			appRoot += File.separator;
	}
	
	private void configureAppContextPath()
	{
		String currentContext = FacesContext.getCurrentInstance().getExternalContext().getContextName();
		if (currentContext == null)
			currentContext = "/room-sharer";
		else if (!currentContext.startsWith("/"))
			currentContext = "/" + currentContext;
		
		appContextPath = currentContext;
	}
	
	private void configureRoot(File workingDir)
	{
		try
		{
			if (!trySelectRoot(workingDir.getParentFile())) {
				if (!trySelectRoot(workingDir)) {
					configFileName = null;
					root = null;
				}
			}
		}
		catch (IOException e)
		{
			configFileName = null;
			root = null;
		}
	}
	
	private boolean trySelectRoot(File dir) throws IOException
	{
		root = dir.getCanonicalPath();
		if (!root.endsWith(File.separator))
			root += File.separator;
		
		File expConf = new File(dir, appConfig.getConfigFilePath());
		configFileName = expConf.getCanonicalPath();
		return expConf.exists();
	}
	
	
	public String getConfigFileName()
	{
		return configFileName;
	}
	
	public String getAppRoot()
	{
		return appRoot;
	}
	
	public String getAppContextPath()
	{
		return appContextPath;
	}
		
	public String getRoot()
	{
		return root;
	}
}
