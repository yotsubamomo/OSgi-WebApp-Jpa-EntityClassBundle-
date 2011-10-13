package com.gfactor.jpa.internal.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.ClassUtils;

public class OsgiPersistenceClassLoader implements BundleContextAware{
	public static final String JPA_MANIFEST_HEADER = "Meta-Persistence-ScanPackage";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	
	private BundleContext bundleContext;
	
	public void start(){
		getPersistenceList();
	}
	
	/**
	 * Get all Entity class from persistence units bundle , 
	 * The persistence units bundle have Meta-Persistence-ScanPackage information on the MANIFEST.mf.
	 * @return List<Class> 
	 */
	public List<Class<?>> getPersistenceList(){
		List<Class<?>> clazzList = new ArrayList<Class<?>>();
		Bundle[] bundles = bundleContext.getBundles();
		
		try {
			clazzList = resolverAllPersistenceUnitsClass(bundles);
		} catch (Exception e) {
			logger.error("getPersistenceList fail...",e);			
		}		
		return clazzList;
	}
	
	private List<Class<?>> resolverAllPersistenceUnitsClass(Bundle[] bundles) throws Exception{
		List<Class<?>> classList =  new ArrayList<Class<?>>();
		logger.info("resolverAllPersistenceUnitsClass ......");
		
		for (Bundle bundle : bundles) {
			Dictionary<String, String> customJpaMetaInfoHeader =bundle.getHeaders(JPA_MANIFEST_HEADER);
			final String allScanPackageListString = customJpaMetaInfoHeader.get(JPA_MANIFEST_HEADER);
			
			if (allScanPackageListString == null)
				continue;
			
			logger.info("bundle name = " + bundle.getSymbolicName());			
			logger.info("get JPA_MANIFEST_HEADER String = " + allScanPackageListString);
			
			foreachBundleGetPersistenceUnitsClass(classList, bundle,allScanPackageListString);			 
		}
		
		logger.info("all persistence class size = " + classList.size());
		return classList;
	}

	private void foreachBundleGetPersistenceUnitsClass(List<Class<?>> classList,
			Bundle bundle, final String allScanPackageListString) {
		for (String scanClassPath : allScanPackageListString.split(",")) {
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			String resourceClassPath = convertClassNameToResourcePath(scanClassPath);				
			Collection<String> allResourcePathClassName= wiring.listResources(resourceClassPath,"*.class", BundleWiring.LISTRESOURCES_LOCAL);
			logger.info("resolverAllPersistenceUnitsClass resolverAllPersistenceUnitsClass : Check BundleWiring = ["+ wiring + "]");	
			
			for (String string : allResourcePathClassName) {						
				String loadClassName = convertResourcePathToClassName(string);					
				printDebugMessage(wiring, resourceClassPath, allResourcePathClassName,loadClassName);					
				
				try {
					Class<?> loadClazz = bundle.loadClass(loadClassName);
					classList.add(loadClazz);
				} catch (ClassNotFoundException e) {
					logger.error("can't load class..." , e);
				}				
			}
			logger.info("");
		}
	}


	private void printDebugMessage(BundleWiring wiring,
			String resourceClassPath, Collection<String> resources,String loadClassName) {
		
//		if(!logger.isDebugEnabled()) return;	
		
		logger.info("resolverAllPersistenceUnitsClass resolverAllPersistenceUnitsClass : resourceClassPath = ["+ resourceClassPath + "]");
		logger.info("resolverAllPersistenceUnitsClass resolverAllPersistenceUnitsClass : resources Class = ["+ resources + "]");
		logger.info("resolverAllPersistenceUnitsClass resolverAllPersistenceUnitsClass : loadClassName = ["+ loadClassName + "]");
		logger.info("--");
	}
	
	/**
	 * Convert a "."-based fully qualified class name to a "/"-based resource path. 
	 * @param packageName
	 * @return String(ResourcePathName)
	 */
	private String convertClassNameToResourcePath(String packageName){
		return ClassUtils.convertClassNameToResourcePath(packageName);
	}
	
	/**
	 * Convert a "/"-based resource path to a "."-based fully qualified class name.
	 * The param className will replace all ".class" to "" , 
	 * e.g: com/my/test.class to com.my.test  
	 * @param className
	 * @return String(className)
	 */
	private String convertResourcePathToClassName(String className){
		String returnClassName = className.replaceAll(".class", "");
		returnClassName = ClassUtils.convertResourcePathToClassName(returnClassName);
		return returnClassName;
	}
	
	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
