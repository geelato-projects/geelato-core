package org.geelato.core.graaljs;

import org.geelato.core.Ctx;
import org.geelato.core.orm.Dao;
import org.geelato.core.script.sql.SqlScriptLexer;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class graaljstest {
    public static void main1(String[] args){
        args= new String[]{"fuck", "shit"};
        List<String> sList=new ArrayList<>();
        sList.add("fuck");
        sList.add("shit");
        System.out.println("[Java] Hello, Java!");
        try (Context context = Context.newBuilder("js").option("js.ecmascript-version", "2020").build()) {
            Value jsFunction = context.eval("js", "" +
                    "(function myJavaScriptFunction(parameter) {    \n" +
                    "    console.log('[JS] Hello, ' + parameter + '!');  \n" +
                    "    console.log('[JS] Hello, ' + JSON.stringify(parameter) + '!');  \n" +
                    "    return parameter.toUpperCase();                 \n" +
                    "})                                                  \n");

            Value result = jsFunction.execute(sList);
            if (result.isString()) {
                System.out.println("[Java] result: " + result.asString());
            } else {
                System.out.println("[Java] unexpected result type returned from JavaScript");
            }
        }
    }


    public static void main(String gql){
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                //allows access to all Java classes
                .allowHostClassLookup(className -> true)
                .build();
//        Value value = context.eval("js",
//                "" +
//                        "var DbOp = Java.type('org.geelato.core.graaljs.DbOp');var dbop = new DbOp();" +
//                        "var HashMap = Java.type('java.util.HashMap');var map = new HashMap();" +
//                        "map.put('login_name', 'admin');" +
//                        "dbop.test(map);"); //执行类中方法并传递map参数

                Value value = context.eval("js",
                        "var metaOperator = Java.type('org.geelato.web.platform.script.MetaOperator');" +
                        "var gql='"+gql+"';" +
                        "var data=metaOperator.list(gql);"+
                        "console.log(JSON.stringify(data))"); //执行类中方法并传递map参数
    }

}