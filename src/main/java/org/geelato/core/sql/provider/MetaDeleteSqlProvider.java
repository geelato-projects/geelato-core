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
     *
     * @param command
     * @return
     */
    @Override
    protected String buildOneSql(DeleteCommand command) {
        StringBuilder sb = new StringBuilder();
        EntityMeta md = getEntityMeta(command);
        if(!LogicDelete){
            sb.append("delete from ");
            sb.append(md.getTableName());
            FilterGroup fg = command.getWhere();
            if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
                sb.append(" where ");
                buildConditions(sb, md, fg.getFilters(), fg.getLogic());
            }
        }else{
            sb.append("update   ");
            sb.append(md.getTableName());
            sb.append(" set del_status=1 ");
            FilterGroup fg = command.getWhere();
            if (fg != null && fg.getFilters() != null && fg.getFilters().size() > 0) {
                sb.append(" where ");
                buildConditions(sb, md, fg.getFilters(), fg.getLogic());
            }
        }
        return sb.toString();
    }

}
