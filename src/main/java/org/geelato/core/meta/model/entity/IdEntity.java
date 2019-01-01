package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Id;
import org.geelato.core.meta.annotation.Title;

import java.io.Serializable;

/**
 * @author geemeta
 */
//@MappedSuperclass
public abstract class IdEntity implements Serializable {

//    private static final long serialVersionUID = 1L;

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
