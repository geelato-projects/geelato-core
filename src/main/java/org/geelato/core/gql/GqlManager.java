package org.geelato.core.gql;

import org.geelato.core.gql.parser.*;
import org.geelato.core.Ctx;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于元数据的sql语句管理器
 * 元数据可来源于java类注解，也可来源于数据库配置的元数据信息
 *
 * @author geemeta
 */
public class GqlManager {
    private static Lock lock = new ReentrantLock();
    private static GqlManager instance;
    private JsonTextQueryParser jsonTextQueryParser = new JsonTextQueryParser();
    private JsonTextSaveParser jsonTextSaveParser = new JsonTextSaveParser();
    private JsonTextDeleteParser jsonTextDeleteParser = new JsonTextDeleteParser();

    public static GqlManager singleInstance() {
        lock.lock();
        if (instance == null) {
            instance = new GqlManager();
        }
        lock.unlock();
        return instance;
    }


    //========================================================
    //                  基于元数据  gql                      ==
    //========================================================
    public QueryCommand generateQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parse(jsonText,ctx);
    }

    public List<QueryCommand> generateMultiQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parseMulti(jsonText,ctx);
    }

    public QueryCommand generatePageQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parse(jsonText,ctx);
    }

    public SaveCommand generateSaveSql(String jsonText, Ctx ctx) {
        return jsonTextSaveParser.parse(jsonText, ctx);
    }

    public List<SaveCommand> generateBatchSaveSql(String jsonText, Ctx ctx) {
        return jsonTextSaveParser.parseBatch(jsonText, ctx);
    }
    public List<SaveCommand> generateMultiSaveSql(String jsonText, Ctx ctx) {
        return jsonTextSaveParser.parseMulti(jsonText, ctx);
    }
    public DeleteCommand generateDeleteSql(String jsonText, Ctx ctx) {
        return jsonTextDeleteParser.parse(jsonText, ctx);
    }

}