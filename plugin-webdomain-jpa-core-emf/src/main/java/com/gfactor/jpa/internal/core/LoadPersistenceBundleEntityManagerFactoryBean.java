/**
 * 
 */
package com.gfactor.jpa.internal.core;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jdbc.datasource.lookup.SingleDataSourceLookup;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.persistenceunit.SmartPersistenceUnitInfo;
import org.springframework.util.ClassUtils;

import com.gfactor.jpa.internal.loader.OsgiPersistenceClassLoader;

/**
 * @author momo
 *
 */
public class LoadPersistenceBundleEntityManagerFactoryBean extends
		AbstractEntityManagerFactoryBean {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());	
	private PersistenceUnitManager persistenceUnitManager;
	private final DefaultPersistenceUnitManager internalPersistenceUnitManager = new DefaultPersistenceUnitManager();
	private PersistenceUnitInfo persistenceUnitInfo;
//	private PersistenceProvider persistenceProvider;
	private OsgiPersistenceClassLoader osgiPersistenceClassLoader;
	
	
//	public PersistenceProvider getPersistenceProvider() {
//		return persistenceProvider;
//	}
//
//	public void setPersistenceProvider(PersistenceProvider persistenceProvider) {
//		this.persistenceProvider = persistenceProvider;
//	}

	public void setOsgiPersistenceClassLoader(
			OsgiPersistenceClassLoader osgiPersistenceClassLoader) {
		this.osgiPersistenceClassLoader = osgiPersistenceClassLoader;
	}

	public void setPersistenceUnitManager(PersistenceUnitManager persistenceUnitManager) {
		this.persistenceUnitManager = persistenceUnitManager;
	}
	
	public void setPersistenceXmlLocation(String persistenceXmlLocation) {
		this.internalPersistenceUnitManager.setPersistenceXmlLocations(new String[] {persistenceXmlLocation});
	}
	
	public void setDataSource(DataSource dataSource) {
		this.internalPersistenceUnitManager.setDataSourceLookup(new SingleDataSourceLookup(dataSource));
		this.internalPersistenceUnitManager.setDefaultDataSource(dataSource);
	}
	
	public void setPersistenceUnitPostProcessors(PersistenceUnitPostProcessor[] postProcessors) {
		this.internalPersistenceUnitManager.setPersistenceUnitPostProcessors(postProcessors);
	}
	
	public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
		this.internalPersistenceUnitManager.setLoadTimeWeaver(loadTimeWeaver);
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.internalPersistenceUnitManager.setResourceLoader(resourceLoader);
	}
	
	
	/* (non-Javadoc)
	 * @see org.springframework.orm.jpa.AbstractEntityManagerFactoryBean#createNativeEntityManagerFactory()
	 */
	@Override
	protected EntityManagerFactory createNativeEntityManagerFactory()
			throws PersistenceException {
		
		logger.info("createNativeEntityManagerFactory start....");
		logger.info("chk ltw instance ..." + this.internalPersistenceUnitManager.getLoadTimeWeaver());
		PersistenceUnitManager managerToUse = this.persistenceUnitManager;
		if (this.persistenceUnitManager == null) {
			this.internalPersistenceUnitManager.afterPropertiesSet();
			managerToUse = this.internalPersistenceUnitManager;
		}
		
		logger.info("managerToUse = "+ managerToUse);
		
		this.persistenceUnitInfo = determinePersistenceUnitInfo(managerToUse);
		JpaVendorAdapter jpaVendorAdapter = getJpaVendorAdapter();
		if (jpaVendorAdapter != null && this.persistenceUnitInfo instanceof SmartPersistenceUnitInfo) {
			((SmartPersistenceUnitInfo) this.persistenceUnitInfo).setPersistenceProviderPackageName(
					jpaVendorAdapter.getPersistenceProviderRootPackage());
		}
		
		logger.info("this.persistenceUnitInfo getPersistenceUnitName = "+ this.persistenceUnitInfo.getPersistenceUnitName());

//		PersistenceProvider provider = getPersistenceProvider();
		PersistenceProvider provider = getPersistenceProvider();

		if (provider == null) {
			String providerClassName = this.persistenceUnitInfo.getPersistenceProviderClassName();
			if (providerClassName == null) {
				throw new IllegalArgumentException(
						"No PersistenceProvider specified in EntityManagerFactory configuration, " +
						"and chosen PersistenceUnitInfo does not specify a provider class name either");
			}
			Class<?> providerClass = ClassUtils.resolveClassName(providerClassName, getBeanClassLoader());
			provider = (PersistenceProvider) BeanUtils.instantiateClass(providerClass);
		}
		
		if (provider == null) {
			throw new IllegalStateException("Unable to determine persistence provider. " +
					"Please check configuration of " + getClass().getName() + "; " +
					"ideally specify the appropriate JpaVendorAdapter class for this provider.");
		}
		logger.info("provider = " + provider.toString());
		logger.info("provider.getProviderUtil = " + provider.getProviderUtil());
		logger.info("provider getClass = " + provider.getClass());		
		logger.info("provider getClassLoader= " + provider.getClass().getClassLoader());
		
		List<Class<?>> persistenceClassList = osgiPersistenceClassLoader.getPersistenceList();
		scanEntitys(persistenceClassList);
		logger.info("persistenceClassList list size = " + persistenceClassList);
		
		if (logger.isInfoEnabled()) {
			logger.info("Building JPA container EntityManagerFactory for persistence unit '" +
					this.persistenceUnitInfo.getPersistenceUnitName() + "'");
		} 
		
		this.nativeEntityManagerFactory = provider.createContainerEntityManagerFactory(this.persistenceUnitInfo, getJpaPropertyMap());
		
		postProcessEntityManagerFactory(this.nativeEntityManagerFactory, this.persistenceUnitInfo);
		logger.info("nativeEntityManagerFactory = " + nativeEntityManagerFactory);
//		EntityManager em = this.nativeEntityManagerFactory.createEntityManager();
//		logger.info("em = " + em);
		
		return this.nativeEntityManagerFactory;
	}
	
	
	private void scanEntitys(List<Class<?>> classList) {
		
		for (Class clazz : classList) {
			logger.info("class name = " + clazz.getName());
						
			persistenceUnitInfo.getManagedClassNames().add(clazz.getName());
		}
		
		List<String> managedClass = persistenceUnitInfo.getManagedClassNames();  
		logger.info("managedClass = " + managedClass);
		List<String> mappingFiles = persistenceUnitInfo.getMappingFileNames();
		logger.info("mappingFiles = " + mappingFiles);

//        String[] pgs = StringUtils.commaDelimitedListToStringArray(scanPackages);  
//        if (pgs.length > -1) {  
//            ClassPathScaner p = new ClassPathScaner();  
//            // p.addIncludeFilter(new AssignableTypeFilter(TypeFilter.class));  
//            // Set<MetadataReader> bd = p.findCandidateClasss("org.springframework");  
//            p.addIncludeFilter(new AnnotationTypeFilter(Entity.class));  
//            Set<MetadataReader> bd = p.findCandidateClasss(pgs);  
//            List<String> managedClass = persistenceUnitInfo.getManagedClassNames();  
//            for (MetadataReader b : bd) {  
//                if (!(managedClass.contains(b.getClassMetadata().getClassName()))) {  
//                    managedClass.add(b.getClassMetadata().getClassName());  
//                }  
//            }  
//        }  
    }  
	
	protected PersistenceUnitInfo determinePersistenceUnitInfo(PersistenceUnitManager persistenceUnitManager) {
		if (getPersistenceUnitName() != null) {
			return persistenceUnitManager.obtainPersistenceUnitInfo(getPersistenceUnitName());
		}
		else {
			return persistenceUnitManager.obtainDefaultPersistenceUnitInfo();
		}
	}
	
	protected void postProcessEntityManagerFactory(EntityManagerFactory emf, PersistenceUnitInfo pui) {
	}


	@Override
	public PersistenceUnitInfo getPersistenceUnitInfo() {
		return this.persistenceUnitInfo;
	}

	@Override
	public String getPersistenceUnitName() {
		if (this.persistenceUnitInfo != null) {
			return this.persistenceUnitInfo.getPersistenceUnitName();
		}
		return super.getPersistenceUnitName();
	}

	@Override
	public DataSource getDataSource() {
		if (this.persistenceUnitInfo != null) {
			return this.persistenceUnitInfo.getNonJtaDataSource();
		}
		return this.internalPersistenceUnitManager.getDefaultDataSource();
	}
	
	

}
