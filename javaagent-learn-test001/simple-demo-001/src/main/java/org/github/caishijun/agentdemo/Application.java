package org.github.caishijun.agentdemo;

public class Application {

    /**
     * -javaagent: XXXX.jar
     */
    public static void main(String[] args) {
        hello();
    }

    public static void hello() {
        System.out.println("this is agent-demo output");
    }
}