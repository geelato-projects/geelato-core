package org.geelato.core.orm;

import org.geelato.core.api.ApiMultiPagedResult;
import org.geelato.core.api.ApiPagedResult;
import org.geelato.core.gql.GqlManager;
import org.geelato.core.gql.execute.BoundPageSql;
import org.geelato.core.gql.execute.BoundSql;
import org.geelato.core.gql.parser.FilterGroup;
import org.geelato.core.gql.parser.QueryCommand;
import org.geelato.core.gql.parser.SaveCommand;
import org.geelato.core.meta.EntityManager;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.CommonRowMapper;
import org.geelato.core.meta.model.entity.IdEntity;
import org.geelato.core.mvc.Ctx;
import org.geelato.core.script.sql.SqlScriptManager;
import org.geelato.core.script.sql.SqlScriptManagerFactory;
import org.geelato.core.sql.SqlManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author geemeta
 */
@Component
public class Dao {

    /**
     * 默认取第一个：primaryJdbcTemplate，可在dao外进行设置更换
     */
//    @Autowired
//    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(Dao.class);
    private static Map<String, Object> defaultParams = new HashMap<>();
    public final static String SQL_TEMPLATE_MANAGER = "sql";
    private MetaManager metaManager = MetaManager.singleInstance();
    private SqlScriptManager sqlScriptManager = SqlScriptManagerFactory.get(SQL_TEMPLATE_MANAGER);
    private GqlManager gqlManager = GqlManager.singleInstance();
    private SqlManager sqlManager = SqlManager.singleInstance();
    private EntityManager entityManager = EntityManager.singleInstance();
//    private static HashMap<String, String> ignoreFieldsMap = new HashMap<String, String>(1);


//    static {
//        ignoreFieldsMap.put("createDate", "createDate");
//    }

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
        jdbcTemplate.execute((String) sqlScriptManager.generate(sqlId, paramMap));
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
        return jdbcTemplate.update((String) sqlScriptManager.generate(sqlId, mixParam(paramMap)));
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
        List<Map<String, Object>> list = jdbcTemplate.queryForList(boundPageSql.getBoundSql().getSql(), boundPageSql.getBoundSql().getParams());
        ApiPagedResult result = new ApiPagedResult();
//        result.setData(convertLongToString(list));
        result.setData(list);
        result.setTotal(jdbcTemplate.queryForObject(boundPageSql.getCountSql(), boundPageSql.getBoundSql().getParams(), Long.class));
        result.setPage(command.getPageNum());
        result.setSize(command.getPageSize());
        result.setDataSize(list != null ? list.size() : 0);
        if (withMeta)
            result.setMeta(metaManager.getByEntityName(command.getEntityName()).getSimpleFieldMetas(command.getFields()));
        return result;
    }

    /**
     * @param boundPageSql
     * @param withMeta     是否需同时查询带出元数据
     * @return
     */
    public ApiMultiPagedResult.PageData queryForMapListToPageData(BoundPageSql boundPageSql, boolean withMeta) {
        QueryCommand command = (QueryCommand) boundPageSql.getBoundSql().getCommand();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(boundPageSql.getBoundSql().getSql(), boundPageSql.getBoundSql().getParams());
        ApiMultiPagedResult.PageData result = new ApiMultiPagedResult.PageData();
//        result.setData(convertLongToString(list));
        result.setData(list);
        result.setTotal(jdbcTemplate.queryForObject(boundPageSql.getCountSql(), boundPageSql.getBoundSql().getParams(), Long.class));
        result.setPage(command.getPageNum());
        result.setSize(command.getPageSize());
        result.setDataSize(list != null ? list.size() : 0);
        if (withMeta)
            result.setMeta(metaManager.getByEntityName(command.getEntityName()).getSimpleFieldMetas(command.getFields()));
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
    public String save(BoundSql boundSql) {
        SaveCommand command = (SaveCommand) boundSql.getCommand();
        int updateNum = jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
        return command.getPK();
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
        BoundSql boundSql = sqlManager.generateQueryForObjectOrMapSql(entityType, filterGroup);
        if (logger.isDebugEnabled())
            logger.debug("{}", boundSql);
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
        BoundSql boundSql = sqlManager.generateQueryForObjectOrMapSql(entityType, filterGroup);
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
        BoundSql boundSql = sqlManager.generateQueryForListSql(entityType, filterGroup);
        return jdbcTemplate.queryForList(boundSql.getSql(), boundSql.getParams());
    }

    /**
     * 无过滤条件查询多个实体
     *
     * @param entityType 实体类型
     * @return map通用格式的实体信息列表
     */
    public List<Map<String, Object>> queryForMapList(Class entityType) {
        BoundSql boundSql = sqlManager.generateQueryForListSql(entityType, null);
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
        BoundSql boundSql = entityManager.generateSaveSql(entity, getSessionCtx());
        jdbcTemplate.update(boundSql.getSql(), boundSql.getParams());
        SaveCommand command = (SaveCommand) boundSql.getCommand();
        return command.getValueMap();
    }

//    protected Object getObject(Class clazz) {
//        if (!beanCache.containsKey(clazz.toString()))
//            try {
//                beanCache.put(clazz.toString(), clazz.newInstance());
//            } catch (InstantiationException e) {
//                logger.error("创建实例失败。", e);
//            } catch (IllegalAccessException e) {
//                logger.error("创建实例失败。", e);
//            }
//        return beanCache.getBizRuleScriptManager(clazz.toString());
//
//    }


//    private <E extends IdEntity> DataDynamic genDataDynamic(E model) {
//        EntityMeta entityMeta = metaManager.getBizRuleScriptManager(model.getClass());
//        DataDynamic dd = new DataDynamic();
//        dd.setIdField("id");
//        dd.setIdValue(model.getId());
////        dd.setAction("");
////        dd.setDescription("");
//        dd.setEntity(model.getClass().getName());
//        dd.setName(entityMeta.getTableMeta().getTitle());
//        dd.setTableName(entityMeta.getTableName());
//        dd.setCreateAt(new Date());
//        dd.setCreateAt(dd.getUpdateAt());
//        dd.setCreator(SecurityHelper.getCurrentUserId());
//        dd.setUpdater(SecurityHelper.getCurrentUserId());
//        dd.setSubjectName(SecurityHelper.getCurrentUserName());
//        return dd;
//    }

    /**
     * @return 当前会话信息
     */
    protected Ctx getSessionCtx() {
        Ctx ctx = new Ctx();
        //TODO 从会话中获取
        ctx.put("userId", String.valueOf(1));
        return ctx;
    }

}
