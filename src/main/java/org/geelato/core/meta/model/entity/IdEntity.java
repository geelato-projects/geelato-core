package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Id;
import org.geelato.core.meta.annotation.Title;

/**
 * @author geemeta
 */
//@MappedSuperclass
public abstract class IdEntity{
//implements Serializable
	protected Long id;

	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Title(title = "序号")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
