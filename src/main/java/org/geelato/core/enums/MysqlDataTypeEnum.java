package org.geelato.core.enums;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author diabl
 * @description: 数据库，数据类型
 * @date 2023/6/21 18:46
 */
public enum MysqlDataTypeEnum {
    CHAR,
    VARCHAR,
    TINYTEXT,
    TEXT,
    MEDIUMTEXT,
    LONGTEXT,

    BIT,

    TINYINT,
    SMALLINT,
    MEDIUMINT,
    INT,
    INTEGER,
    BIGINT,
    FLOAT,
    DOUBLE,
    DECIMAL,

    YEAR,
    DATE,
    TIME,
    DATETIME,
    TIMESTAMP,

    ENUM;

    /**
     * 获取枚举值
     *
     * @param type
     * @return
     */
    public static MysqlDataTypeEnum getEnum(String type) {
        if (Strings.isNotBlank(type)) {
            for (MysqlDataTypeEnum value : MysqlDataTypeEnum.values()) {
                if (value.toString().equals(type.toUpperCase(Locale.ENGLISH))) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * 获取，char、varchar
     *
     * @return
     */
    public static List<String> getChars() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.CHAR,
                MysqlDataTypeEnum.VARCHAR};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取text，tinytext、text、mediumtext、longtext
     *
     * @return
     */
    public static List<String> getTexts() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.TINYTEXT,
                MysqlDataTypeEnum.TEXT,
                MysqlDataTypeEnum.MEDIUMTEXT,
                MysqlDataTypeEnum.LONGTEXT};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取 字符串类型，char、varchar、tinytext、text、mediumtext、longtext
     *
     * @return
     */
    public static List<String> getStrings() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.CHAR,
                MysqlDataTypeEnum.VARCHAR,
                MysqlDataTypeEnum.TINYTEXT,
                MysqlDataTypeEnum.TEXT,
                MysqlDataTypeEnum.MEDIUMTEXT,
                MysqlDataTypeEnum.LONGTEXT};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取 布尔值类型，bit
     *
     * @return
     */
    public static List<String> getBooleans() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.BIT};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取 时间类型，year、date、time、datetime、timestamp
     *
     * @return
     */
    public static List<String> getDates() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.YEAR,
                MysqlDataTypeEnum.DATE,
                MysqlDataTypeEnum.TIME,
                MysqlDataTypeEnum.DATETIME,
                MysqlDataTypeEnum.TIMESTAMP};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取 整数类型
     *
     * @return
     */
    public static List<String> getIntegers() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.TINYINT,
                MysqlDataTypeEnum.SMALLINT,
                MysqlDataTypeEnum.MEDIUMINT,
                MysqlDataTypeEnum.INT,
                MysqlDataTypeEnum.INTEGER,
                MysqlDataTypeEnum.BIGINT};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取，浮点数类型
     *
     * @return
     */
    public static List<String> getDecimals() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.FLOAT,
                MysqlDataTypeEnum.DOUBLE,
                MysqlDataTypeEnum.DECIMAL};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    /**
     * 获取 数值类型
     *
     * @return
     */
    public static List<String> getNumbers() {
        MysqlDataTypeEnum[] typeEnums = new MysqlDataTypeEnum[]{MysqlDataTypeEnum.TINYINT,
                MysqlDataTypeEnum.SMALLINT,
                MysqlDataTypeEnum.MEDIUMINT,
                MysqlDataTypeEnum.INT,
                MysqlDataTypeEnum.INTEGER,
                MysqlDataTypeEnum.BIGINT,
                MysqlDataTypeEnum.FLOAT,
                MysqlDataTypeEnum.DOUBLE,
                MysqlDataTypeEnum.DECIMAL};
        return MysqlDataTypeEnum.getNames(typeEnums);
    }

    private static List<String> getNames(MysqlDataTypeEnum[] typeEnums) {
        List<String> typeNames = new ArrayList<>();
        for (MysqlDataTypeEnum typeEnum : typeEnums) {
            typeNames.add(typeEnum.name());
        }

        return typeNames;
    }
}
