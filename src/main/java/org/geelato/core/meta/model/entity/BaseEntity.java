package org.geelato.core.meta.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.geelato.core.constants.ColumnDefault;
import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

import java.util.Date;

/**
 * 基础实体，默认一些人员信息、更新信息等常规字段
 */
public class BaseEntity extends IdEntity {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateAt;
    private Long creator;
    private Long updater;

    // 逻辑删除的标识
    private int delStatus = ColumnDefault.DEL_STATUS_VALUE;
    // 单位Id
    private Long buId;
    // 部门Id
    private Long deptId;

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

    @Col(name = "del_status")
    @Title(title = "删除状态", description = "逻辑删除的状态，1：已删除、0：未删除")
    public int getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(int delStatus) {
        this.delStatus = delStatus;
    }


    @Col(name = "bu", nullable = true, charMaxlength = 8)
    @Title(title = "单位", description = "bu即business unit，记录（分）公司的编码信息，可用于分公司、或事业部，主要用于数据权限的区分，如分公司可看自己分公司的数据。")
    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @Col(name = "dept", nullable = true, charMaxlength = 8)
    @Title(title = "部门")
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /***
     * 其它属性设置之后，调用。可用于通用的增删改查功能中，特别字段的生成
     */
    public void afterSet() {

    }
}
