package org.geelato.core.sql.provider;

import org.apache.commons.collections.map.HashedMap;
import org.geelato.core.gql.TypeConverter;
import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.BaseCommand;
import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author geemeta
 */
public abstract class MetaBaseSqlProvider<E extends BaseCommand> {
    protected  Boolean LogicDelete=true;
    private static Logger logger = LoggerFactory.getLogger(MetaBaseSqlProvider.class);
    protected static final Map<String, Boolean> keywordsMap = new HashedMap();
    protected static final Map<FilterGroup.Operator, String> enumToSignString = new HashMap<FilterGroup.Operator, String>();
    protected MetaManager metaManager = MetaManager.singleInstance();

    //表别名MAP
    private Map<String, String> tableAlias = new HashMap<>(8);

    static {
        // TODO 待添加所有的关键字、保留字
        keywordsMap.put("index", true);
        keywordsMap.put("indexs", true);
        keywordsMap.put("inner", true);
        keywordsMap.put("enable", true);
        keywordsMap.put("key", true);
    }

    protected static String convertToSignString(FilterGroup.Operator operator) {
        if (operator == FilterGroup.Operator.eq) {
            return "=";
        } else if (operator == FilterGroup.Operator.neq) {
            return "!=";
        } else if (operator == FilterGroup.Operator.lt) {
            return "<";
        } else if (operator == FilterGroup.Operator.lte) {
            return "<=";
        } else if (operator == FilterGroup.Operator.gt) {
            return ">";
        } else if (operator == FilterGroup.Operator.gte) {
            return ">=";
        } else if (operator == FilterGroup.Operator.in) {
            return "in";
        }
        return "=";
    }


    static {
        for (FilterGroup.Operator operator : FilterGroup.Operator.values()) {
            enumToSignString.put(operator, convertToSignString(operator));
        }
    }

    /**
     * 对command进行递归解析，创建BoundSql对象及其BoundSql子对象
     *
     * @param command QueryCommand、UpdateCommand等
     * @return
     */
    public BoundSql generate(E command) {
        BoundSql boundSql = new BoundSql();
        boundSql.setName(command.getEntityName());
        boundSql.setSql(buildOneSql(command));
        boundSql.setParams(buildParams(command));
        boundSql.setTypes(buildTypes(command));
        // 解析子级的command
        if (command.getCommands() != null) {
            command.getCommands().forEach(item -> {
                BoundSql subBoundSql = generate((E) item);
                if (boundSql.getBoundSqlMap() == null) {
                    boundSql.setBoundSqlMap(new HashMap<>());
                }
                boundSql.getBoundSqlMap().put(subBoundSql.getName(), subBoundSql);
            });
        }
        boundSql.setCommand(command);
        return boundSql;
    }

    protected abstract Object[] buildParams(E command);

    protected abstract int[] buildTypes(E command);

    /**
     * 构建一条语句，如insert、save、select、delete语句，在子类中实现
     *
     * @param command
     * @return 带参数（?）或无参数的完整sql语句
     */
    protected abstract String buildOneSql(E command);


    /**
     * 基于条件部分，构建参数值对像数组
     * 对于update、insert、delete的sql provider，即结合字段设值部分的需要，组合调整
     *
     * @param command
     * @return
     */
    protected Object[] buildWhereParams(E command) {
        if (command.getWhere() == null || command.getWhere().getFilters() == null || command.getWhere().getFilters().size() == 0) {
            return new Object[0];
        }
        List<Object> list = new ArrayList<>();
        for (FilterGroup.Filter filter : command.getWhere().getFilters()) {
            // 若为in操作，则需将in内的内容拆分成多个，相应地在构建参数占位符的地方也做相应的处理
            if (filter.getOperator().equals(FilterGroup.Operator.in)) {
                String[] ary = filter.getValue().split(",");
                for (String s : ary) {
                    list.add(s);
                }
            } else {
                list.add(filter.getValue());
            }
        }
        return list.toArray();
    }

