package org.geelato.core.sql.provider;

import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.gql.parser.QueryCommand;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author geemeta
 */
@Component
public class MetaQuerySqlProvider extends MetaBaseSqlProvider<QueryCommand> {
    private static Logger logger = LoggerFactory.getLogger(MetaQuerySqlProvider.class);

    @Override
    protected Object[] buildParams(QueryCommand command) {
        return buildWhereParams(command);
    }

    @Override
    protected int[] buildTypes(QueryCommand command) {
        return buildWhereTypes(command);
    }

    @Override
    protected String buildOneSql(QueryCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        sb.append("select ");
        buildSelectFields(sb, md, command.getFields(), command.getAlias());
        sb.append(" from ");
        sb.append(md.getTableName());
        // where
        FilterGroup fg = command.getWhere();
        if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
            sb.append(" where ");
            buildConditions(sb, md, fg.getFilters(), fg.getLogic());
        }
        // group by
        if (StringUtils.hasText(command.getGroupBy())) {
            sb.append(" group by ");
            sb.append(command.getGroupBy());
        }
        // having
        if (command.getHaving() != null) {
            sb.append(" having ");
            sb.append(command.getHaving());
        }
        // order by
        if (StringUtils.hasText(command.getOrderBy())) {
            sb.append(" order by ");
            sb.append(command.getOrderBy());
        }
        // limit offset count
        // TODO limit分页在数据记录达百万级时，性能较差
        if (command.isPagingQuery()) {
            sb.append(" limit ");
            sb.append((command.getPageNum() - 1) * command.getPageSize());
            sb.append(",");
            sb.append(command.getPageSize());
        }
        return sb.toString();
    }

    /**
     * 构健统计数据
     *
     * @param command
     * @return
     */
    public String buildCountSql(QueryCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        sb.append("select count(*) from (");
        sb.append("select ");
        // fields
        buildSelectFields(sb, md, command.getFields(), command.getAlias());
        sb.append(" from ");
        sb.append(md.getTableName());
        //where
        FilterGroup fg = command.getWhere();
        if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
            sb.append(" where ");
            buildConditions(sb, md, fg.getFilters(), fg.getLogic());
        }
        //group by
        if (StringUtils.hasText(command.getGroupBy())) {
            sb.append(" group by ");
            sb.append(command.getGroupBy());
        }
        //having
        if (command.getHaving() != null) {
            sb.append(" having ");
            sb.append(command.getHaving());
        }
        sb.append(") t");
        return sb.toString();
    }

    private void buildSelectCountField(StringBuilder sb, EntityMeta md, String[] fields) {
        if (fields == null || fields.length == 0) {
            sb.append("*");
            return;
        } else if (fields.length == 1 && "*".equals(fields[0])) {
            sb.append("*");
            return;
        }
        // 只取第一个字段用于统计总数，默认按主键统计
        if (md.getId() != null) {
            sb.append(md.getId().getColumnName());
        } else {
            FieldMeta fm = md.getFieldMeta(fields[0]);
            sb.append(fm.getColumnName());
        }
    }

    private void buildSelectFields(StringBuilder sb, EntityMeta md, String[] fields, Map alias) {
        if (fields == null || fields.length == 0) {
            sb.append("*");
            return;
        } else if (fields.length == 1 && "*".equals(fields[0])) {
            sb.append("*");
            return;
        }
        //重命名查询的结果列表为实体字段名
        for (String fieldName : fields) {
            FieldMeta fm = md.getFieldMeta(fieldName);
            if (alias.containsKey(fieldName)) {
                // 有指定的重命名要求时
                sb.append(fm.getColumnName());
                sb.append(" ");
                sb.append(alias.get(fieldName));
            } else {
                // 无指定的重命名要求，将数据库的字段格式转成实体字段格式，如role_id to roleId
                if (fm.isEquals()) {
                    sb.append(fm.getColumnName());
                } else {
                    sb.append(fm.getColumnName());
                    sb.append(" ");
                    sb.append(fm.getFieldName());
                }
            }
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
    }

//    protected void buildConditions(StringBuilder sb, EntityMeta md, List<FilterGroup.Filter> list, FilterGroup.Logic logic) {
//        if (list != null && list.size() > 0) {
//            Iterator<FilterGroup.Filter> iterator = list.iterator();
//            int index = 0;
//            while (iterator.hasNext()) {
//                FilterGroup.Filter filter = iterator.next();
//                //只构建当前实体的查询条件
//                if (filter.isRefField()) continue;
//                if (index > 0) {
//                    sb.append(" ");
//                    sb.append(logic.getText());
//                    sb.append(" ");
//                }
//                buildConditionSegment(sb, md, filter);
//                index += 1;
//            }
//        }
//    }
//
//    
//  
//    protected void buildConditionSegment(StringBuilder sb, EntityMeta md, FilterGroup.Filter filter) {
//
//        String columnName = md.getColumnName(filter.getField());
//        FilterGroup.Operator operator = filter.getOperator();
//        if (operator == FilterGroup.Operator.eq || operator == FilterGroup.Operator.neq || operator == FilterGroup.Operator.lt || operator == FilterGroup.Operator.lte || operator == FilterGroup.Operator.gt || operator == operator.gte) {
//            sb.append(columnName);
//            sb.append(enumToSignString.get(operator));
//            sb.append("?");
//        } else if (operator == FilterGroup.Operator.startWith) {
//            sb.append(columnName);
//            sb.append(" like CONCAT('',?,'%')");
//        } else if (operator == FilterGroup.Operator.endWith) {
//            sb.append(columnName);
//            sb.append(" like CONCAT('%',?,'')");
//        } else if (operator == FilterGroup.Operator.contains) {
//            sb.append(columnName);
//            sb.append(" like CONCAT('%',?,'%')");
//        }
//    }

}
