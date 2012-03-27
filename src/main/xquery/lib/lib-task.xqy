xquery version "1.0-ml";

module namespace inst-task = 'http://www.marklogic.com/ps/lib/lib-task.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst = "http://www.marklogic.com/ps/lib/lib-install.xqy" at "/lib/lib-install.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";
declare namespace group = "http://marklogic.com/xdmp/group";


declare function  inst-task:install-tasks($install-config)
{
    inst-task:uninstall-tasks($install-config),
    
    for $database in $install-config//conf:database 
    let $tasks := $database/conf:task       
    return
    if($tasks) then
        inst-task:add-tasks($install-config, $database, $tasks)
    else ()
};

declare function  inst-task:uninstall-tasks($install-config)
{      
    for $database in $install-config//conf:database
    let $tasks := $database/conf:task       
    return
    if($tasks) then
        inst-task:uninstall-database-tasks($install-config, $database, $tasks)
    else ()
};

declare function  inst-task:uninstall-database-tasks($install-config, $database as element(conf:database),
    $tasks as element(conf:task)*)
{
    let $config := admin:get-configuration()
    let $current-tasks := admin:group-get-scheduled-tasks($config,
        admin:group-get-id($config, "Default"))
        
    let $content-database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
    let $content-db-id := xdmp:database($content-database-name)
    let $tasks-to-remove :=
        for $task in $tasks
        let $module-db-id :=
            xdmp:database(inst-db:mk-database-name-from-string($install-config, $task/conf:module/@database))
        return
        for $curr-task in $current-tasks
        where $curr-task/group:task-database = $content-db-id and
            $curr-task/group:task-modules = $module-db-id  and
            $curr-task/group:task-path = $task/conf:module and
            $curr-task/group:task-root = $task/conf:module/@path
        return $curr-task
        
    return
    if ($tasks-to-remove) then
    (   xdmp:log(fn:concat("Removing tasks for database ", $content-database-name, ": ", xdmp:quote($tasks-to-remove))),
        let $config := admin:get-configuration()  
        let $new-config := admin:group-delete-scheduled-task($config, admin:group-get-id($config, "Default"),
            $tasks-to-remove)
        return
            admin:save-configuration-without-restart($new-config)    
    )   
    else ()
};

declare function inst-task:add-tasks($install-config as node(), $database as element(conf:database), $tasks as element(conf:task)*) {
    let $content-database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
    let $content-db-id := xdmp:database($content-database-name)
    
    for $task in $tasks
    let $config := admin:get-configuration()
    let $group-id := admin:group-get-id($config, "Default")
    let $type := $task/conf:type/fn:string()
    let $path := $task/conf:module/fn:string()
    let $root := $task/conf:module/@path/fn:string()
    let $user := xdmp:user($task/conf:user/fn:string())
    let $host :=
        if ($task/conf:host) then
            admin:host-get-id($config, $task/conf:host/fn:string())
        else ()  
    let $module-db-id :=
            xdmp:database(inst-db:mk-database-name-from-string($install-config, $task/conf:module/@database))
    let $scheduled-task :=
        if ($type = "one-time") then
            admin:group-one-time-scheduled-task($path, $root, xs:dateTime($task/conf:start),
                $content-db-id, $module-db-id, $user, $host)
        else if ($type = "monthly") then
            admin:group-monthly-scheduled-task($path, $root, xs:positiveInteger($task/conf:period),
                xs:unsignedLong($task/conf:month-day), xs:time($task/conf:start-time),
                $content-db-id, $module-db-id, $user, $host)        
        else if ($type = "weekly") then
            admin:group-weekly-scheduled-task($path, $root, xs:positiveInteger($task/conf:period),
                $task/conf:day/fn:string(), xs:time($task/conf:start-time),
                $content-db-id, $module-db-id, $user, $host)                
        else if ($type = "daily") then
            admin:group-daily-scheduled-task($path, $root, xs:positiveInteger($task/conf:period),
                xs:time($task/conf:start-time),
                $content-db-id, $module-db-id, $user, $host)        
        else if ($type = "hourly") then
            admin:group-hourly-scheduled-task($path, $root, xs:positiveInteger($task/conf:period),
                xs:nonNegativeInteger($task/conf:minute),
                $content-db-id, $module-db-id, $user, $host)        
        else if ($type = "minutely") then
            admin:group-minutely-scheduled-task($path, $root, xs:positiveInteger($task/conf:period),
                $content-db-id, $module-db-id, $user, $host)                
        else ()
    let $new-config := 
        admin:group-add-scheduled-task($config, $group-id, $scheduled-task)
    return
    (xdmp:log(fn:concat("Installing task: ", xdmp:quote($scheduled-task))),
    admin:save-configuration-without-restart($new-config))    
};