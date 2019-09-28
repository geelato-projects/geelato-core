package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.annotation.Title;

/**
 * @author geemeta
 */
@Title(title = "实体信息")
@Entity(name = "platform_dev_table")
public class TableMeta extends BaseEntity implements EntityEnableAble {
    private String title;
    private String connectId;
    private String tableName;
    private String tableType;
    private String tableComment;
    private int enabled;
    private int linked;
    private String description;

    public TableMeta() {
    }

    public TableMeta(String tableName, String title, String description) {
        this.tableName = tableName;
        this.title = title;
        this.description = description;
    }

    @Col(name = "table_name")
    @Title(title = "表名", description = "与数据库中的表名一致")
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Col(name = "connect_id")
    @Title(title = "数据库连接id")
    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String ConnectId) {
        this.connectId = ConnectId;
    }

    @Col(name = "table_type")
    @Title(title = "表格类型", description = "entity or view")
    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    @Col(name = "table_comment")
    @Title(title = "备注")
    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }


    @Col(name = "title")
    @Title(title = "名称(中文)")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Col(name = "linked")
    @Title(title = "已链接")
    public int getLinked() {
        return linked;
    }

    public void setLinked(int linked) {
        this.linked = linked;
    }

    @Col(name = "description")
    @Title(title = "补充描述")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Title(title = "启用状态", description = "1表示启用、0表示未启用")
    @Col(name = "enabled", nullable = false, dataType = "tinyint", numericPrecision = 1)
    @Override
    public int getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}
