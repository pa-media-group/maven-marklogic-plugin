package com.marklogic.maven;

public class XQueryModuleXDMP extends XQueryModule {
    public static final String XDMP_MODULE_PREFIX = "xdmp";

    public static String database(final String name) {
        return invokeFunction(XDMP_MODULE_PREFIX, "database", quote(name));
    }

    public static String eval(final String xquery) {
        return eval(xquery, "()", "<options xmlns='xdmp:eval'><isolation>different-transaction</isolation></options>");
    }

    public static String eval(final String xquery, final String variables, final String options) {
        return invokeFunction(XDMP_MODULE_PREFIX, "eval", quote(xquery, false), variables, options);
    }

    public static String forest(final String name) {
        return invokeFunction(XDMP_MODULE_PREFIX, "forest", quote(name));
    }

    public static String host() {
        return invokeFunction(XDMP_MODULE_PREFIX, "host");
    }

    public static String log(final String message) {
        return invokeFunction(XDMP_MODULE_PREFIX, "log", message);
    }
}