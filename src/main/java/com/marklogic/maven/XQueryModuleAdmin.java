package com.marklogic.maven;

public class XQueryModuleAdmin extends XQueryModule {

    public static final String ADMIN_MODULE_PREFIX = "admin";

    public static final String ADMIN_MODULE_NS = "http://marklogic.com/xdmp/admin";

    public static final String ADMIN_MODULE_PATH = "/MarkLogic/admin.xqy";

    public static String getConfiguration() {
        return invokeFunction(ADMIN_MODULE_PREFIX, "get-configuration");
    }

    public static String attachForest(final String configuration, final String databaseName, final String forestName) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "database-attach-forest", configuration, databaseName, forestName);
    }

    public static String databaseCreate(final String configuration, final String name) {
        return databaseCreate(configuration, name, XQueryModuleXDMP.database("Security"), XQueryModuleXDMP.database("Schemas"));
    }

    public static String databaseCreate(final String configuration,
                                        final String databaseName,
                                        final String securityDatabase,
                                        final String schemasDatabase) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "database-create",
                configuration, quote(databaseName), securityDatabase, schemasDatabase);
    }

    public static String forestCreate(final String configuration,
                                      final String forestName) {
        return forestCreate(configuration, forestName, XQueryModuleXDMP.host());
    }

    public static String forestCreate(final String configuration,
                                      final String forestName,
                                      final String host) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "forest-create",
                configuration, quote(forestName), host, "()");
    }

    public static String groupGetId(final String configuration, final String name) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "group-get-id", configuration, quote(name));
    }

    public static String importModule() {
        return importModule(ADMIN_MODULE_PREFIX, ADMIN_MODULE_NS, ADMIN_MODULE_PATH);
    }

    public static String saveConfiguration(final String configuration) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "save-configuration", configuration);
    }

    public static String webdavServerCreate(final String configuration,
                                            final String serverName,
                                            final String moduleRoot,
                                            final int port,
                                            final String database) {
        return webdavServerCreate(configuration, groupGetId(configuration, "Default"), serverName, moduleRoot, port, database);
    }

    public static String webdavServerCreate(final String configuration,
                                            final String groupId,
                                            final String serverName,
                                            final String moduleRoot,
                                            final int port,
                                            final String database) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "webdav-server-create", configuration,
                groupId, quote(serverName), quote(moduleRoot), Integer.toString(port), database);
    }

    public static String xdbcServerCreate(final String configuration,
                                            final String serverName,
                                            final String moduleRoot,
                                            final int port,
                                            final String modulesDatabase,
                                            final String database) {
        return xdbcServerCreate(configuration, groupGetId(configuration, "Default"), serverName, moduleRoot, port,
                modulesDatabase, database);
    }

    public static String xdbcServerCreate(final String configuration,
                                            final String groupId,
                                            final String serverName,
                                            final String moduleRoot,
                                            final int port,
                                            final String modulesDatabase,
                                            final String database) {
        return invokeFunction(ADMIN_MODULE_PREFIX, "xdbc-server-create", configuration,
                groupId, quote(serverName), quote(moduleRoot), Integer.toString(port), modulesDatabase, database);
    }

}