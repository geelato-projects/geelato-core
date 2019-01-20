package org.geelato.core.sql;

import org.geelato.core.gql.execute.BoundPageSql;
import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.*;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.sql.provider.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于元数据的sql语句管理器
 * <p>创建sql语句</p>
 *
 * @author geemeta
 */
public class SqlManager {
    private static Lock lock = new ReentrantLock();
    private static SqlManager instance;
    private MetaManager metaManager = MetaManager.singleInstance();
    private MetaQuerySqlProvider metaQuerySqlProvider = new MetaQuerySqlProvider();
    private MetaQueryTreeSqlProvider metaQueryTreeSqlProvider = new MetaQueryTreeSqlProvider();
    private MetaInsertSqlProvider metaInsertSqlProvider = new MetaInsertSqlProvider();
    private MetaUpdateSqlProvider metaUpdateSqlProvider = new MetaUpdateSqlProvider();
    private MetaDeleteSqlProvider metaDeleteSqlProvider = new MetaDeleteSqlProvider();

    public static SqlManager singleInstance() {
        lock.lock();
        if (instance == null) instance = new SqlManager();
        lock.unlock();
        return instance;
    }

    //========================================================
    //                  基于元数据  gql                      ==
    //========================================================
    public BoundSql generateQuerySql(QueryCommand command) {
        return metaQuerySqlProvider.generate(command);
    }

    public BoundPageSql generatePageQuerySql(QueryCommand command) {
        BoundPageSql boundPageSql = new BoundPageSql();
        boundPageSql.setBoundSql(metaQuerySqlProvider.generate(command));
        boundPageSql.setCountSql(metaQuerySqlProvider.buildCountSql(command));
        return boundPageSql;
    }

    public BoundPageSql generatePageQuerySql(QueryTreeCommand command) {
        BoundPageSql boundPageSql = new BoundPageSql();
        boundPageSql.setBoundSql(metaQueryTreeSqlProvider.generate(command));
        boundPageSql.setCountSql(metaQueryTreeSqlProvider.buildCountSql(command));
        return boundPageSql;
    }

    public BoundSql generateSaveSql(SaveCommand command) {
        if (command.getCommandType() == CommandType.Update) {
            return metaUpdateSqlProvider.generate(command);
        } else {
            return metaInsertSqlProvider.generate(command);
        }
    }

    public BoundSql generateDeleteSql(DeleteCommand command) {
        return metaDeleteSqlProvider.generate(command);
    }

    //========================================================
    //                  基于元数据  model                   ==
    //========================================================
    public <T> BoundSql generateQueryForObjectOrMapSql(Class<T> clazz, FilterGroup filterGroup) {
        return generateQuerySql(clazz, false, filterGroup, null);
    }

    public <T> BoundSql generateQueryForListSql(Class<T> clazz, FilterGroup filterGroup) {
        return generateQuerySql(clazz, true, filterGroup, null);
    }

    /**
     * @param clazz 查询的实体
     * @param filterGroup 过滤条件
     * @param field 指定实体中的查询列，单列
     * @param <T>
     * @return 单列列表查询语句
     */
    public <T> BoundSql generateQueryForListSql(Class<T> clazz, FilterGroup filterGroup, String field) {
        return generateQuerySql(clazz, true, filterGroup, new String[]{field});
    }

    /**
     * @param clazz 查询的实体
     * @param filterGroup 过滤条件
     * @param fields 指定实体中的查询列，多列
     * @param <T>
     * @return 多列列表查询语句
     */
    public <T> BoundSql generateQueryForListSql(Class<T> clazz, FilterGroup filterGroup, String[] fields) {
        return generateQuerySql(clazz, true, filterGroup, fields);
    }


    /**
     * @param clazz 查询的实体
     * @param isArray 是否查询多条记录，true：是，false：否
     * @param filterGroup 过滤条件
     * @param fields 指定实体中的查询列，多列
     * @param <T>
     * @return 多列列表查询语句
     */
    private <T> BoundSql generateQuerySql(Class<T> clazz, boolean isArray, FilterGroup filterGroup, String[] fields) {
        QueryCommand queryCommand = new QueryCommand();
        EntityMeta em = metaManager.get(clazz);
        queryCommand.setEntityName(em.getEntityName());
        queryCommand.setFields(fields != null && fields.length > 0 ? fields : em.getFieldNames());
        queryCommand.setQueryForList(isArray);
        queryCommand.setWhere(filterGroup);
        return metaQuerySqlProvider.generate(queryCommand);
    }


}