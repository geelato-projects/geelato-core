package org.geelato.core.orm;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.logging.log4j.util.Strings;
import org.geelato.core.aop.annotation.MethodLog;
import org.geelato.core.api.ApiMultiPagedResult;
import org.geelato.core.api.ApiPagedResult;
import org.geelato.core.exception.TestException;
import org.geelato.core.gql.GqlManager;
import org.geelato.core.gql.execute.BoundPageSql;
import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.gql.parser.QueryCommand;
import org.geelato.core.gql.parser.QueryViewCommand;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.meta.EntityManager;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.CommonRowMapper;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.entity.IdEntity;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.core.Ctx;
import org.geelato.core.script.sql.SqlScriptManager;
import org.geelato.core.script.sql.SqlScriptManagerFactory;
import org.geelato.core.sql.SqlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geemeta
 */
@Component
public class Dao {

    public final static String SQL_TEMPLATE_MANAGER = "sql";
    private static final Logger logger = LoggerFactory.getLogger(Dao.class);
    private static final Map<String, Object> defaultParams = new HashMap<>();
    /**
     * 默认取第一个：primaryJdbcTemplate，可在dao外进行设置更换
     */
    private JdbcTemplate jdbcTemplate;
    private Boolean defaultFilterOption = false;
    private FilterGroup defaultFilterGroup;
    private final MetaManager metaManager = MetaManager.singleInstance();
    private final SqlScriptManager sqlScriptManager = SqlScriptManagerFactory.get(SQL_TEMPLATE_MANAGER);
    private final SqlManager sqlManager = SqlManager.singleInstance();
    private final EntityManager entityManager = EntityManager.singleInstance();


    /**
     * <p>注意: 在使用之前，需先设置JdbcTemplate
     *
     * @see #setJdbcTemplate
     */
    public Dao() {
    }

    public Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDefaultFilter(Boolean defaultFilter, FilterGroup defaultFilterGroup) {
        this.defaultFilterOption = defaultFilter;
        this.defaultFilterGroup = defaultFilterGroup;
    }

    /**
     * @return 若需执行自己构建的语句，可以获取jdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //========================================================
    //                  基于sqlId                           ==
    //========================================================
    public void execute(String sqlId, Map<String, Object> paramMap) {
        jdbcTemplate.execute(sqlScriptManager.generate(sqlId, paramMap));
    }

    public Map<String, Object> queryForMap(String sqlId, Map<String, Object> paramMap) throws DataAccessException {
        return jdbcTemplate.queryForMap(sqlScriptManager.generate(sqlId, mixParam(paramMap)));
    }

    public <T> T queryForObject(String sqlId, Map<String, Object> paramMap, Class<T> requiredType) throws DataAccessException {
        return jdbcTemplate.queryForObject(sqlScriptManager.generate(sqlId, mixParam(paramMap)), requiredType);
    }

    public List<Map<String, Object>> queryForMapList(String sqlId, Map<String, Object> paramMap) {
        return jdbcTemplate.queryForList(sqlScriptManager.generate(sqlId, mixParam(paramMap)));
    }

    /**
     * @param sqlId       注意：该sqlId对应语句的查询结果是单列的
     * @param paramMap    参数用于sqlId对应语句的构建
     * @param elementType 查询单列结果的列类型如：Integer.class、String.class
     * @param <T>
     * @return 单列数据列表
     * @throws DataAccessException 查询出错
     */
    public <T> List<T> queryForOneColumnList(String sqlId, Map<String, Object> paramMap, Class<T> elementType) throws DataAccessException {
        return jdbcTemplate.queryForList(sqlScriptManager.generate(sqlId, mixParam(paramMap)), elementType);
    }

    public int save(String sqlId, Map<String, Object> paramMap) {
        return jdbcTemplate.update(sqlScriptManager.generate(sqlId, mixParam(paramMap)));
    }

