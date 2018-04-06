package org.geelato.core.orm;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载sql文件并执行，用于数据初始化sql脚本的执行
 * @author geemeta
 */
public class SqlFiles {
    private static Logger logger = LoggerFactory.getLogger(SqlFiles.class);


    public static void loadAndExecute(String[] lines, JdbcTemplate jdbcTemplate, boolean isWinOS) {
        if (lines != null) {
            StringBuffer sb = new StringBuffer();
            for (String line : lines) {
                if (StringUtils.isBlank(line)) continue;
                line = line.trim();
                int index = line.trim().indexOf("--");
                if (index >= 0) {
                    //新的语句开始，若存在老的语句，执行老的语句并清空
                    if (sb.length() > 1) {
                        logger.info("execute sql :{}", sb.toString());
                        jdbcTemplate.execute(sb.toString());
                    }
                    sb = new StringBuffer();
                } else {
                    sb.append(line);
                    if (isWinOS)
                        sb.append("\r\n");
                    else
                        sb.append("\n");
                }
            }
            if (sb.length() > 1) {
                logger.info("execute sql :{}", sb.toString());
                jdbcTemplate.execute(sb.toString());
            }
        }
    }

    /**
     * 文件格式说明：每条语句之间必须用注释“--”进行分割
     *
     * @param is           SQL文件的输入流
     * @param jdbcTemplate
     * @param isWinOS
     */
    public static void loadAndExecute(InputStream is, JdbcTemplate jdbcTemplate, boolean isWinOS) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lineList = new ArrayList<String>();
            String line = null;
            while ((line = reader.readLine()) != null) {
                lineList.add(line);
                logger.info("line:{}", line);
            }
//            String[] lines = new String[lineList.size()];
            loadAndExecute(lineList, jdbcTemplate, isWinOS);
        } catch (IOException e) {
            logger.error("加载SQL流文件并执行出错！{}", e);
        }
    }


    public static void loadAndExecute(List<String> lineList, JdbcTemplate jdbcTemplate, boolean isWinOS) {
        String[] lines = new String[lineList.size()];
        lineList.toArray(lines);
        loadAndExecute(lines, jdbcTemplate, isWinOS);
    }

    /**
     * 文件格式说明：每条语句之间必须用注释“--”进行分割
     *
     * @param file         SQL文件
     * @param jdbcTemplate
     * @param isWinOS
     */
    public static void loadAndExecute(File file, JdbcTemplate jdbcTemplate, boolean isWinOS) {
        loadAndExecute(file.list(), jdbcTemplate, isWinOS);
    }

    /**
     * 文件格式说明：每条语句之间必须用注释“--”进行分割
     *
     * @param path         SQL文件物理位置,无法加载fatjar中的文件
     * @param jdbcTemplate
     * @param isWinOS
     */
    public static void loadAndExecute(String path, JdbcTemplate jdbcTemplate, boolean isWinOS) {
        try {
            if (Files.isExecutable(Paths.get(path))) {
                List<String> lines = Files.readAllLines(Paths.get(path));
                loadAndExecute(lines, jdbcTemplate, isWinOS);
            }
        } catch (IOException e) {
            logger.error("加载SQL文件并执行出错！\r\n文件：" + path + "\r\n", e);
        }
    }
}
