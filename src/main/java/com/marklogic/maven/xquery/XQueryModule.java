package com.marklogic.maven.xquery;

public class XQueryModule {
    public static String invokeFunction(final String functionPrefix, final String functionName, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(functionPrefix).append(':').append(functionName).append('(');
        if(args.length > 0) {
            sb.append(args[0]);
            for (int i = 1; i < args.length; i++) {
                sb.append(',').append(args[i]);
            }
        }
        sb.append(')');
        return sb.toString();
    }

    public static String quote(final String value) {
        return quote(value, true);
    }

    public static String quote(final String value, boolean single) {
        StringBuilder sb = new StringBuilder();
        if (single) {
            sb.append("'").append(value).append("'");
        } else {
            sb.append('"').append(value).append('"');
        }
        return sb.toString();
    }

    public static String importModule(final String prefix, final String namespace, final String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("import module namespace ").append(prefix).append(" = ")
                .append(quote(namespace)).append(" at ").append(quote(path)).append(";\n");
        return sb.toString();
    }
}