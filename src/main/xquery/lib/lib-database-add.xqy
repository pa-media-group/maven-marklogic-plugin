xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-db-add = 'http://www.marklogic.com/ps/lib/lib-database-add.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace inst-conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
<element>
    <database name="{DatabaseName}">

    <add name="fragment-parent" namespace="" localname=""/>
    <add name="fragment-root"   namespace="" localname=""/>
    <add name="phrase-around"   namespace="" localname=""/>
    <add name="phrase-through"  namespace="" localname=""/>

    <database/>
	...
	...

</element>
:)

declare function inst-db-add:do-adds($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
            for $add in $database/inst-conf:add
                return inst-db-add:do-database-add($database-name, $add)
};

declare function  inst-db-add:do-database-add($database-name, $add)
{
    let $database-id := xdmp:database($database-name)
    let $name  := $add/@name
    let $namespace := $add/@namespace
    let $localname := $add/@localname
    
    let $LOG := xdmp:log(text{"Adding Object:", $name, "to Database:", $database-name, "with:", $namespace, "/", $localname})
    
    let $config := admin:get-configuration()
    let $config := 
        try {
             if ("fragment-parent"  eq $name) then admin:database-add-fragment-parent($config, $database-id, admin:database-fragment-parent($namespace, $localname))            
        else if ("fragment-root"    eq $name) then admin:database-add-fragment-root($config, $database-id, admin:database-fragment-root($namespace, $localname))
        else if ("phrase-around"    eq $name) then admin:database-add-phrase-around($config, $database-id, admin:database-phrase-around($namespace, $localname))
        else if ("phrase-through"   eq $name) then admin:database-add-phrase-through($config, $database-id, admin:database-phrase-through($namespace, $localname))
        else    $config                                  
        }
        catch ($e) {(xdmp:log(text{"### Skipping adding database object:",$name,"(may be an invalid value)->",$namespace, "/", $localname}), $config)}
        
    return admin:save-configuration-without-restart($config)
};
