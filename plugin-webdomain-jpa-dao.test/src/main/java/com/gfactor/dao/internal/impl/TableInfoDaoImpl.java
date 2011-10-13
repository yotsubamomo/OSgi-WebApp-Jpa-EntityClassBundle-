/**
 * 
 */
package com.gfactor.dao.internal.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.support.JpaDaoSupport;

import com.gfactor.dao.TableInfoDao;
import com.gfactor.jpa.persistence.test.TableInfo;

/**
 * @author momo
 * 
 */
public class TableInfoDaoImpl extends JpaDaoSupport implements TableInfoDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// @PersistenceUnit
	// private EntityManagerFactory entityManagerFactory;

	@PersistenceContext
	private EntityManager em;

	@Override
	public TableInfo findByEntry(TableInfo tableInfo) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public TableInfo getTableInfo(String userName, String userMail) {
		logger.info("em = "+ this.em);
		logger.info("em = "+ this.em.getEntityManagerFactory());
		
		logger.info("getTableInfo query value = " +userName+","+userMail);
		TableInfo tableinfo = null;
		 
		try{
			Query query = this.em.createNamedQuery("QueryByNameAndMail");
			query.setParameter("userName", userName);
			query.setParameter("userMail", userMail); 
			tableinfo = (TableInfo) query.getSingleResult();
		}catch (Exception e) {
			logger.error("dao getTableInfo exception = " +e);
		}
		
		logger.info("tableinfo ============> "+ tableinfo);
		return tableinfo;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveTableInfo(TableInfo tableInfo) {
		logger.info("save tableInfo " + tableInfo); 
		getJpaTemplate().persist(tableInfo);

	}

	@Override
	public TableInfo update(TableInfo tableInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(TableInfo tableInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteExpiredTableInfo(TableInfo tableInfo) {
		// TODO Auto-generated method stub

	}



	@Override
	public List<TableInfo> findTableInfoList(String userName, String userMail) {
		// TODO Auto-generated method stub
		return null;
	}
}
