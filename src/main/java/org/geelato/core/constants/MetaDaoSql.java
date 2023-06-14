package org.geelato.core.constants;

/**
 * @author diabl
 */
public class MetaDaoSql {
    /**
     * 查询 platform_dev_table
     */
    public static final String SQL_TABLE_LIST = String.format("select * from platform_dev_table where del_status =%d", ColumnDefault.DEL_STATUS_VALUE);
    /**
     * 查询 platform_dev_column
     */
    public static final String SQL_COLUMN_LIST_BY_TABLE = String.format("select * from platform_dev_column where del_status=%d", ColumnDefault.DEL_STATUS_VALUE);
    /**
     * 查询 platform_dev_table_foreign
     */
    public static final String SQL_FOREIGN_LIST_BY_TABLE = String.format("select * from platform_dev_table_foreign where del_status=%d", ColumnDefault.DEL_STATUS_VALUE);
    /**
     * 查询表单（%s）中非主键的唯一约束索引
     */
    public static final String SQL_INDEXES_NO_PRIMARY = "SHOW INDEXES FROM `%s` WHERE NON_UNIQUE = 0 AND KEY_NAME != 'PRIMARY';";
    public static final String SQL_INDEXES = "SHOW INDEXES FROM `%s`";
    /**
     * 查询表单（%s）中所有外键
     */
    public static final String SQL_FOREIGN_KEY = "SELECT i.TABLE_NAME, i.CONSTRAINT_TYPE, i.CONSTRAINT_NAME, k.REFERENCED_TABLE_NAME, k.REFERENCED_COLUMN_NAME FROM information_schema.TABLE_CONSTRAINTS i LEFT JOIN information_schema.KEY_COLUMN_USAGE k ON i.CONSTRAINT_NAME = k.CONSTRAINT_NAME WHERE i.CONSTRAINT_TYPE = 'FOREIGN KEY' AND i.TABLE_SCHEMA = DATABASE() AND i.TABLE_NAME = '%s';";

    public static final String SQL_TABLE_DEFAULT_VIEW = "SELECT * FROM `%s`";
}
