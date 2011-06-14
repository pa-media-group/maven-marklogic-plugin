package com.marklogic.maven;

/**
 * Created by IntelliJ IDEA.
 * User: bobb
 * Date: 14/06/2011
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
public class XQueryDocumentBuilder {

    private static final String XQUERY_PROLOG = "xquery version '1.0-ml';";

    protected StringBuilder sb;

    public XQueryDocumentBuilder() {
        sb = new StringBuilder(XQUERY_PROLOG).append('\n');
    }

    public XQueryDocumentBuilder append(String value) {
        sb.append(value);
        return this;
    }

    public XQueryDocumentBuilder assign(final String variableName, final String value) {
        sb.append("let ").append(variable(variableName)).append(" := ").append(value);
        return this.newline();
    }

    public XQueryDocumentBuilder newline() {
        sb.append("\n");
        return this;
    }

    public XQueryDocumentBuilder doReturn(final String value) {
        sb.append("return ").append(value);
        return this.newline();
    }

    public String variable(final String name) {
        return (name.startsWith("$")) ? name : "$".concat(name);
    }

    public String trycatch(final String xquery) {
        return trycatch(xquery, "()");
    }

    public String trycatch(final String xquery, final String catchBlock) {
        return "try { " + xquery + " } catch ($e) { " + catchBlock + " }";
    }

    public String toString() {
        return sb.toString();
    }

}
