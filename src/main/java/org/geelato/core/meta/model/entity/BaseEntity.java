package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Title;

import java.util.Date;

/**
 * @author geemeta
 *
 */
//@MappedSuperclass
//@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity extends IdEntity {
    private Date createAt;
    private Date updateAt;
    private Long creator;
    private Long updater;
    private int checkStatus;
    private String businessUnit;
    private String dept;

    private String bid;//工作流实例id

//    protected String uuid;


    public BaseEntity() {
    }

    public BaseEntity(Long Id) {
        setId(id);
    }

//    @CN(title = "序号")
//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }

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


    @Col(name = "check_status", nullable = false)
    @Title(title = "审核状态", description = "用于如数据审核状态，-1：未审核，0：无需审核，1：已审核，默认为0，为无需工作流审核的数据。")
    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    /**
     * business_unit可用于分公司、或事业部，主要用于数据权限的区分，如分公司可能看自己分公司的数据
     * 存放分公司的编码信息
     *
     * @return 单位值
     */
    @Col(name = "business_unit", nullable = true, charMaxlength = 8)
    @Title(title = "单位")
    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
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
