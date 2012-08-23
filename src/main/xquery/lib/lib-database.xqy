xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-db = 'http://www.marklogic.com/ps/lib/lib-database.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst-idx = "http://www.marklogic.com/ps/lib/lib-index.xqy" at "/lib/lib-index.xqy";
import module namespace inst-fld = "http://www.marklogic.com/ps/lib/lib-field.xqy" at "/lib/lib-field.xqy";
import module namespace inst-db-set = "http://www.marklogic.com/ps/lib/lib-database-set.xqy" at "/lib/lib-database-set.xqy";
import module namespace inst-db-add = "http://www.marklogic.com/ps/lib/lib-database-add.xqy" at "/lib/lib-database-add.xqy";

declare namespace inst-conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
    <database name="DatabaseName">
        
        <schema-database name="{SchemaDatabaseName}"/>
        <triggers-database name="{TriggersDatabaseName}"/>
        <security-database name="{SecurityDatabaseName}"/>
        ...
        <forest name="ForestName">
            <forest-directory/>
        </forest>
        ...
        <field/>
        ...
        <set name="{PropertyName}" value="{PropertyValue}"/> 
        ...
    </database>
:)

declare function  inst-db:install-databases($install-config)
{(
    inst-db:create-databases($install-config),
    inst-db:create-forests($install-config),
    inst-db:attach-forests($install-config),
    inst-db:add-triggers-to-databases($install-config),
    inst-db:add-schema-to-databases($install-config),
    inst-db:add-security-to-databases($install-config),
    inst-db-set:do-sets($install-config),
    inst-db-add:do-adds($install-config)
)};

declare function  inst-db:update-databases($install-config)
{(
    inst-db:add-triggers-to-databases($install-config),
    inst-db:add-schema-to-databases($install-config),
    inst-db:add-security-to-databases($install-config),
    inst-db-set:do-sets($install-config),
    inst-db-add:do-adds($install-config)
)};

declare function  inst-db:uninstall-databases($install-config, $delete-data)
{(
	inst-db:detach-forests($install-config),
    inst-db:delete-databases($install-config),
    inst-db:delete-forests($install-config, $delete-data)
)};

declare function  inst-db:mk-database-name-from-string($install-config, $short-name)
{
    if($short-name eq "0") then "file-system"
    else if ($install-config/inst-conf:application/@name ne "null") then
        fn:concat($install-config/inst-conf:application/@name, "-", $short-name)
    else $short-name
};

declare function  inst-db:mk-database-name($install-config, $database)
{
    inst-db:mk-database-name-from-string($install-config, $database/@name)
};

declare function  inst-db:mk-forest-name($install-config, $forest)
{
    if ($install-config/inst-conf:application/@name ne "null") then
        fn:concat($install-config/inst-conf:application/@name, "-", $forest/@name)
    else $forest/@name
};

declare function  inst-db:create-database($database-name)
{
    let $LOG := xdmp:log(text{"Creating Database:", $database-name})
    let $config := admin:get-configuration()
    return
    if (admin:database-exists($config, $database-name)) then
        ()
    else  
        let $config := admin:database-create($config, $database-name,xdmp:database("Security"), xdmp:database("Schemas"))
        return admin:save-configuration($config)
};

declare function inst-db:create-databases($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
            inst-db:create-database($database-name)
};

declare function  inst-db:create-forest($forest-name)
{
    let $LOG := xdmp:log(text{"Creating Forest:", $forest-name})
    let $config := admin:get-configuration()
    return
    if (admin:forest-exists($config, $forest-name)) then
        ()
    else
        let $config := admin:forest-create($config, $forest-name, xdmp:host(), ())
        return admin:save-configuration($config)
};

declare function inst-db:create-forests($install-config)
{
    for $forest in $install-config/inst-conf:database/inst-conf:forest
        let $forest-name := inst-db:mk-forest-name($install-config, $forest)
        return
            inst-db:create-forest($forest-name)
};

