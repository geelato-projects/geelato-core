package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

import java.util.Date;

/**
 * 基础实体，默认一些人员信息、更新信息等常规字段
 */
public class BaseEntity extends IdEntity {

    private Date createAt;
    private Date updateAt;
    private Long creator;
    private Long updater;
    private int checkState;

    public BaseEntity() {
    }

    public BaseEntity(Long Id) {
        setId(id);
    }

    @Col(name = "create_at", nullable = false)
    @Title(title = "创建时间")
    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @Col(name = "update_at", nullable = false)
    @Title(title = "更新时间")
    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    @Col(name = "creator", nullable = false)
    @Title(title = "创建者")
    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    @Col(name = "updater", nullable = false)
    @Title(title = "更新者")
    public Long getUpdater() {
        return updater;
    }

    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    @Col(name = "check_state", nullable = false, dataType = "tinyint", numericPrecision = 2)
    @Title(title = "审核状态", description = "用于如数据审核状态，99：未审核，0：无需审核，2：已审核，默认为0，为无需工作流审核的数据。")
    public int getCheckState() {
        return checkState;
    }

    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }

    /***
     * 其它属性设置之后，调用。可用于通用的增删改查功能中，特别字段的生成
     */
    public void afterSet() {

    }
}
