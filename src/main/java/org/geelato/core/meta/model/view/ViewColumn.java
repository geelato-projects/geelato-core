package org.geelato.core.meta.model.view;

import org.geelato.core.meta.model.field.ColumnMeta;

/**
 * @author diabl
 * @description: 视图字段
 * @date 2023/6/30 9:29
 */
public class ViewColumn {
    private String tableName;
    private String title;
    private String name;
    private String fieldName;
    private String type;
    private String comment;
    private Boolean key = false;
    private Boolean nullable = false;
    private Long charMaxLength;
    private Integer precision;
    private Integer scale;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getKey() {
        return key;
    }

    public void setKey(Boolean key) {
        this.key = key;
    }

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Long getCharMaxLength() {
        return charMaxLength;
    }

    public void setCharMaxLength(Long charMaxLength) {
        this.charMaxLength = charMaxLength;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * 将表格字段转为视图字段
     *
     * @param meta
     * @return
     */
    public static ViewColumn fromColumnMeta(ColumnMeta meta) {
        ViewColumn column = new ViewColumn();
        if (meta != null) {
            column.setTableName(meta.getTableName());
            column.setTitle(meta.getTitle());
            column.setName(meta.getName());
            column.setFieldName(meta.getFieldName());
            column.setType(meta.getDataType());
            column.setComment(meta.getComment());
            column.setKey(meta.isKey());
            column.setNullable(meta.isNullable());
            column.setCharMaxLength(meta.getCharMaxLength());
            column.setPrecision(meta.getNumericPrecision());
            column.setScale(meta.getNumericScale());
        }

        return column;
    }
}
