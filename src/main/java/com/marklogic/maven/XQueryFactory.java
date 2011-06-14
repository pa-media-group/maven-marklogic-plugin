package com.marklogic.maven;

/**
 * Created by IntelliJ IDEA.
 * User: bobb
 * Date: 14/06/2011
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
public class XQueryFactory {
    public static String getDatabase(final String name) {
        String destinationVariableName = name.replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
        return getDatabase(name, destinationVariableName);
    }

    public static String getDatabase(final String name, final String destinationVariableName) {
        return "let $" + destinationVariableName + " := xdmp:database('" + name + "')\n";
    }

    public static String getAdminConfiguration() {
        return "let $config := admin:get-configuration()\n";
    }

    public static String createDatabase(final String name) {
        return "let $config := admin:database-create( $config, '" +
                name + "', xdmp:database('Security'), xdmp:database('Schemas') )\n";
    }

    public static String saveAdminConfiguration() {
        return saveAdminConfiguration(true);
    }

    public static String saveAdminConfiguration(boolean shouldReturnValue) {
        return ((shouldReturnValue) ? "return " : "let $_ := ") + "admin:save-configuration($config)\n";
    }

    public static String transactionDelimiter() {
        return ";\n";
    }

    public static String createForest(final String name) {
        return "let $config := admin:forest-create( $config, '" + name + "', xdmp:host(), () )\n";
    }

    public static String attachForest(final String databaseName, final String forestName) {
        return "let $config := admin:database-attach-forest( $config, xdmp:database('" +
                databaseName + "'), xdmp:forest('" + forestName + "') )\n";
    }

    public static String createWebDavServer(final String serverName, final String moduleRoot,
                                            final int port, final String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append("let $config := admin:webdav-server-create( $config                                 \n");
        sb.append("                                         , admin:group-get-id($config, 'Default')  \n");
        sb.append("                                         , '" + serverName + "'                    \n");
        sb.append("                                         , '" + moduleRoot + "'                    \n");
        sb.append("                                         ,  " + port + "                           \n");
        sb.append("                                         , xdmp:database('" + databaseName + "'))  \n");
        return sb.toString();
    }

    public static String createXDBCServer(final String serverName, final String moduleRoot, final int port,
                                            final String modulesDatabaseName, final String databaseName) {
        StringBuilder sb = new StringBuilder();
        sb.append("let $config := admin:xdbc-server-create( $config                                      \n");
        sb.append("                                       , admin:group-get-id($config, 'Default')       \n");
        sb.append("                                       , '" + serverName + "'                         \n");
        sb.append("                                       , '" + moduleRoot + "'                         \n");
        sb.append("                                       ,  " + port + "                                \n");
        sb.append("                                       , xdmp:database('" + modulesDatabaseName + "') \n");
        sb.append("                                       , xdmp:database('" + databaseName + "') )      \n");
        return sb.toString();
    }

    public static String eval(final String xquery) {
        return eval(xquery, false);
    }

    public static String eval(final String xquery, boolean sameTransaction) {
        String options = "<options xmlns=\"xdmp:eval\"><isolation>" +
                ((sameTransaction) ? "same-statement" : "different-transaction") + "</isolation></options>";
        return "let $result := xdmp:eval(\"" + xquery + "\", (), " + options + ") \n";
    }

    public static String tryCatch(final String xquery) {
        return tryCatch(xquery, "()");
    }

    public static String tryCatch(final String xquery, final String catchBlock) {
        return "let $_ := try { " + xquery + " } catch ($e) { " + catchBlock + " } \n";
    }

    public static String log(final String xquery) {
        return "let $_ := xdmp:log(" + xquery + ") \n";
    }
}
