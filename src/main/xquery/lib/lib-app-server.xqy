xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-app = 'http://www.marklogic.com/ps/lib/lib-app-server.xqy';

import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUTS :::
    <servers>
        <server type="http"   name="Content" port="9000" group="Default" database="Content"   root="/" modules="0"/>
        <server type="xdb"    name="Content" port="9001" group="Default" database="Content"   root="/" modules="Modules"/>
        <server type="webdav" name="Content" port="9002" group="Default" database="Content"   root="/" modules="0"/>
        <server type="webdav" name="Modules" port="9003" group="Default" database="Modules"   root="/" modules="0"/>
    </servers>
:::)

declare function  inst-app:install-servers($install-config)
{
    for $server in $install-config/conf:servers/conf:server
       return inst-app:create-server($install-config, $server)
};

declare function  inst-app:uninstall-servers($install-config)
{
    for $server in $install-config/conf:servers/conf:server
       return inst-app:delete-server($install-config, $server)
};

declare function  inst-app:mk-server-name($config, $server)
{
    fn:concat($server/@port, "-", $config/conf:application/@name, "-", $server/@name)
};

declare function inst-app:create-server($install-config, $server as element())
{
    let $server-name := mk-server-name($install-config, $server)
    let $LOG := xdmp:log(text{"Creating Server:", $server-name})
    let $server-port := $server/@port
    let $database-name := inst-db:mk-database-name-from-string($install-config, $server/@database)
    let $database := xdmp:database($database-name)
    let $config := admin:get-configuration()
    let $group-id := admin:group-get-id($config, xs:string($server/@group))
    let $modules := $server/@modules
    let $modules := if($modules eq "0") then "file-system" else xdmp:database(inst-db:mk-database-name-from-string($install-config, $modules))
    let $root := $server/@root
    let $collation := "http://marklogic.com/collation/codepoint"
 
    let $config := 
        try {
            if ($server/@type = "webdav") then (
                xdmp:log(fn:concat("Creating webdav: ", $root)), 
                admin:webdav-server-create($config, $group-id, $server-name, $root, $server-port, $database )
            ) else if ($server/@type = "http") then (
                xdmp:log(fn:concat("Creating http: ", $root)), 
                admin:http-server-create($config, $group-id, $server-name, $root, $server-port, $modules, $database )
            ) else if ($server/@type = "xdb") then (
                xdmp:log(fn:concat("Creating xdbc: ", $root)), 
                admin:xdbc-server-create($config, $group-id, $server-name, $root, $server-port, $modules, $database )
            ) else (
                xdmp:log("Unknown"),
                $config
            ) 
        }catch ($e) {
            (xdmp:log("skipping server create (may already exist or the port may be in use)"), $config)
        }
        
    let $server-id := admin:appserver-get-id($config, $group-id, $server-name)
    let $config := admin:appserver-set-collation($config, $server-id, $collation)
    let $config := admin:save-configuration-without-restart($config)
    
    (::: Set Auth type :::)
    let $config := admin:get-configuration()
    let $authentication := $server/@authentication 
    let $config := if($authentication) then
                let $LOG := xdmp:log(text{"Authentication = ",$authentication})
                return admin:appserver-set-authentication($config, $server-id, $authentication)
            else
                 $config
    
    let $config := admin:save-configuration-without-restart($config)
    
    (::: Set Default Auth User :::)
    let $config := admin:get-configuration()
    let $default-user := $server/@default-user
    let $config :=
        if($default-user) then
            let $LOG := xdmp:log(text{"Default User = ",$default-user})
            let $xqy := '   xquery version "1.0-ml";
                            import module "http://marklogic.com/xdmp/security" at "/MarkLogic/security.xqy";
                            declare variable $default-user as xs:string external; 
                            sec:uid-for-name($default-user)'
                            
            let $uid := xdmp:eval($xqy, (xs:QName("default-user"), $default-user),  
                        <options xmlns="xdmp:eval">
                           <database>{xdmp:security-database()}</database>
                         </options>)
            return admin:appserver-set-default-user($config, $server-id, $uid)
        else
            $config
    
    return admin:save-configuration-without-restart($config)
};

declare function inst-app:delete-server($install-config, $server as element()*)
{
    let $server-name := mk-server-name($install-config, $server)
    let $LOG := xdmp:log(text{"Deleting Server:", $server-name})
    let $config := admin:get-configuration()
    let $config := 
        try {
            admin:appserver-delete($config, xdmp:server($server-name))
        }
        catch ($e){
            (xdmp:log("skipping server delete (may not exist)"), $config)
        }
    return admin:save-configuration-without-restart($config)
};
