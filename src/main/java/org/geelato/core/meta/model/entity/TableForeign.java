package org.geelato.core.meta.model.entity;

import org.geelato.core.constants.ColumnDefault;
import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.annotation.Title;

/**
 * @Description 实体外键关系
 * @Date 2020/3/20 14:42 by liuwq
 */
@Title(title = "实体外键关系")
@Entity(name = "platform_dev_table_foreign")
public class TableForeign extends BaseSortableEntity implements EntityEnableAble {

    private String mainTable;

    private String mainTableCol;

    private String foreignTable;

    private String foreignTableCol;

    private int enableStatus = ColumnDefault.ENABLE_STATUS_VALUE;

    private String description;

    public TableForeign() {
    }

    @Col(name = "main_table")
    @Title(title = "主表表名")
    public String getMainTable() {
        return mainTable;
    }

    public void setMainTable(String mainTable) {
        this.mainTable = mainTable;
    }

    @Col(name = "main_table_col")
    @Title(title = "主表表名字段")
    public String getMainTableCol() {
        return mainTableCol;
    }

    public void setMainTableCol(String mainTableCol) {
        this.mainTableCol = mainTableCol;
    }

    @Col(name = "foreign_table")
    @Title(title = "外键关联表表名")
    public String getForeignTable() {
        return foreignTable;
    }

    public void setForeignTable(String foreignTable) {
        this.foreignTable = foreignTable;
    }

    @Col(name = "foreign_table_col")
    @Title(title = "外键关联表字段")
    public String getForeignTableCol() {
        return foreignTableCol;
    }

    public void setForeignTableCol(String foreignTableCol) {
        this.foreignTableCol = foreignTableCol;
    }

    @Override
    public int getEnableStatus() {
        return this.enableStatus;
    }

    /**
     * @param enableStatus
     */
    @Override
    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    @Col(name = "description")
    @Title(title = "描述")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
