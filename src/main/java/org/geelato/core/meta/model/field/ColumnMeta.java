package org.geelato.core.meta.model.field;

import org.apache.logging.log4j.util.Strings;
import org.geelato.core.meta.annotation.Col;
import org.geelato.core.meta.annotation.DictDataSrc;
import org.geelato.core.meta.annotation.Entity;
import org.geelato.core.meta.annotation.Title;
import org.geelato.core.meta.model.entity.BaseSortableEntity;
import org.geelato.core.meta.model.entity.EntityEnableAble;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author geemeta
 * <p>
 * 抽象数库中的元数据字段属性、并加入实体属性建立联系
 */
@Title(title = "字段信息")
@Entity(name = "platform_dev_column")
public class ColumnMeta extends BaseSortableEntity implements EntityEnableAble, Serializable {

    //******--以下为元数据管理专用辅助字段
    // 实体属性中文
    private String title = "";
    private String abstractColumnExpressions;
    // 实体属性名称
    private String fieldName = "";
    //******--以上为元数据管理专用辅助字段

    private String tableId;
    private String tableSchema;
    private String tableName;
    private String tableCatalog;
    //COLUMN_NAME
    private String name = "";
    //COLUMN_COMMENT
    private String comment = "";
    //ORDINAL_POSITION
    private int ordinalPosition = 0;
    //COLUMN_DEFAULT
    private String defaultValue = null;
    //COLUMN_TYPE  --varchar(100)
    private String type;
    //COLUMN_KEY,-- PRI
    private boolean key;

    //isNullable
    private boolean nullable = true;

    private String dataType = "";

    private String extra;

    private boolean autoIncrement;
    private boolean uniqued = false;

    //CHARACTER_MAXIMUM_LENGTH
    private long charMaxLength = 64;//默认长度
    //NUMERIC_PRECISION
    private int numericPrecision = 19; //默认长度
    //NUMERIC_SCALE
    private int numericScale = 0;

    //MySQL的information_schema.column中没有该字段，该信息体现在type字段中，numericPrecision无符号比有符号长1
    private boolean numericSigned = true; //是否有符号，默认有，若无符号，则需在type中增加：unsigned
    //DATETIME_PRECISION
    private int datetimePrecision = 0; //datetime 长度


    //`DATETIME_PRECISION` bigint(21) unsigned DEFAULT NULL,
    // private int datetime_precision;,
    //`CHARACTER_OCTET_LENGTH` bigint(21) unsigned DEFAULT NULL,
    //----------------
    private int enableStatus;
    private int linked;
    private String description;

    //1-外表字段，默认0
    private boolean isRefColumn;
    //isRefColumn为true时，需要通过本表引用字段
    private String refLocalCol;
    //外表字段名称
    private String refColName;
    //外表表名
    private String refTables;
    private boolean abstractColumn;

    /**
     * @return e.g. sum(columnName) as aliasColumnName
     */
    public String getAbstractColumnExpressions() {
        return abstractColumnExpressions;
    }

    public void setAbstractColumnExpressions(String abstractColumnExpressions) {
        this.abstractColumnExpressions = abstractColumnExpressions;
    }