    /**
     * 加入默认上下文参数：
     * 1、userId：当前用户id
     *
     * @param paramMap 需混合添加的参数
     * @return 混合添加后的参数
     */
    private Map<String, Object> mixParam(Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.size() == 0) {
            paramMap = new HashMap<>();
        }
        paramMap.put("$ctx", defaultParams.get("$ctx"));
        return paramMap;
    }

    //========================================================
    //                  基于元数据  gql                      ==
    //========================================================
    public Map<String, Object> queryForMap(BoundSql boundSql) throws DataAccessException {
        return jdbcTemplate.queryForMap(boundSql.getSql(), boundSql.getParams());
    }

    public <T> T queryForObject(BoundSql boundSql, Class<T> requiredType) throws DataAccessException {
        return jdbcTemplate.queryForObject(boundSql.getSql(), boundSql.getParams(), requiredType);
    }

    /**
     * @param boundPageSql
     * @param withMeta     是否需同时查询带出元数据
     * @return
     */
    public ApiPagedResult queryForMapList(BoundPageSql boundPageSql, boolean withMeta) {
        QueryCommand command = (QueryCommand) boundPageSql.getBoundSql().getCommand();
        logger.info(boundPageSql.getBoundSql().getSql());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(boundPageSql.getBoundSql().getSql(), boundPageSql.getBoundSql().getParams());
        ApiPagedResult result = new ApiPagedResult();
        list = convert(list, metaManager.getByEntityName(command.getEntityName()));
        result.setData(list);
        result.setTotal(jdbcTemplate.queryForObject(boundPageSql.getCountSql(), boundPageSql.getBoundSql().getParams(), Long.class));
        result.setPage(command.getPageNum());
        result.setSize(command.getPageSize());
        result.setDataSize(list != null ? list.size() : 0);
        if (withMeta) {
            result.setMeta(metaManager.getByEntityName(command.getEntityName()).getSimpleFieldMetas(command.getFields()));
        }
        return result;
    }

    private List<Map<String, Object>> convert(List<Map<String, Object>> data, EntityMeta entityMeta) {
        for (Map<String, Object> map : data) {
            for (String key : map.keySet()) {
                FieldMeta fieldMeta = entityMeta.getFieldMeta(key);
                if (fieldMeta != null) {
                    String columnType = entityMeta.getFieldMeta(key).getColumn().getDataType();
                    if (columnType.equals("JSON")) {
                        Object value = map.get(key);
                        String str = (value != null) ? value.toString() : "";
                        if (str.startsWith("{") && str.endsWith("}")) {
                            JSONObject jsonObject = JSONObject.parse(value.toString());
                            map.replace(key, value, jsonObject);
                        } else if (str.startsWith("[") && str.endsWith("]")) {
                            JSONArray jsonArray = JSONArray.parse(value.toString());
                            map.replace(key, value, jsonArray);
                        }
                    }
                }
            }
        }
        return data;
    }

    /**
     * @param boundPageSql
     * @param withMeta     是否需同时查询带出元数据
     * @return
     */
    public ApiMultiPagedResult.PageData queryForMapListToPageData(BoundPageSql boundPageSql, boolean withMeta) {
        QueryCommand command = (QueryCommand) boundPageSql.getBoundSql().getCommand();
        logger.info(boundPageSql.getBoundSql().getSql());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(boundPageSql.getBoundSql().getSql(), boundPageSql.getBoundSql().getParams());
        ApiMultiPagedResult.PageData result = new ApiMultiPagedResult.PageData();
        result.setData(list);
        result.setTotal(jdbcTemplate.queryForObject(boundPageSql.getCountSql(), boundPageSql.getBoundSql().getParams(), Long.class));
        result.setPage(command.getPageNum());
        result.setSize(command.getPageSize());
        result.setDataSize(list != null ? list.size() : 0);
        if (withMeta) {
            result.setMeta(metaManager.getByEntityName(command.getEntityName()).getSimpleFieldMetas(command.getFields()));
        }
        return result;
    }

    public <T> List<T> queryForOneColumnList(BoundSql boundSql, Class<T> elementType) throws DataAccessException {
        return jdbcTemplate.queryForList(boundSql.getSql(), boundSql.getParams(), elementType);
    }

    /**
     * 保存
     *
     * @param boundSql 查询语句
     * @return 主健值
     */
    public String save(BoundSql boundSql) throws TestException {
        SaveCommand command = (SaveCommand) boundSql.getCommand();
//        jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
        try {
            jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
        } catch (DataAccessException e) {
//            throw new TestException(e.getMessage());
            e.printStackTrace();
            return "saveFail";
        }
        return command.getPK();
    }

    /**
     * 批量保存
     *
     */
    public List<String> batchSave(List<BoundSql> boundSqlList) {
        List<Object[]> paramsObjs=new ArrayList<>();
        List<String> returnPks=new ArrayList<>();
        for (BoundSql bs:boundSqlList) {
            paramsObjs.add(bs.getParams());
            SaveCommand saveCommand=(SaveCommand)bs.getCommand();
            returnPks.add(saveCommand.getPK());
        }
        try{
            jdbcTemplate.batchUpdate(boundSqlList.get(0).getSql(),paramsObjs);
        }catch (DataAccessException e) {
            e.printStackTrace();
        }
        return returnPks;
    }
    public List<String> multiSave(List<BoundSql> boundSqlList) {
        DataSourceTransactionManager dataSourceTransactionManager=new DataSourceTransactionManager(this.jdbcTemplate.getDataSource());
        TransactionStatus transactionStatus=TransactionHelper.beginTransaction(dataSourceTransactionManager);
        List<Object[]> paramsObjs = new ArrayList<>();
        List<String> returnPks = new ArrayList<>();
        try {
            for (BoundSql bs : boundSqlList) {
                paramsObjs.add(bs.getParams());
                SaveCommand saveCommand = (SaveCommand) bs.getCommand();
                returnPks.add(saveCommand.getPK());
                jdbcTemplate.update(bs.getSql(), bs.getParams());
            }
            TransactionHelper.commitTransaction(dataSourceTransactionManager,transactionStatus);
        } catch (DataAccessException e) {
            e.printStackTrace();
            TransactionHelper.rollbackTransaction(dataSourceTransactionManager,transactionStatus);
            returnPks.clear();
        }
        return returnPks;
    }

    /**
     * 删除
     *
     * @param boundSql 删除语句
     * @return 删除的记录数据
     */
    public int delete(BoundSql boundSql) {
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
    }


    //========================================================
    //                  基于元数据  model                   ==
    //========================================================

    /**
     * 依据主键查询实体
     *
     * @param entityType 实体类型
     * @return 单个实体
     */
    public <T> T queryForObject(Class<T> entityType, Object PKValue) {
        return queryForObject(entityType, metaManager.get(entityType).getId().getFieldName(), PKValue);
    }

    /**
     * 依据单个条件查询实体
     *
     * @param entityType 实体类型
     * @param fieldName  实体的属性名
     * @param value      实体属性的值
     * @return 单个实体
     */
    public <T> T queryForObject(Class<T> entityType, String fieldName, Object value) {
        FilterGroup filterGroup = new FilterGroup().addFilter(fieldName, value.toString());
        BoundSql boundSql = sqlManager.generateQueryForObjectOrMapSql(entityType, filterGroup, null);
        logger.info(boundSql.toString());
        return jdbcTemplate.queryForObject(boundSql.getSql(), boundSql.getParams(), new CommonRowMapper<T>());
    }

    /**
     * 依据单个条件查询实体
     *
     * @param entityType 实体类型
     * @param fieldName  实体的属性名
     * @param value      实体属性的值
     * @return map通用格式的实体信息
     */
    public Map queryForMap(Class entityType, String fieldName, Object value) {
        FilterGroup filterGroup = new FilterGroup().addFilter(fieldName, value.toString());
        BoundSql boundSql = sqlManager.generateQueryForObjectOrMapSql(entityType, filterGroup, null);
        return jdbcTemplate.queryForMap(boundSql.getSql(), boundSql.getParams());
    }

    /**
     * 依据单个条件查询多个实体
     *
     * @param entityType 实体类型
     * @param fieldName  实体的属性名
     * @param value      实体属性的值
     * @return map通用格式的实体信息列表
     */
    public List<Map<String, Object>> queryForMapList(Class entityType, String fieldName, Object value) {
        FilterGroup filterGroup = new FilterGroup().addFilter(fieldName, value.toString());
        return queryForMapList(entityType, filterGroup);
    }

    /**
     * 依据多个条件查询多个实体
     *
     * @param entityType  实体类型
     * @param filterGroup 多条件过滤组合
     * @return map通用格式的实体信息列表
     */
    public List<Map<String, Object>> queryForMapList(Class entityType, FilterGroup filterGroup) {
        BoundSql boundSql = sqlManager.generateQueryForListSql(entityType, filterGroup, null);
        return jdbcTemplate.queryForList(boundSql.getSql(), boundSql.getParams());
    }

    /**
     * 无过滤条件查询多个实体
     *
     * @param entityType 实体类型
     * @return map通用格式的实体信息列表
     */
    public List<Map<String, Object>> queryForMapList(Class entityType) {
        BoundSql boundSql = sqlManager.generateQueryForListSql(entityType, null, null);
        return jdbcTemplate.queryForList(boundSql.getSql(), boundSql.getParams());
    }


    /**
     * @param entityType  查询的实体
     * @param field       查询的单列字段
     * @param filterGroup 查询条件
     * @param <T>         数据列类型
     * @return 单列数据列表
     */
    public <T> List<T> queryForOneColumnList(Class<T> entityType, String field, FilterGroup filterGroup) {
        BoundSql boundSql = sqlManager.generateQueryForListSql(entityType, filterGroup, field);
        return jdbcTemplate.queryForList(boundSql.getSql(), boundSql.getParams(), entityType);
    }


    public <E extends IdEntity> Map save(E entity) {
        BoundSql boundSql = entityManager.generateSaveSql(entity,new Ctx());
        logger.info(boundSql.toString());
        jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
        SaveCommand command = (SaveCommand) boundSql.getCommand();
        return command.getValueMap();
    }


    /**
     * 常用全量查询
     *
     * @param entityType
     * @param filterGroup
     * @param orderBy
     * @param <T>
     * @return
     */
    public <T> List<T> queryList(Class<T> entityType, FilterGroup filterGroup, String orderBy) {
        if (defaultFilterOption && defaultFilterGroup != null) {
            for (FilterGroup.Filter filter : defaultFilterGroup.getFilters()) {
                filterGroup.addFilter(filter);
            }
        }
        BoundSql boundSql = sqlManager.generateQueryForObjectOrMapSql(entityType, filterGroup, orderBy);
        logger.info(boundSql.toString());
        return jdbcTemplate.query(boundSql.getSql(), boundSql.getParams(), new CommonRowMapper<T>());
    }

    /**
     * 常用全量查询
     *
     * @param entityType
     * @param params
     * @param orderBy
     * @param <T>
     * @return
     */
    public <T> List<T> queryList(Class<T> entityType, Map<String, Object> params, String orderBy) {
        FilterGroup filterGroup = new FilterGroup();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null && Strings.isNotBlank(entry.getValue().toString())) {
                    filterGroup.addFilter(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return queryList(entityType, filterGroup, orderBy);
    }

    /**
     * 常用分页查询
     *
     * @param entityType
     * @param filterGroup
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @param <T>
     * @return
     */
    public <T> List<T> queryList(Class<T> entityType, FilterGroup filterGroup, int pageNum, int pageSize, String orderBy) {
        if (defaultFilterOption && defaultFilterGroup != null) {
            for (FilterGroup.Filter filter : defaultFilterGroup.getFilters()) {
                filterGroup.addFilter(filter);
            }
        }
        QueryCommand command = new QueryCommand();
        command.setPageNum(pageNum);
        command.setPageSize(pageSize);
        command.setOrderBy(orderBy);
        BoundSql boundSql = sqlManager.generatePageQuerySql(command, entityType, true, filterGroup, null);
        logger.info(boundSql.toString());
        return jdbcTemplate.query(boundSql.getSql(), boundSql.getParams(), new CommonRowMapper<T>());
    }

    /**
     * @param entityType
     * @param params
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @param <T>
     * @return
     */
    public <T> List<T> queryList(Class<T> entityType, Map<String, Object> params, int pageNum, int pageSize, String orderBy) {
        FilterGroup filterGroup = new FilterGroup();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null && Strings.isNotBlank(entry.getValue().toString())) {
                    filterGroup.addFilter(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return queryList(entityType, filterGroup, pageNum, pageSize, orderBy);
    }

    @MethodLog(type = "queryListByView")
    public List<Map<String, Object>> queryListByView(String entityName, String viewName, int pageNum, int pageSize, Map<String, Object> params) {
        FilterGroup filterGroup = new FilterGroup();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() != null && Strings.isNotBlank(entry.getValue().toString())) {
                filterGroup.addFilter(entry.getKey(), entry.getValue().toString());
            }
        }
        QueryViewCommand command = new QueryViewCommand();
        command.setPageNum(pageNum);
        command.setPageSize(pageSize);
        command.setViewName(viewName);
        BoundSql boundSql = sqlManager.generatePageQuerySql(command, entityName, true, filterGroup, null);
        logger.info(boundSql.toString());
        return jdbcTemplate.queryForList(boundSql.getSql());
    }

    public int delete(Class entityType, String fieldName, Object value) {
        FilterGroup filterGroup = new FilterGroup().addFilter(fieldName, value.toString());
        BoundSql boundSql = sqlManager.generateDeleteSql(entityType, filterGroup);
        logger.info(boundSql.toString());
        return jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
    }
}
