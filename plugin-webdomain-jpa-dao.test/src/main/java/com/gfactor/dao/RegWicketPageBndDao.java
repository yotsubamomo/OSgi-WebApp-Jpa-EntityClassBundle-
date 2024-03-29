package com.gfactor.dao;

import java.util.List;

import com.gfactor.jpa.persistence.test2.Bndpageinfo;


public interface RegWicketPageBndDao {
	
	public Bndpageinfo findByEntry(Bndpageinfo bndpageinfo);
	public List<Bndpageinfo> findBndPageInfoList(String bndName,String bndVer,String entry_point);
	public Bndpageinfo getBndPageInfo(String bndName,String bndVer,String entry_point);
	public void saveBndPageInfo(Bndpageinfo bndpageinfo);
	public Bndpageinfo update(Bndpageinfo bndpageinfo);
	public void delete(Bndpageinfo bndpageinfo);
	public void deleteExpiredBndPageInfo(Bndpageinfo bndpageinfo);
}
