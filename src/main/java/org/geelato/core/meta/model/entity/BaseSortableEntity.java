package org.geelato.core.meta.model.entity;


import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

/**
 * 可排序的基础实体
 */
public class BaseSortableEntity extends BaseEntity {

    protected long seq;

    @Title(title = "次序")
    @Col(name = "seq")
    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
