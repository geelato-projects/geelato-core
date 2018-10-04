package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

/**
 * 基础业务实体，增加了组织信息、工作流信息
 */
public class BizSortableEntity extends BizEntity {

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
