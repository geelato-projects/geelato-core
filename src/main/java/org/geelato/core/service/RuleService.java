package org.geelato.core.service;

import org.geelato.core.api.ApiMultiPagedResult;
import org.geelato.core.api.ApiPagedResult;
import org.geelato.core.biz.rules.common.EntityValidateRule;
import org.geelato.core.gql.GqlManager;
import org.geelato.core.gql.execute.BoundPageSql;
import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.DeleteCommand;
import org.geelato.core.gql.parser.QueryCommand;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.mvc.Ctx;
import org.geelato.core.orm.Dao;
import org.geelato.core.sql.SqlManager;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geemeta
 */
@Component
public class RuleService {

    private Dao dao;
    private GqlManager gqlManager = GqlManager.singleInstance();
    private SqlManager sqlManager = SqlManager.singleInstance();
    private RulesEngine rulesEngine = new DefaultRulesEngine();

    /**
     * <p>注意: 在使用之前，需先设置dao
     *
     * @see #setDao
     */
    public RuleService() {
    }

    /**
     * @param dao 设置dao，如primaryDao
     */
    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Map<String, Object> queryForMap(String gql) throws DataAccessException {
        QueryCommand command = gqlManager.generateQuerySql(gql, getSessionCtx());
        BoundSql boundSql = sqlManager.generateQuerySql(command);
        return dao.queryForMap(boundSql);
    }

    public <T> T queryForObject(String gql, Class<T> requiredType) throws DataAccessException {
        QueryCommand command = gqlManager.generateQuerySql(gql, getSessionCtx());
        BoundSql boundSql = sqlManager.generateQuerySql(command);
        return dao.queryForObject(boundSql, requiredType);
    }

    public ApiPagedResult queryForMapList(String gql, boolean withMeta) {
        QueryCommand command = gqlManager.generateQuerySql(gql, getSessionCtx());
        BoundPageSql boundPageSql = sqlManager.generatePageQuerySql(command);
        return dao.queryForMapList(boundPageSql, withMeta);
    }

    public ApiMultiPagedResult queryForMultiMapList(String gql, boolean withMeta) {
        Map<String, ApiMultiPagedResult.PageData> dataMap = new HashMap<String, ApiMultiPagedResult.PageData>();
        List<QueryCommand> commandList = gqlManager.generateMultiQuerySql(gql, getSessionCtx());
        for (QueryCommand command : commandList) {
            BoundPageSql boundPageSql = sqlManager.generatePageQuerySql(command);
            dataMap.put(command.getEntityName(), dao.queryForMapListToPageData(boundPageSql, withMeta));
        }
        ApiMultiPagedResult result = new ApiMultiPagedResult();
        result.setData(dataMap);
        return result;
    }

    public <T> List<T> queryForOneColumnList(String gql, Class<T> elementType) throws DataAccessException {
        QueryCommand command = gqlManager.generateQuerySql(gql, getSessionCtx());
        BoundSql boundSql = sqlManager.generateQuerySql(command);
        return dao.queryForOneColumnList(boundSql, elementType);
    }

    /**
     * 保存操作
     * <p>在保存之前，依据业务代码，从配置的业务规则库中读取规则，对command中的数据进行预处理，如更改相应的参数数据。</p>
     *
     * @param biz 业务代码
     * @param gql geelato query language
     * @return 主健值
     */
    public String save(String biz, String gql) {
        SaveCommand command = gqlManager.generateSaveSql(gql, getSessionCtx());
        Facts facts = new Facts();
        facts.put("saveCommand", command);
        Rules rules = new Rules();
        rules.register(new EntityValidateRule());

        rulesEngine.fire(rules, facts);

        BoundSql boundSql = sqlManager.generateSaveSql(command);
        return dao.save(boundSql);
    }

    /**
     * 删除操作
     * <p>在删除之前，依据业务代码，从配置的业务规则库中读取规则，对command中的数据进行预处理，如更改相应的参数数据。</p>
     *
     * @param biz 业务代码
     * @param gql geelato query language
     * @return 主健值
     */
    public int delete(String biz, String gql) {
        DeleteCommand command = gqlManager.generateDeleteSql(gql, getSessionCtx());
        Facts facts = new Facts();
        facts.put("deleteCommand", command);
        Rules rules = new Rules();
        rules.register(new EntityValidateRule());

        rulesEngine.fire(rules, facts);

        BoundSql boundSql = sqlManager.generateDeleteSql(command);
        return dao.delete(boundSql);
    }

    /**
     * @return 当前会话信息
     */
    protected Ctx getSessionCtx() {
        Ctx ctx = new Ctx();
        // TODO 从会话中获取
        ctx.put("userId", String.valueOf(1));
        return ctx;
    }
}
