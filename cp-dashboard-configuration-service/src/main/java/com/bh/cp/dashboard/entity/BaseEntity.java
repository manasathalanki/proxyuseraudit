package com.bh.cp.dashboard.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;

import com.bh.cp.dashboard.constants.DashboardConstants;
import com.bh.cp.dashboard.util.SimpleMapper;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.transaction.Transactional;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected transient Map<Object, Object> preMap = new HashMap<>();

	public Map<Object, Object> getPreMap() {
		return preMap;
	}

	public Map<Object, Object> getPostMap() {
		return postMap;
	}

	@Transactional
	public String getTableName() {
		return this.getClass().getSimpleName().replace("([a-z]+)([A-Z]+)", "$1\\_$2").toLowerCase();
	}

	protected transient Map<Object, Object> postMap = new HashMap<>();

	@PostLoad
	private void onPostLoad() {
		this.preMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}

	@PrePersist
	public void onPrePersist() {
		this.preMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}

	@PostPersist
	public void onPostPersist() {
		this.postMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
		this.postMap.put(DashboardConstants.ACTION_DATE, Timestamp.from(Clock.systemUTC().instant()));
		this.postMap.put(DashboardConstants.USER_ACTION, "CREATE");
	}

	@PreUpdate
	public void onPreUpdate() {
		this.preMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}

	@PostUpdate
	public void onPostUpdate() {
		this.postMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
		this.postMap.put(DashboardConstants.ACTION_DATE, Timestamp.from(Clock.systemUTC().instant()));
		this.postMap.put(DashboardConstants.USER_ACTION, "UPDATE");
	}

	@PreRemove
	public void onPreRemove() {
		this.preMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}

	@PostRemove
	public void onPostRemove() {
		this.postMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
		this.postMap.put(DashboardConstants.ACTION_DATE, Timestamp.from(Clock.systemUTC().instant()));
		this.postMap.put(DashboardConstants.USER_ACTION, "DELETE");
	}

	@PreFilter(value = "id")
	public void onPreFilter() {
		this.preMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}

	@PostFilter(value = "id")
	public void onPostFilter() {
		this.postMap = SimpleMapper.getMap(this, DashboardConstants.PERMAP, DashboardConstants.POSTMAP);
	}
}
