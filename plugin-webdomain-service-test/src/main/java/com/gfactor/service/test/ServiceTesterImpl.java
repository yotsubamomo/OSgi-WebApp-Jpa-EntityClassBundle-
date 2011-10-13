/**
 * 
 */
package com.gfactor.service.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gfactor.dao.RegWicketPageBndDao;
import com.gfactor.dao.TableInfoDao;
import com.gfactor.jpa.persistence.test.TableInfo;

/**
 * @author momo
 *
 */
public class ServiceTesterImpl implements ServiceTester {
	@Autowired
	private RegWicketPageBndDao regWicketPageBndDao;
	
	@Autowired
	private TableInfoDao tableInfoDao;
	
	/* (non-Javadoc)
	 * @see com.gfactor.service.test.ServiceTester#Test()
	 */
	@Override
	@Transactional
	public void Test() {
		// TODO Auto-generated method stub
		 regWicketPageBndDao.findBndPageInfoList("test2", "1.0.1", "mains");
		 tableInfoDao.findTableInfoList("momo","momo");
		 TableInfo tableinfo = new TableInfo();
		 tableinfo.setId(2);
		 tableinfo.setUser_desc("test for transaction");
		 tableinfo.setUser_mail("yotsuba@test.com.tw");
		 tableinfo.setUser_name("yotsuba");
		 tableInfoDao.saveTableInfo(tableinfo);
	}

}
