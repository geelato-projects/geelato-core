package org.geelato.core.meta.model.entity;


import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

/**
 * 可排序的基础实体
 */
public class BaseSortableEntity extends BaseEntity implements EntitySortable {

    protected long seqNo;

    @Title(title = "次序")
    @Col(name = "seq_no")
    @Override
    public long getSeqNo() {
        return seqNo;
    }

    @Override
    public void setSeqNo(long seqNo) {
        this.seqNo = seqNo;
    }

}
