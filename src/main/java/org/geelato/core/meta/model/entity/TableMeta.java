package org.geelato.core.meta.model.entity;

import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.annotation.Title;

/**
 * @author geemeta
 */
@Entity(name = "dev_table_config")
public class TableMeta extends BaseEntity {
    private String title;
    private String tableSchema;
    private String tableName;
    private String tableType;
    private String tableComment;
    private boolean activity;
    private boolean linked;
    private String description;

    public TableMeta(){}

    public TableMeta(String tableName, String title, String description) {
        this.tableName = tableName;
        this.title = title;
        this.description = description;
    }

    @Col(name="table_name")
    @Title(title = "表名",description = "与数据库中的表名一致")
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Col(name="table_schema")
    @Title(title = "数据库名")
    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Col(name="table_type")
    @Title(title = "表格类型",description = "entity or view")
    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    @Col(name="table_comment")
    @Title(title = "备注")
    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }


    @Title(title = "名称(中文)")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Title(title = "启用")
    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    @Title(title = "已链接")
    public boolean getLinked() {
        return linked;
    }

    public void setLinked(boolean linked) {
        this.linked = linked;
    }

    @Title(title = "补充描述")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
