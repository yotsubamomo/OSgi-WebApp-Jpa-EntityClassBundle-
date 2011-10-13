package com.gfactor.jpa.persistence.test;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

 
@NamedQuery(name = "QueryByNameAndMail", 
query = "SELECT tableinfo FROM TableInfo tableinfo " +
		"WHERE tableinfo.user_name = :userName " +
		"and tableinfo.user_mail = :userMail ")


@Entity
@Table(name="tableinfo")
public class TableInfo implements Serializable{
	
	@Id 
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="user_name")
	private String user_name;
	
	@Column(name="user_desc")
	private String user_desc;
	
	@Column(name="user_mail")
	private String user_mail;
	
	public TableInfo(){
		
	}
	
	@Override
	public String toString()
	{
		return "[TableInfo id=" + id + " user_name=" + user_name + " user_desc=" + user_desc +
				" user_mail=" + user_mail + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_desc() {
		return user_desc;
	}

	public void setUser_desc(String user_desc) {
		this.user_desc = user_desc;
	}

	public String getUser_mail() {
		return user_mail;
	}

	public void setUser_mail(String user_mail) {
		this.user_mail = user_mail;
	}
	
	
	
}
