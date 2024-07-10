package org.geelato.core.util;

import org.apache.logging.log4j.util.Strings;
import org.geelato.core.enums.Dialects;
import org.geelato.core.meta.model.connect.ConnectMeta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author diabl
 */
public class ConnectUtils {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true";
    private static final int MYSQL_CONNECT_TIMEOUT = 10;

    /**
     * Mysql数据库链接测试
     *
     * @param ip
     * @param port
     * @param name
     * @param user
     * @param password
     * @return
     * @throws SQLException
     */
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

    /**
     * 数据库链接测试
     *
     * @param meta
     * @return
     * @throws SQLException
     */
    public static boolean connectionTest(ConnectMeta meta) throws SQLException {
        if (Strings.isNotBlank(meta.getDbType())) {
            if (Dialects.MYSQL.name().equalsIgnoreCase(meta.getDbType())) {
                return ConnectUtils.mysqlConnectionTest(meta.getDbHostnameIp(), Integer.toString(meta.getDbPort()), meta.getDbName(), meta.getDbUserName(), meta.getDbPassword());
            }
        }

        return false;
    }

    /**
     * 创建一个数据库连接对象，但不要打开连接。
     *
     * @param meta
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(ConnectMeta meta) throws SQLException {
        if (Strings.isNotBlank(meta.getDbType())) {
            if (Dialects.MYSQL.name().equalsIgnoreCase(meta.getDbType())) {
                String url = String.format(ConnectUtils.MYSQL_URL, meta.getDbHostnameIp(), Integer.toString(meta.getDbPort()), meta.getDbName());
                return DriverManager.getConnection(url, meta.getDbUserName(), meta.getDbPassword());
            }
        }

        return null;
    }

}
