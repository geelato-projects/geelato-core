package org.geelato.core.meta.model.field;

import org.geelato.core.meta.annotation.*;
import org.geelato.core.meta.model.entity.BaseSortableEntity;
import org.geelato.core.meta.model.entity.EntityEnableAble;
import org.springframework.util.StringUtils;

import java.io.Serializable;

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
    private String key;

    //isNullable
    private boolean nullable = true;

    private String dataType = "";

    private String extra;

    private boolean autoIncrement;
    private boolean unique;

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
    private int enabled;
    private int linked;
    private String description;


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
        if (type != null && type.indexOf("unsigned") != -1)
            setNumericSigned(false);
        else
            setNumericSigned(true);
    }

    @Col(name = "column_key")
    @Title(title = "列键")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
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
    @Title(title = "数据类型", description = "tinyint|int|bigint|varchar|datetime|date|time|timestamp|text|longText")
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
        if ("auto_increment".equalsIgnoreCase(this.extra)) autoIncrement = true;
        else autoIncrement = false;
    }

    @Transient
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Transient
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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

    /**
     * 注：数据库中没有该字段
     *
     * @return 是否小数位
     */
    @Transient
    @Title(title = "小数位")
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
    @Col(name = "enabled", nullable = false, dataType = "tinyint", numericPrecision = 1)
    @Override
    public int getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(int enabled) {
        this.enabled = enabled;
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


    /**
     * (select columnName from t2) as abstractColumn
     * sum(columnName) as abstractColumn
     *
     * @return 是否为计算出来的列，即非物理列
     */
    public boolean isAbstractColumn() {
        return StringUtils.hasText(abstractColumnExpressions);
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
        if (dataType == null) return;
        String t = null;
        if ("int|bigint|tinyint".indexOf(dataType) != -1)
            if (isNumericSigned()) {
                t = dataType + "(" + (numericPrecision) + ")";
            } else {
                t = dataType + "(" + (numericPrecision + 1) + ") unsigned";
            }
        else if ("varchar".indexOf(dataType) != -1) {
            t = dataType + "(" + charMaxLength + ")";
        } else if ("decimal".indexOf(dataType) != -1) {
            t = dataType + "(" + numericPrecision + "," + numericScale + ")";
        } else if ("datetime".indexOf(dataType) != -1) {
            if (datetimePrecision > 0)
                t = dataType + "(" + numericPrecision + ")";
            else
                t = dataType;
        } else if ("enum".indexOf(dataType) != -1) {
            //TODO enum('N','Y')
            t = "enum";
        } else {
            //
            t = dataType;
        }
        setType(t);
    }
}