    /**
     * 基于条件部分，构建参数类型数组
     * 对于update、insert、delete的sql provider，即结合字段设值部分的需要，组合调整
     *
     * @param command
     * @return
     */
    protected int[] buildWhereTypes(E command) {
        if (command.getWhere() == null || command.getWhere().getFilters() == null) {
            return new int[0];
        }
        EntityMeta em = getEntityMeta(command);
        int[] types = new int[command.getWhere().getFilters().size()];
        int i = 0;
        for (FilterGroup.Filter filter : command.getWhere().getFilters()) {
            types[i] = TypeConverter.toSqlType(em.getFieldMeta(filter.getField()).getColumn().getDataType());
            i++;
        }
        return types;
    }

    /**
     * 只构建当前实体的查询条件!isRefField
     *
     * @param sb
     * @param em
     * @param list
     * @param logic
     */
    protected void buildConditions(StringBuilder sb, EntityMeta em, List<FilterGroup.Filter> list, FilterGroup.Logic logic) {
        if (list != null && list.size() > 0) {
            Iterator<FilterGroup.Filter> iterator = list.iterator();
            int index = 0;
            while (iterator.hasNext()) {
                FilterGroup.Filter filter = iterator.next();
                //只构建当前实体的查询条件
                if (filter.isRefField()) {
                    continue;
                }
                if (index > 0) {
                    sb.append(" ");
                    sb.append(logic.getText());
                    sb.append(" ");
                }
                buildConditionSegment(sb, em, filter);
                index += 1;
            }
        }
    }


    /**
     * 构建单个过滤条件
     *
     * @param sb
     * @param em
     * @param filter
     */
    protected void buildConditionSegment(StringBuilder sb, EntityMeta em, FilterGroup.Filter filter) {
        FieldMeta fm = em.getFieldMeta(filter.getField());
        FilterGroup.Operator operator = filter.getOperator();
        if (operator == FilterGroup.Operator.eq || operator == FilterGroup.Operator.neq || operator == FilterGroup.Operator.lt || operator == FilterGroup.Operator.lte || operator == FilterGroup.Operator.gt || operator == FilterGroup.Operator.gte) {
            tryAppendKeywords(em, sb, fm);
            sb.append(enumToSignString.get(operator));
            sb.append("?");
        } else if (operator == FilterGroup.Operator.startWith) {
            tryAppendKeywords(em, sb, fm);
            sb.append(" like CONCAT('',?,'%')");
        } else if (operator == FilterGroup.Operator.endWith) {
            tryAppendKeywords(em, sb, fm);
            sb.append(" like CONCAT('%',?,'')");
        } else if (operator == FilterGroup.Operator.contains) {
            tryAppendKeywords(em, sb, fm);
            sb.append(" like CONCAT('%',?,'%')");
        } else if (operator == FilterGroup.Operator.in) {
            tryAppendKeywords(em, sb, fm);
            String[] ary = filter.getValue().split(",");
            sb.append(" in(");
            sb.append(org.geelato.core.util.StringUtils.join(ary.length, "?", ","));
            sb.append(")");
        } else {
            throw new RuntimeException("未实现Operator：" + operator);
        }
    }

    protected boolean isKeywords(String field) {
        if (field == null) {
            return false;
        }
        return keywordsMap.containsKey(field);
    }

    protected StringBuilder tryAppendKeywords(EntityMeta em, StringBuilder sb, FieldMeta fm) {
        Assert.notNull(fm, "获取不到元数据，fieldName：" + fm.getFieldName());
        return this.tryAppendKeywords(sb, fm.getColumnName());
    }

    protected StringBuilder tryAppendKeywords(StringBuilder sb, String field) {
        if (isKeywords(field)) {
            sb.append("'");
            sb.append(field);
            sb.append("'");
        } else {
            sb.append(field);
        }
        return sb;
    }

    public EntityMeta getEntityMeta(E command) {
        EntityMeta em = metaManager.getByEntityName(command.getEntityName());
        Assert.notNull(em, "未能通过entityName：" + command.getEntityName() + ",获取元数据信息EntityMeta。");
        return em;
    }

    //表别名
    public String buildTableAlias(String tableName) {
        if (tableName != null && !this.tableAlias.containsKey(tableName)) {
            this.tableAlias.put(tableName, "t" + this.tableAlias.size());
        }
        return this.tableAlias.get(tableName);
    }

    public String getTableAlias(String tableName) {
        return this.tableAlias.get(tableName);
    }
}
