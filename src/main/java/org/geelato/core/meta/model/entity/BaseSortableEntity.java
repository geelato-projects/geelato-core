package org.geelato.core.meta.model.entity;


import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

/**
 * 可排序的基础实体
 */
public class BaseSortableEntity extends BaseEntity implements EntitySortable {

    protected long seq;


    @Title(title = "次序")
    @Col(name = "seq")
    @Override
    public long getSeq() {
        return seq;
    }

    @Override
    public void setSeq(long seq) {
        this.seq = seq;
    }

}
