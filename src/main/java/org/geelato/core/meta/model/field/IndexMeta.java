package org.geelato.core.meta.model.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author diabl
 */
public class IndexMeta implements Serializable {
    private String table;
    private String nonUnique;
    private String keyName;
    private String seqInIndex;
    private String columnName;
    private String collation;
    private String cardinality;
    private String subPart;
    private String packed;
    private String indexType;
    private String comment;
    private String indexComment;
    private String visible;
    private String expression;

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

    public static List<IndexMeta> buildIndexMetas(List<Map<String, Object>> mapList) {
        List<IndexMeta> indexMetas = new ArrayList<>();
        if (mapList != null && !mapList.isEmpty()) {
            for (Map<String, Object> map : mapList) {
                indexMetas.add(IndexMeta.buildIndexMeta(map));
            }
        }
        return indexMetas;
    }

    public static IndexMeta buildIndexMeta(Map<String, Object> map) {
        IndexMeta meta = new IndexMeta();
        meta.setTable(map.get("TABLE") == null ? null : map.get("TABLE").toString());
        meta.setNonUnique(map.get("NON_UNIQUE") == null ? null : map.get("NON_UNIQUE").toString());
        meta.setKeyName(map.get("KEY_NAME") == null ? null : map.get("KEY_NAME").toString());
        meta.setSeqInIndex(map.get("SEQ_IN_INDEX") == null ? null : map.get("SEQ_IN_INDEX").toString());
        meta.setColumnName(map.get("COLUMN_NAME") == null ? null : map.get("COLUMN_NAME").toString());
        meta.setCollation(map.get("COLLATION") == null ? null : map.get("COLLATION").toString());
        meta.setCardinality(map.get("CARDINALITY") == null ? null : map.get("CARDINALITY").toString());
        meta.setSubPart(map.get("SUB_PART") == null ? null : map.get("SUB_PART").toString());
        meta.setPacked(map.get("PACKED") == null ? null : map.get("PACKED").toString());
        meta.setIndexType(map.get("INDEX_TYPE") == null ? null : map.get("INDEX_TYPE").toString());
        meta.setComment(map.get("COMMENT") == null ? null : map.get("COMMENT").toString());
        meta.setIndexComment(map.get("INDEX_COMMENT") == null ? null : map.get("INDEX_COMMENT").toString());
        meta.setVisible(map.get("VISIBLE") == null ? null : map.get("VISIBLE").toString());
        meta.setExpression(map.get("EXPRESSION") == null ? null : map.get("EXPRESSION").toString());

        return meta;
    }
}
