package org.github.caishijun.javaagent.trace;

import java.util.ArrayList;
import java.util.List;

public class TraceMethod {

    private List<TraceMethod> traceMethods = new ArrayList<>();
    private long startTime = System.currentTimeMillis();
    private boolean isFinish = false;

    private TraceMethod parentTraceMethod;

    public TraceMethod(){

    }


}
