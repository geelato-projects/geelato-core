package org.geelato.core.sql.provider;

import org.geelato.core.gql.TypeConverter;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.gql.parser.FilterGroup;
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
public class MetaUpdateSqlProvider extends MetaBaseSqlProvider<SaveCommand> {
    private static Logger logger = LoggerFactory.getLogger(MetaUpdateSqlProvider.class);

    @Override
    protected Object[] buildParams(SaveCommand command) {
        Assert.notNull(command.getValueMap(), "必须有指的更新字段。");

        //条件部分
        Object[] whereObjects = buildWhereParams(command);

        Object[] objects = new Object[whereObjects.length + command.getValueMap().size()];
        int i = 0;
        //值部分
        for (Map.Entry<String, Object> entry : command.getValueMap().entrySet()) {
            //1、先加值部分
            objects[i] = entry.getValue();
            i++;
        }
        //2、再加条件部分
        for (Object object : whereObjects) {
            objects[i] = object;
            i++;
        }
        return objects;
    }

    @Override
    protected int[] buildTypes(SaveCommand command) {
        Assert.notNull(command.getValueMap(), "必须有指的更新字段。");
        EntityMeta em = getEntityMeta(command);
        //条件部分
        int[] whereTypes = buildWhereTypes(command);

        int[] types = new int[whereTypes.length + command.getValueMap().size()];
        int i = 0;
        //值部分
        for (Map.Entry<String, Object> entry : command.getValueMap().entrySet()) {
            //1、先加值部分
            types[i] = TypeConverter.toSqlType(em.getFieldMeta(entry.getKey()).getColumn().getDataType());
            i++;
        }
        //2、再加条件部分
        for (int type : whereTypes) {
            types[i] = type;
            i++;
        }
        return types;
    }

    /**
     * UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
     * UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson'
     *
     * @param command 保存命令
     * @return 构建的保存语句
     */
    protected String buildOneSql(SaveCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        sb.append("update ");
        sb.append(md.getTableName());
        sb.append(" set ");
//        ArrayUtils.remove()
        buildFields(sb, md, command.getFields());
        //where
        FilterGroup fg = command.getWhere();
        if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
            sb.append(" where ");
            buildConditions(sb, md, fg.getFilters(), fg.getLogic());
        }
        return sb.toString();
    }

    protected void buildFields(StringBuilder sb, EntityMeta md, String[] fields) {
        //重命名查询的结果列表为实体字段名
        for (String fieldName : fields) {
            if (md.isIgnoreUpdateField(fieldName)) continue;
            sb.append(md.getColumnName(fieldName));
            sb.append("=?,");
        }
        sb.deleteCharAt(sb.length() - 1);
    }
}
