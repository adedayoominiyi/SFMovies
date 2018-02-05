package com.ominiyi.model;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Setting {
	@Id private Long id;
	@Index private String key;
	private String value;
	
	static {
		ObjectifyService.register(Setting.class);
	}
	
	public Setting() {
		super();
	}
	
	public Setting(String key, String value) {
		super();
		
		this.key = key;
		this.value = value;
	}
	
	public Long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void save() {
		ofy().save().entity(this).now();
	}
	public static Setting findSetting(String key) {
		Setting setting = ofy().load().type(Setting.class).filter("key", key).first().now();
		
		return setting;
	}
}
