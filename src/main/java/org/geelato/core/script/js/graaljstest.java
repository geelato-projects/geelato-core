package org.geelato.core.script.js;

import org.geelato.core.Ctx;
import org.geelato.core.orm.Dao;
import org.geelato.core.script.sql.SqlScriptLexer;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;

public class graaljstest {
    public static void  test(String args){
        System.out.println("[Java] Hello, Java!");
        try (Context context = Context.newBuilder("js").option("js.ecmascript-version", "2020").build()) {
            Value jsFunction = context.eval("js", "" +
                    "(function myJavaScriptFunction(parameter,par2) {         \n" +
                    "    console.log('[JS] Hello, ' + parameter + '!');  \n" +
                    "    console.log('[JS] Hello, ' + JSON.stringify(par2) + '!');  \n" +
                    "    return parameter.toUpperCase();                 \n" +
                    "})                                                  \n");

            Value result = jsFunction.execute(args);
            if (result.isString()) {
                System.out.println("[Java] result: " + result.asString());
            } else {
                System.out.println("[Java] unexpected result type returned from JavaScript");
            }
        }
    }


    public static void  main(String[] args){
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                //allows access to all Java classes
                .allowHostClassLookup(className -> true)
                .build();
        Value value = context.eval("js",
                "" +
                        "var DbOp = Java.type('org.geelato.core.script.js.DbOp');var dbop = new DbOp();" +
                        "var HashMap = Java.type('java.util.HashMap');var map = new HashMap();" +
                        "map.put('login_name', 'admin');" +
                        "dbop.test(map);"); //执行类中方法并传递map参数
    }

}