declare function  inst-db:attach-forest($database-name, $forest-name)
{
    let $LOG := xdmp:log(text{"Attaching Forest:", $forest-name, "to Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-attach-forest($config, xdmp:database($database-name), xdmp:forest($forest-name))}
        catch ($e) {(xdmp:log("skipping forest attach (may already be attached)"), $config)}
        
    return admin:save-configuration($config)
};

declare function inst-db:attach-forests($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
        for $forest in $database/inst-conf:forest
            let $forest-name := inst-db:mk-forest-name($install-config, $forest)
            return
            inst-db:attach-forest($database-name, $forest-name)           
};

declare function  inst-db:add-triggers-to-database($database-name, $triggers-name)
{
    let $LOG := xdmp:log(text{"Adding Triggers:", $triggers-name, "to Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-set-triggers-database($config, xdmp:database($database-name), xdmp:database($triggers-name))
}
        catch ($e) {(xdmp:log("skipping add triggers database (may already be added)"), $config)}
        
    return admin:save-configuration($config)
};

declare function inst-db:add-triggers-to-databases($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        let $triggers-base-name := ($database/inst-conf:triggers-database/@name)[1]
        return
            if($triggers-base-name) then
                let $triggers-name := inst-db:mk-database-name-from-string($install-config, $triggers-base-name)
                return inst-db:add-triggers-to-database($database-name, $triggers-name)
            else ()        
};

declare function  inst-db:add-schema-to-database($database-name, $schema-name)
{
    let $LOG := xdmp:log(text{"Adding Schema:", $schema-name, "to Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-set-schema-database($config, xdmp:database($database-name), xdmp:database($schema-name))
}
        catch ($e) {(xdmp:log("skipping add schema database (may already be added)"), $config)}
        
    return admin:save-configuration($config)
};

declare function inst-db:add-schema-to-databases($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        let $schema-base-name := ($database/inst-conf:schema-database/@name)[1]
        return
            if($schema-base-name) then
                let $schema-name := inst-db:mk-database-name-from-string($install-config, $schema-base-name)
                return inst-db:add-schema-to-database($database-name, $schema-name)
            else ()        
};

declare function  inst-db:add-security-to-database($database-name, $security-name)
{
    let $LOG := xdmp:log(text{"Adding Security:", $security-name, "to Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-set-security-database($config, xdmp:database($database-name), xdmp:database($security-name))
}
        catch ($e) {(xdmp:log("skipping add security database (may already be added)"), $config)}
        
    return admin:save-configuration($config)
};

declare function inst-db:add-security-to-databases($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        let $security-base-name := ($database/inst-conf:security-database/@name)[1]
        return
            if($security-base-name) then
                let $security-name := inst-db:mk-database-name-from-string($install-config, $security-base-name)
                return inst-db:add-security-to-database($database-name, $security-name)
            else ()        
};

declare function  inst-db:detach-forest($database-name, $forest-name)
{
    let $LOG := xdmp:log(text{"Detaching Forest:", $forest-name, "from Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-detach-forest($config, xdmp:database($database-name), xdmp:forest($forest-name))}
        catch ($e) {(xdmp:log("skipping forest detach (may not be attached)"), $config)}
        
    return admin:save-configuration-without-restart($config)
};

declare function inst-db:detach-forests($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
        for $forest in $database/inst-conf:forest
            let $forest-name := inst-db:mk-forest-name($install-config, $forest)
            return
            inst-db:detach-forest($database-name, $forest-name)           
};

declare function  inst-db:delete-database($database-name)
{   
    let $LOG := xdmp:log(text{"Deleting Database:", $database-name})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:database-delete($config, xdmp:database($database-name))} 
        catch ($e) {(xdmp:log(fn:concat("skipping db delete (may not exist) ", $e/*:message/text())), $config)}
        
    return admin:save-configuration-without-restart($config)
};

declare function inst-db:delete-databases($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
            inst-db:delete-database($database-name)
};

declare function  inst-db:delete-forest($forest-name, $delete-data as xs:boolean)
{
    let $LOG := xdmp:log(text{"Deleting Forest:", $forest-name, "; Delete-Data =",$delete-data})
    let $config := admin:get-configuration()
    let $config := 
        try {admin:forest-delete($config, xdmp:forest($forest-name), $delete-data)} 
        catch ($e) {(xdmp:log("skipping forest delete (may not exist)"), $config)}
        
    return admin:save-configuration($config)
};

declare function inst-db:delete-forests($install-config, $delete-data as xs:boolean)
{
    for $forest in $install-config/inst-conf:database/inst-conf:forest
        let $forest-name := inst-db:mk-forest-name($install-config, $forest)
        return
            inst-db:delete-forest($forest-name, $delete-data)
};

