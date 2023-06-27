package org.geelato.core.gql.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geemeta
 */
public class BaseCommand<E extends BaseCommand> {

    /**
     * TODO 客户端生成的唯一标识，用于缓存
     */
    private String key;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    protected BaseCommand<E> parentCommand;

    protected CommandType commandType;
    // 命令对应实体名称
    protected String entityName;
    // 指定字段
    protected String[] fields;
    // 指定条件
    protected FilterGroup where;
    // 指定条件
    protected StringBuilder from = new StringBuilder();
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

    /**
     * @return 如果是根命令，则返回null
     */
    public BaseCommand<E> getParentCommand() {
        return parentCommand;
    }

    public void setParentCommand(BaseCommand<E> parentCommand) {
        this.parentCommand = parentCommand;
    }

    /**
     * @return 获取子命令，若不存在，则创建一个空的命令列表
     */
    public List<E> getCommands() {
        if (commands == null){ commands = new ArrayList<>();}
        return commands;
    }

    public void setCommands(List<E> commands) {
        this.commands = commands;
    }

    public boolean hasCommands() {
        return commands == null || commands.size() == 0 ? false : true;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public StringBuilder getFrom() {
        return from;
    }

    public BaseCommand appendFrom(String from) {
        this.from.append(from);
        return this;
    }

    public BaseCommand appendFrom(String tablaName, String alias) {
        this.from.append(tablaName).append(" ").append(alias);
        return this;
    }

    /**
     * from中是否已join该表
     *
     * @param alias
     * @return
     */
    public boolean hasNotJoin(String alias) {
        return this.from.indexOf(alias) == -1;
    }
}
