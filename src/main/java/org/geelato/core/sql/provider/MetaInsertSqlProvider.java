package org.geelato.core.sql.provider;

import org.geelato.core.gql.TypeConverter;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.gql.parser.SaveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author geemeta
 */
@Component
public class MetaInsertSqlProvider extends MetaBaseSqlProvider<SaveCommand> {
    private static Logger logger = LoggerFactory.getLogger(MetaInsertSqlProvider.class);

//    @Override
//    protected Object[] buildParams(SaveCommand command) {
//        Assert.notNull(command.getValueMap(), "必须有指的插入字段。");
//
//        Object[] objects = new Object[command.getValueMap().size()];
//        int i = 0;
//        //值部分
//        for (Map.Entry<String, Object> entry : command.getValueMap().entrySet()) {
//            // 当entry.getValue() == null时，objects[i]，未设置值，导致后续执行insert语句时，因值为null，不知数据类型，尝试取元数据出错
//            // 故这里默认为空字符串值
////            objects[i] = entry.getValue() == null ? "" : entry.getValue();
//            objects[i] = entry.getValue();
//            i++;
//        }
//        return objects;
//    }
    @Override
    protected Object[] buildParams(SaveCommand command) {
        Assert.notNull(command.getValueMap(), "必须有指的插入字段。");

        Object[] objects = new Object[command.getValueMap().size()];
        int i = 0;
        //值部分
        for (Map.Entry<String, Object> entry : command.getValueMap().entrySet()) {
            // 当entry.getValue() == null时，objects[i]，未设置值，导致后续执行insert语句时，因值为null，不知数据类型，尝试取元数据出错
            // 故这里默认为空字符串值
//            objects[i] = entry.getValue() == null ? "" : entry.getValue();
            objects[i] = entry.getValue();
            i++;
        }
        return objects;
    }

    @Override
    protected int[] buildTypes(SaveCommand command) {
        Assert.notNull(command.getValueMap(), "必须有指的插入字段。");
        EntityMeta em = getEntityMeta(command);
        int[] types = new int[command.getValueMap().size()];
        int i = 0;
        //值部分
        for (Map.Entry<String, Object> entry : command.getValueMap().entrySet()) {
            types[i] = TypeConverter.toSqlType(em.getFieldMeta(entry.getKey()).getColumn().getDataType());
            i++;
        }
        return types;
    }

    /**
     * INSERT INTO 表名称 VALUES (值1, 值2,....)
     * INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
     *
     * @param command
     * @return
     */
    protected String buildOneSql(SaveCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        sb.append("insert into ");
        sb.append(md.getTableName());
        sb.append("(");
        buildFields(sb, md, command.getFields());
        sb.append(")");
        sb.append(" values ");
        sb.append("(");
        buildValues(sb, md, command.getFields());
        sb.append(")");
        return sb.toString();
    }

    protected void buildFields(StringBuilder sb, EntityMeta md, String[] fields) {
        //重命名查询的结果列表为实体字段名
        for (String fieldName : fields) {
            sb.append(md.getColumnName(fieldName));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
    }

    protected void buildValues(StringBuilder sb, EntityMeta md, String[] fields) {
        //重命名查询的结果列表为实体字段名
        for (String fieldName : fields) {
            sb.append("?");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
    }

}
