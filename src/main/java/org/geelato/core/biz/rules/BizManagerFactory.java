package org.geelato.core.biz.rules;

import org.geelato.core.script.rule.BizRuleScriptManager;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author geemeta
 *
 */
public class BizManagerFactory {
    private static Lock lock = new ReentrantLock();
    private static HashMap<String, BizRuleScriptManager> map = new HashMap<>();

    private BizManagerFactory() {
    }

    public static BizRuleScriptManager get(String name) {
        lock.lock();
        if (!map.containsKey(name))
            map.put(name, new BizRuleScriptManager());
        lock.unlock();
        return map.get(name);
    }
}
