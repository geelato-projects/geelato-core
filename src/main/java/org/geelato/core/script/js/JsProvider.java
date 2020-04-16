package org.geelato.core.script.js;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 先compile之后再调用{@link #execute}
 *
 * @author geemeta
 */
public class JsProvider {

    private static Logger logger = LoggerFactory.getLogger(JsProvider.class);
    protected ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private Map<String, CompiledScriptInfo> compiledScriptMap = new HashMap<>();
    // 格式例如：funName(a,b,c)
    private static Pattern callScriptPattern = Pattern.compile("[\\S]*[\\s]?\\([\\S]*\\)");


    public JsProvider() {
    }

    /**
     * 编译多个js函数（function）片段
     *
     * @param jsFuncMap
     * @throws ScriptException
     */
    public void compile(Map<String, String> jsFuncMap) throws ScriptException {
        if (jsFuncMap == null) return;
        for (Map.Entry<String, String> entry : jsFuncMap.entrySet()) {
            if (compiledScriptMap.containsKey(entry.getKey())) {
                logger.warn("存在同名称key：{},不进行解析！", entry.getKey());
            } else {
                compiledScriptMap.put(entry.getKey(), compile(entry.getKey(), entry.getValue()));
            }
        }
    }

    /**
     * 在编译的同时，在function的结尾默认追加一条调用语句如：“;fun1(1,2)”
     *
     * @param functionName   函数名
     * @param scriptText javascript function脚本片段，有具只有一个function,
     *                   格式如function fun1(a,b){return a+b}
     * @return
     * @throws ScriptException
     */
    public CompiledScriptInfo compile(String functionName, String scriptText) throws ScriptException {
        ScriptEngine engine = scriptEngineManager.getEngineByName("javascript");
        // functionName + "(" + SqlScriptParser.VAL_NAME + ");"
        CompiledScript functionScript = ((Compilable) engine).compile(scriptText);
        CompiledScript commandScript = ((Compilable) engine).compile(matcherFnCallScript(scriptText));
        return new CompiledScriptInfo(functionName, functionScript, commandScript);
    }

    /**
     * @param fnScriptText 完整的function脚本
     * @return 匹配脚本中的第一个function，取functionName(args..)，用于作调用function的执行脚本
     */
    private String matcherFnCallScript(String fnScriptText) {
        Matcher matcher = callScriptPattern.matcher(fnScriptText);
        if (matcher.find())
            return matcher.group();
        else
            throw new RuntimeException("未能匹配callScriptPattern，待匹配的fnScriptText为：" + fnScriptText);
    }

    public boolean contain(String functionName) {
        return compiledScriptMap.containsKey(functionName);
    }

    /**
     * 调用预编译js脚本中的函数
     *
     * @param functionName 函数名称
     * @param bindings 调用参数
     * @param <T>      执行结果类型
     * @return 执行结果
     * @throws ScriptException 脚本错误
     */
    public <T> T execute(String functionName, Bindings bindings) throws ScriptException {
        CompiledScriptInfo compiledScriptInfo = compiledScriptMap.get(functionName);
        compiledScriptInfo.getFunctionScript().eval(bindings);
        return (T) compiledScriptInfo.getCommandScript().eval(bindings);
    }

    /**
     * 编译的脚本信息
     */
    class CompiledScriptInfo {
        private String functionName;
        private String description;
        // 函数脚本
        private CompiledScript functionScript;
        // 调用该函数的脚本
        private CompiledScript commandScript;

        public CompiledScriptInfo(String functionName, CompiledScript functionScript, CompiledScript commandScript) {
            this.functionScript = functionScript;
            this.commandScript = commandScript;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public CompiledScript getFunctionScript() {
            return functionScript;
        }

        public CompiledScript getCommandScript() {
            return commandScript;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
