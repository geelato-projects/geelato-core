package org.geelato.core.meta;

import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.CommandType;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.meta.model.entity.IdEntity;
import org.geelato.core.meta.model.parser.EntitySaveParser;
import org.geelato.core.mvc.Ctx;
import org.geelato.core.sql.provider.MetaDeleteSqlProvider;
import org.geelato.core.sql.provider.MetaInsertSqlProvider;
import org.geelato.core.sql.provider.MetaQuerySqlProvider;
import org.geelato.core.sql.provider.MetaUpdateSqlProvider;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author geemeta
 */
public class EntityManager {
    private static Lock lock = new ReentrantLock();
    private static EntityManager instance;
    private EntitySaveParser entitySaveParser = new EntitySaveParser();
    private MetaQuerySqlProvider metaQuerySqlProvider = new MetaQuerySqlProvider();
    private MetaInsertSqlProvider metaInsertSqlProvider = new MetaInsertSqlProvider();
    private MetaUpdateSqlProvider metaUpdateSqlProvider = new MetaUpdateSqlProvider();
    private MetaDeleteSqlProvider metaDeleteSqlProvider = new MetaDeleteSqlProvider();

    public static EntityManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new EntityManager();
        }
        lock.unlock();
        return instance;
    }

    public BoundSql generateSaveSql(IdEntity entity, Ctx ctx) {
        SaveCommand command = entitySaveParser.parse(entity, ctx);
        if (command.getCommandType() == CommandType.Update) {
            return metaUpdateSqlProvider.generate(command);
        } else {
            return metaInsertSqlProvider.generate(command);
        }
    }
}
