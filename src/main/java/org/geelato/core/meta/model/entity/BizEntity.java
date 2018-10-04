package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

/**
 * 基础业务实体，增加了组织信息、工作流信息
 */
public class BizEntity extends BaseEntity {

    private String bu;
    private String dept;
    // 工作流实例id
    private String bid;

    @Col(name = "bu", nullable = true, charMaxlength = 8)
    @Title(title = "单位", description = "bu即business unit，记录（分）公司的编码信息，可用于分公司、或事业部，主要用于数据权限的区分，如分公司可看自己分公司的数据。")
    public String getBu() {
        return bu;
    }

    public void setBu(String bu) {
        this.bu = bu;
    }

    @Col(name = "dept", nullable = true, charMaxlength = 8)
    @Title(title = "部门")
    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }


    /***
     * 其它属性设置之后，调用。可用于通用的增删改查功能中，特别字段的生成
     */
    public void afterSet() {

    }
}
