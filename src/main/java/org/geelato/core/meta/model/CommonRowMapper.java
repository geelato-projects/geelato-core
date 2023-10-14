package org.geelato.core.meta.model;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geelato.core.meta.MetaManager;
import org.geelato.core.meta.model.entity.EntityMeta;
import org.geelato.core.meta.model.field.FieldMeta;
import org.geelato.core.orm.DateTimeConverter;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author geemeta
 */
public class CommonRowMapper<T> implements RowMapper<T> {
    private final Log logger = LogFactory.getLog(CommonRowMapper.class);
    private static final MetaManager metaManager = MetaManager.singleInstance();

    public CommonRowMapper() {
        // ConvertUtils.register(new DateLocaleConverter(), Date.class);
        // ConvertUtils.deregister(Date.class);
        DateTimeConverter dtc = new DateTimeConverter();
        ConvertUtils.register(dtc, java.time.LocalDateTime.class);
        ConvertUtils.register(dtc, Date.class);
        ConvertUtils.register(dtc, java.sql.Date.class);
    }

    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        String tableName = rsmd.getTableName(1);
        EntityMeta em = metaManager.get(tableName);

        Converter c = ConvertUtils.lookup(Date.class);
        Converter d = ConvertUtils.lookup(java.sql.Date.class);

        T bean = null;
        if (em.getEntityType() != null) {
            try {
                bean = (T) em.getEntityType().newInstance();
                for (int _iterator = 0; _iterator < rsmd.getColumnCount(); _iterator++) {
                    // getting the SQL column name
                    String columnName = rsmd.getColumnName(_iterator + 1);
                    // reading the value of the SQL column
                    Object columnValue = resultSet.getObject(_iterator + 1);
                    // iterating over outputClass attributes to check if
                    // any attribute has 'Column' annotation with
                    // matching 'name' value
                    for (FieldMeta fm : em.getFieldMetas()) {
                        if (columnName.equals(fm.getColumnName())) {
                            BeanUtils.setProperty(bean, fm.getFieldName(), columnValue);
                            break;
                        }
                    }
                }
                return bean;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logger.error(e);
            }
        } else {
            //map?
        }
        return null;
    }
}