    @Title(title = "表ID")
    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    @Col(name = "table_schema")
    @Title(title = "数据库名", description = "即table_schema")
    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    @Col(name = "table_name")
    @Title(title = "表名")
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Col(name = "table_catalog")
    @Title(title = "表目录", description = "如：def")
    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    @Col(name = "title")
    @Title(title = "中文名")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Col(name = "field_name")
    @Title(title = "列名")
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Col(name = "column_name")
    @Title(title = "列名")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Col(name = "column_comment")
    @Title(title = "备注")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Col(name = "ordinal_position")
    @Title(title = "次序")
    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    @Col(name = "column_default")
    @Title(title = "默认值", description = "auto_increment、null、无默认值、current_timestamp、on save current_timestamp、custom")
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Col(name = "column_type")
    @Title(title = "类型")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Col(name = "column_key")
    @Title(title = "列键")
    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }


    @DictDataSrc(group = "YES_OR_NO")
    @Col(name = "is_nullable", nullable = false)
    @Title(title = "可空")
    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @DictDataSrc(group = "DATA_TYPE")
    @Col(name = "data_type")
    @Title(title = "数据类型", description = "BIT|VARCHAR|TEXT|LONGTEXT|TINYINT|INT|BIGINT|DECIMAL|YEAR|DATE|TIME|DATETIME|TIMESTAMP")
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Col(name = "extra")
    @Title(title = "特别", description = "value like auto_increment")
    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Col(name = "auto_increment")
    @Title(title = "自动递增")
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @DictDataSrc(group = "YES_OR_NO")
    @Col(name = "is_unique", nullable = false)
    @Title(title = "唯一约束")
    public boolean isUniqued() {
        return uniqued;
    }

    public void setUniqued(boolean uniqued) {
        this.uniqued = uniqued;
    }


    @Col(name = "character_maxinum_length")
    @Title(title = "长度")
    public long getCharMaxLength() {
        return charMaxLength;
    }

    public void setCharMaxLength(long charMaxLength) {
        this.charMaxLength = charMaxLength;
    }

    @Col(name = "numeric_precision")
    @Title(title = "整数位")
    public int getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(int numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    @Col(name = "numeric_scale")
    @Title(title = "小数位")
    public int getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(int numericScale) {
        this.numericScale = numericScale;
    }

    @Col(name = "numeric_signed")
    @Title(title = "是否有符号")
    public boolean isNumericSigned() {
        return numericSigned;
    }

    public void setNumericSigned(boolean numericSigned) {
        this.numericSigned = numericSigned;
    }

    @Col(name = "datetime_precision")
    @Title(title = "日期长度")
    public int getDatetimePrecision() {
        return datetimePrecision;
    }

    public void setDatetimePrecision(int datetimePrecision) {
        this.datetimePrecision = datetimePrecision;
    }

    @Title(title = "启用状态", description = "1表示启用、0表示未启用")
    @Col(name = "enable_status", nullable = false, dataType = "tinyint", numericPrecision = 1)
    @Override
    public int getEnableStatus() {
        return this.enableStatus;
    }

    @Override
    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    @Col(name = "linked")
    @Title(title = "链接")
    public int getLinked() {
        return linked;
    }

    public void setLinked(int linked) {
        this.linked = linked;
    }

    @Col(name = "description")
    @Title(title = "描述")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Col(name = "is_foreign_column")
    @Title(title = "外表字段")
    public boolean getIsRefColumn() {
        return isRefColumn;
    }

    public void setIsRefColumn(boolean isRefColumn) {
        this.isRefColumn = isRefColumn;
    }

    @Col(name = "ref_local_col")
    @Title(title = "本表引用字段", description = "isRefColumn为true时有效")
    public String getRefLocalCol() {
        return refLocalCol;
    }

    public void setRefLocalCol(String refLocalCol) {
        this.refLocalCol = refLocalCol;
    }

    @Col(name = "foreign_table")
    @Title(title = "外表表名", description = "多层关联外表用逗号隔开")
    public String getRefTables() {
        return refTables;
    }

    public void setRefTables(String refTables) {
        this.refTables = refTables;
    }

    @Col(name = "foreign_col_name")
    @Title(title = "外表字段名称", description = "命名规则：[表名]+[.]+[表字段]")
    public String getRefColName() {
        return refColName;
    }

    public void setRefColName(String refColName) {
        this.refColName = refColName;
    }

    /**
     * (select columnName from t2) as abstractColumn
     * sum(columnName) as abstractColumn
     *
     * @return 是否为计算出来的列，即非物理列
     */
    public boolean isAbstractColumn() {
        return StringUtils.hasText(abstractColumnExpressions);
    }

    public void setAbstractColumn(boolean abstractColumn) {
        this.abstractColumn = abstractColumn;
    }

    /**
     * TODO 改由TypeConvert提从转换
     * 应在所有属性都设置完成之后，依据当前的属性值生成,例如：
     * bigint(21) unsigned
     * varchar(64)
     * datetime(2)
     */
    @Override
    public void afterSet() {
        // 更加数据类型dataType 设置 type
        if (Strings.isNotBlank(dataType)) {
            dataType = dataType.toUpperCase(Locale.ENGLISH);
            String columnType = null;
            if (Arrays.asList(new String[]{"BIT"}).contains(dataType)) {
                setCharMaxLength(Long.parseLong("1"));
                columnType = dataType + "(" + charMaxLength + ")";
            } else if (Arrays.asList(new String[]{"VARCHAR"}).contains(dataType)) {
                columnType = dataType + "(" + charMaxLength + ")";
            } else if (Arrays.asList(new String[]{"TEXT"}).contains(dataType)) {
                setCharMaxLength(Long.parseLong("65535"));
                setDefaultValue(null);
                columnType = dataType + "(" + charMaxLength + ")";
            } else if (Arrays.asList(new String[]{"LONGTEXT"}).contains(dataType)) {
                setCharMaxLength(Long.parseLong("4294967295"));
                setDefaultValue(null);
                columnType = dataType;
            } else if (Arrays.asList(new String[]{"TINYINT", "INT", "BIGINT"}).contains(dataType)) {
                setCharMaxLength(isNumericSigned() ? numericPrecision : (numericPrecision + 1));
                columnType = dataType + (isNumericSigned() ? ("(" + numericPrecision + ")") : ("(" + (numericPrecision + 1) + ") unsigned"));
            } else if (Arrays.asList(new String[]{"DECIMAL"}).contains(dataType)) {
                setCharMaxLength(numericPrecision + numericScale);
                columnType = dataType + "(" + numericPrecision + "," + numericScale + ")";
            } else if (Arrays.asList(new String[]{"YEAR", "DATE", "TIME", "DATETIME", "TIMESTAMP", "ENUM"}).contains(dataType)) {
                columnType = dataType;
            } else if (Arrays.asList(new String[]{"ENUM"}).contains(dataType)) {
                columnType = dataType;
            } else {
                columnType = dataType;
            }
            setDataType(dataType);
            setType(columnType);
            // 设置额外值
            List<String> extras = new ArrayList<String>();
            if (isUniqued()) {
                extras.add("unique");
            }
            if (isAutoIncrement()) {
                extras.add("auto_increment");
            }
            if (isNumericSigned()) {
                extras.add("unsigned");
            }
            setExtra(String.join(",", extras));
        }
    }
}
