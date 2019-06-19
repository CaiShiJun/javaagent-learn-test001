package org.github.caishijun.javaagent;

import org.github.caishijun.javaagent.transformer.MonitorTransformer;

import java.lang.instrument.Instrumentation;

public class AgentStart {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("=========premain方法执行========");
        System.out.println(agentArgs);

        // 添加Transformer
        inst.addTransformer(new MonitorTransformer());
    }
}
