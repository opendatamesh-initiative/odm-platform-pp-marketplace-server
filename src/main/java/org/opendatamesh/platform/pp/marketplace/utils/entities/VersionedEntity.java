package org.opendatamesh.platform.pp.marketplace.utils.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
public abstract class VersionedEntity {

	@Column(name="created_at", updatable=false)
	private Timestamp createdAt;
	
	@Column(name="updated_at")
	private Timestamp updatedAt;

	/*
	 * Initialize the createdAt and updateAt date.
	 * UpdatedAt is set here to increase performance
	 * of incremental batches, so do not remove or everything will break.
	 */
	@PrePersist
	public void onCreate(){
		Timestamp date = new Timestamp(new Date().getTime());
		this.updatedAt= date;
		this.createdAt= date;
	}

	@PreUpdate
	public void onUpdate(){
		this.updatedAt=new Timestamp(new Date().getTime());
	}
	
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Transient
	public void setCreatedAt(Date date){
		if(date!=null){
			this.createdAt = new Timestamp(date.getTime());
		}else{
			this.createdAt = null;
		}
	}

	@Transient
	public void setUpdatedAt(Date date){
		if(date != null) {
			this.updatedAt = new Timestamp(date.getTime());
		}else{
			this.updatedAt = null;
		}
	}

}