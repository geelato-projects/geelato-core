package org.geelato.core.orm;

import jakarta.annotation.Resource;
import org.geelato.core.meta.EntityManager;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.script.sql.SqlScriptManager;
import org.geelato.core.script.sql.SqlScriptManagerFactory;
import org.geelato.core.sql.SqlManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
public class BaseDao {
    public final static String SQL_TEMPLATE_MANAGER = "sql";
    protected JdbcTemplate jdbcTemplate;

    protected final MetaManager metaManager = MetaManager.singleInstance();
    protected final SqlScriptManager sqlScriptManager = SqlScriptManagerFactory.get(SQL_TEMPLATE_MANAGER);
    protected final SqlManager sqlManager = SqlManager.singleInstance();
    protected final EntityManager entityManager = EntityManager.singleInstance();

    protected static final Map<String, Object> defaultParams = new HashMap<>();

}
