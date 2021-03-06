package org.github.caishijun.javaagent.transformer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonitorTransformer implements ClassFileTransformer {

    final static String prefix = "\nlong startTime = System.currentTimeMillis();\n";
    final static String postfix = "\nlong endTime = System.currentTimeMillis();\n";
    final static Map<String, List<String>> injectedClassAndMethod = new HashMap<>();

    static {
        String injectedClassName = "org.github.caishijun.agentdemo.Application";
        List<String> injectedMethodNames = new ArrayList<>();
        injectedMethodNames.add("sayHello");
        injectedMethodNames.add("sayHello2");

        injectedClassAndMethod.put(injectedClassName, injectedMethodNames);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //通过instrumentation进来的class路径用‘/’分割，将‘/’替换为‘.’m比如monitor/agent/Mytest替换为monitor.agent.Mytest
        className = className.replace("/", ".");
        //System.out.println(className);
        //先判断下现在加载的class的包路径是不是需要监控的类，并获取监控的方法名
        List<String> injectedMethodNames = injectedClassAndMethod.get(className);
        if (injectedMethodNames != null && injectedMethodNames.size() != 0) {
            CtClass ctclass = null;
            try {
                //用于取得字节码类，必须在当前的classpath中，使用全称 ,这部分是关于javassist的知识
                ctclass = ClassPool.getDefault().get(className);
                //循环一下，看看哪些方法需要加时间监测
                for (String methodName : injectedMethodNames) {
                    //获取方法名
                    String outputStr = "\nSystem.out.println(\"this method " + methodName + " cost:\" +(endTime - startTime) +\"ms.\");";
                    //得到这方法实例
                    CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);
                    //新定义一个方法叫做比如sayHello$impl
                    String newMethodName = methodName + "$impl";
                    //原来的方法改个名字
                    ctmethod.setName(newMethodName);

                    //创建新的方法，复制原来的方法 ，名字为原来的名字
                    CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctclass, null);
                    //构建新的方法体
                    StringBuilder bodyStr = new StringBuilder();
                    bodyStr.append("{");
                    bodyStr.append(prefix);
                    //调用原有代码，类似于method();($$)表示所有的参数
                    bodyStr.append(newMethodName + "($$);\n");

                    bodyStr.append(postfix);
                    bodyStr.append(outputStr);

                    bodyStr.append("}");
                    //替换新方法
                    newMethod.setBody(bodyStr.toString());
                    //增加新方法
                    ctclass.addMethod(newMethod);
                }
                return ctclass.toBytecode();
            } catch (IOException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CannotCompileException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotFoundException e) {
                //TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
}