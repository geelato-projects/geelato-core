package org.geelato.core.script.rule;

import org.geelato.core.AbstractManager;
import org.geelato.core.script.js.JsProvider;
import org.geelato.core.script.js.JsTemplateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.script.Bindings;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author geemeta
 */
public class BizRuleScriptManager extends AbstractManager {

    private static Logger logger = LoggerFactory.getLogger(BizRuleScriptManager.class);
    private JsTemplateParser jsTemplateParser = new JsTemplateParser();
    private JsProvider jsProvider = new JsProvider();

    /**
     * 解析*.js的文件
     *
     * @param file
     */
    @Override
    public void parseFile(File file) throws IOException {
        compileJs(jsTemplateParser.parse(Files.readAllLines(Paths.get(file.getPath()))));
    }

    /**
     * 解析*.js的文件流
     * @param inputStream
     */
    public void parseStream(InputStream inputStream) throws IOException {
        compileJs(jsTemplateParser.parse(readLines(inputStream)));
    }

    private void compileJs(Map<String, String> jsFuncMap) {
        try {
            jsProvider.compile(jsFuncMap);
        } catch (ScriptException e) {
            logger.error("", e);
        }
    }

    @Override
    public void loadDb(String sqlId) {

    }


    /**
     * @param functionName functionName
     * @param bindings     bindings中put的key与函数的参数名称需一致，{@link Bindings}
     * @return 执行结果
     * @throws ScriptException 脚本执行错误
     */
    public <T> T execute(String functionName, Bindings bindings) {
        if (jsProvider.contain(functionName))
            try {
                T result = jsProvider.execute(functionName, bindings);
                if (logger.isInfoEnabled()) {
                    logger.info("execute {} : {}", functionName, result);
                }
                return result;
            } catch (ScriptException e) {
                logger.error("脚本执行失败。function:" + functionName + "。", e);
                return null;
            }
        else {
            Assert.isTrue(false, "未找到function：" + functionName + "，对应的函数。");
            return null;
        }
    }


}
