package org.geelato.core.util;

import org.apache.logging.log4j.util.Strings;
import org.geelato.core.constants.ApiErrorMsg;
import org.geelato.core.enums.Dialects;
import org.geelato.core.meta.model.connect.ConnectMeta;
import org.springframework.util.Assert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

/**
 * @author diabl
 */
public class ConnectUtils {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
    private static final int MYSQL_CONNECT_TIMEOUT = 10;

    private static Boolean mysqlConnectionTest(String ip, String port, String name, String user, String password) throws SQLException {
        Boolean isConnected = false;
        if (Strings.isBlank(ip) || Strings.isBlank(name) || Strings.isBlank(port)) {
            return false;
        }
        if (Strings.isBlank(user) || Strings.isBlank(password)) {
            return false;
        }
        String url = String.format(ConnectUtils.MYSQL_URL, ip, port, name);
        Connection connection = null;
        try {
            Class.forName(ConnectUtils.MYSQL_DRIVER);
            connection = DriverManager.getConnection(url, user, password);
            isConnected = connection != null && connection.isValid(ConnectUtils.MYSQL_CONNECT_TIMEOUT);
        } catch (ClassNotFoundException | SQLException e) {
            isConnected = false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        return isConnected;
    }

    public static boolean connectionTest(ConnectMeta meta) throws SQLException {
        Assert.notNull(meta, ApiErrorMsg.IS_NULL);
        if (Strings.isNotBlank(meta.getDbType())) {
            String dialect = meta.getDbType().toUpperCase(Locale.ENGLISH);
            if (Dialects.MYSQL.name().equals(dialect)) {
                return ConnectUtils.mysqlConnectionTest(meta.getDbHostnameIp(), Integer.toString(meta.getDbPort()), meta.getDbName(), meta.getDbUserName(), meta.getDbPassword());
            }
        }

        return false;
    }

}