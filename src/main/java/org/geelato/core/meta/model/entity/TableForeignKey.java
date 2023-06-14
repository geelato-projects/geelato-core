package org.geelato.core.meta.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author diabl
 */
public class TableForeignKey implements Serializable {
    private String tableName;
    private String constraintType;
    private String constraintName;
    private String referencedTableName;
    private String referencedColumnName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }


    public static List<TableForeignKey> buildTableForeignKeys(List<Map<String, Object>> mapList) {
        List<TableForeignKey> tableForeignKeys = new ArrayList<>();
        if (mapList != null && !mapList.isEmpty()) {
            for (Map<String, Object> map : mapList) {
                tableForeignKeys.add(TableForeignKey.buildTableForeignKey(map));
            }
        }
        return tableForeignKeys;
    }

    public static TableForeignKey buildTableForeignKey(Map<String, Object> map) {
        TableForeignKey key = new TableForeignKey();
        key.setTableName(map.get("TABLE_NAME") == null ? null : map.get("TABLE_NAME").toString());
        key.setConstraintType(map.get("CONSTRAINT_TYPE") == null ? null : map.get("CONSTRAINT_TYPE").toString());
        key.setConstraintName(map.get("CONSTRAINT_NAME") == null ? null : map.get("CONSTRAINT_NAME").toString());
        key.setReferencedTableName(map.get("REFERENCED_TABLE_NAME") == null ? null : map.get("REFERENCED_TABLE_NAME").toString());
        key.setReferencedColumnName(map.get("REFERENCED_COLUMN_NAME") == null ? null : map.get("REFERENCED_COLUMN_NAME").toString());

        return key;
    }
}
