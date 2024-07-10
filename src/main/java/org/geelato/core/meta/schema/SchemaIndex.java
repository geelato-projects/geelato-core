package org.geelato.core.meta.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author diabl
 * @description: 数据库表的索引信息
 * SHOW INDEX FROM `table_name`;
 */
public class SchemaIndex implements Serializable {
    private String table;
    private String nonUnique;//唯一约束[0：唯一][1：普通]
    private String keyName;// 索引名称；[PRIMARY：主键]
    private String seqInIndex;
    private String columnName;// 字段名称
    private String collation;
    private String cardinality;
    private String subPart;
    private String packed;
    private String indexType;
    private String comment;
    private String indexComment;
    private String visible;
    private String expression;

    public static List<SchemaIndex> buildData(List<Map<String, Object>> list) {
        List<SchemaIndex> schemas = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (Map<String, Object> map : list) {
                schemas.add(buildData(map));
            }
        }
        return schemas;
    }

    public static SchemaIndex buildData(Map<String, Object> map) {
        SchemaIndex schema = new SchemaIndex();
        schema.setTable(map.get("TABLE") == null ? null : map.get("TABLE").toString());
        schema.setNonUnique(map.get("NON_UNIQUE") == null ? null : map.get("NON_UNIQUE").toString());
        schema.setKeyName(map.get("KEY_NAME") == null ? null : map.get("KEY_NAME").toString());
        schema.setSeqInIndex(map.get("SEQ_IN_INDEX") == null ? null : map.get("SEQ_IN_INDEX").toString());
        schema.setColumnName(map.get("COLUMN_NAME") == null ? null : map.get("COLUMN_NAME").toString());
        schema.setCollation(map.get("COLLATION") == null ? null : map.get("COLLATION").toString());
        schema.setCardinality(map.get("CARDINALITY") == null ? null : map.get("CARDINALITY").toString());
        schema.setSubPart(map.get("SUB_PART") == null ? null : map.get("SUB_PART").toString());
        schema.setPacked(map.get("PACKED") == null ? null : map.get("PACKED").toString());
        schema.setIndexType(map.get("INDEX_TYPE") == null ? null : map.get("INDEX_TYPE").toString());
        schema.setComment(map.get("COMMENT") == null ? null : map.get("COMMENT").toString());
        schema.setIndexComment(map.get("INDEX_COMMENT") == null ? null : map.get("INDEX_COMMENT").toString());
        schema.setVisible(map.get("VISIBLE") == null ? null : map.get("VISIBLE").toString());
        schema.setExpression(map.get("EXPRESSION") == null ? null : map.get("EXPRESSION").toString());

        return schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getNonUnique() {
        return nonUnique;
    }

    public void setNonUnique(String nonUnique) {
        this.nonUnique = nonUnique;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getSeqInIndex() {
        return seqInIndex;
    }

    public void setSeqInIndex(String seqInIndex) {
        this.seqInIndex = seqInIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public String getSubPart() {
        return subPart;
    }

    public void setSubPart(String subPart) {
        this.subPart = subPart;
    }

    public String getPacked() {
        return packed;
    }

    public void setPacked(String packed) {
        this.packed = packed;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIndexComment() {
        return indexComment;
    }

    public void setIndexComment(String indexComment) {
        this.indexComment = indexComment;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
