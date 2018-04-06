package org.geelato.core.gql.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geemeta
 *
 */
public class BaseCommand<E> {


    protected CommandType commandType;
    // 命令对应实体名称
    protected String entityName;
    // 指定字段
    protected String[] fields;
    // 指定条件
    protected FilterGroup where;
    // 子命令
    protected List<E> commands;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public FilterGroup getWhere() {
        return where;
    }

    public void setWhere(FilterGroup where) {
        this.where = where;
    }

    public List<E> getCommands() {
        if (commands == null) commands = new ArrayList<>();
        return commands;
    }

    public void setCommands(List<E> commands) {
        this.commands = commands;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}
