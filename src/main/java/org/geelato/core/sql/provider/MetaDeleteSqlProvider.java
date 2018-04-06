package org.geelato.core.sql.provider;

import org.geelato.core.gql.parser.DeleteCommand;
import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author geemeta
 */
@Component
public class MetaDeleteSqlProvider extends MetaBaseSqlProvider<DeleteCommand> {
    private static Logger logger = LoggerFactory.getLogger(MetaDeleteSqlProvider.class);

    @Override
    protected Object[] buildParams(DeleteCommand command) {
        return buildWhereParams(command);
    }

    @Override
    protected int[] buildTypes(DeleteCommand command) {
        return buildWhereTypes(command);
    }

    /**
     * DELETE FROM 表名称
     * DELETE FROM 表名称 WHERE 列名称 = 值
     * DELETE FROM Person WHERE LastName = 'Wilson'
     *
     * @param command
     * @return
     */
    protected String buildOneSql(DeleteCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        sb.append("delete from ");
        sb.append(md.getTableName());
        // where
        FilterGroup fg = command.getWhere();
        if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
            sb.append(" where ");
            buildConditions(sb, md, fg.getFilters(), fg.getLogic());
        }
        return sb.toString();
    }

}
