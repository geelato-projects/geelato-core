package org.geelato.core.gql;

import org.geelato.core.gql.parser.*;
import org.geelato.core.mvc.Ctx;

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
        if (instance == null) instance = new GqlManager();
        lock.unlock();
        return instance;
    }


    //========================================================
    //                  基于元数据  gql                      ==
    //========================================================
    public QueryCommand generateQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parse(jsonText);
    }

    public List<QueryCommand> generateMultiQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parseMulti(jsonText);
    }

    public QueryCommand generatePageQuerySql(String jsonText, Ctx ctx) {
        return jsonTextQueryParser.parse(jsonText);
    }

    public SaveCommand generateSaveSql(String jsonText, Ctx ctx) {
        return jsonTextSaveParser.parse(jsonText, ctx);
    }

    public DeleteCommand generateDeleteSql(String jsonText, Ctx ctx) {
        return jsonTextDeleteParser.parse(jsonText, ctx);
    }

}