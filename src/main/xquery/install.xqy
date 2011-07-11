xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

declare namespace inst-conf = "http://www.marklogic.com/ps/install/config.xqy";

import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";
import module namespace inst-app = 'http://www.marklogic.com/ps/lib/lib-app-server.xqy' at "/lib/lib-app-server.xqy";
import module namespace inst-idx = 'http://www.marklogic.com/ps/lib/lib-index.xqy' at "/lib/lib-index.xqy";
import module namespace inst-cpf = 'http://www.marklogic.com/ps/lib/lib-cpf.xqy' at "/lib/lib-cpf.xqy";
import module namespace inst-load = 'http://www.marklogic.com/ps/lib/lib-load.xqy' at "/lib/lib-load.xqy";
import module namespace inst-trgr = 'http://www.marklogic.com/ps/lib/lib-trigger.xqy' at "/lib/lib-trigger.xqy";
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";

declare variable $action as xs:string external;
declare variable $environ as xs:string external;
declare variable $delete-data as xs:boolean external;
declare variable $configuration-string as xs:string external;
declare variable $CONFIGURATION as node() := xdmp:unquote($configuration-string);

let $install-config := $CONFIGURATION//inst-conf:install/*[fn:node-name(.) = xs:QName(fn:concat('inst-conf:', $environ))]

let $LOG := xdmp:log($install-config)

let $ACTION :=
if ("install-all" eq $action) then
(
    $action,
    inst-db:install-databases($install-config),
    inst-trgr:install-triggers($install-config),
    inst-app:install-servers($install-config),
    inst-cpf:install-cpf($install-config),
    inst-load:load-content($install-config),
    xdmp:restart((),"Installation")
)
else if ("uninstall-all" eq $action) then
(
    $action,
    inst-app:uninstall-servers($install-config),
    inst-trgr:uninstall-triggers($install-config),
    inst-db:uninstall-databases($install-config, $delete-data)
)
else if ("install-databases" eq $action) then
(
   $action,
    inst-db:install-databases($install-config)
)
else if ("uninstall-databases" eq $action) then
(
    $action,
    inst-db:uninstall-databases($install-config, $delete-data)
)
else if ("install-triggers" eq $action) then
(
    $action,
    inst-trgr:install-triggers($install-config)
)
else if ("uninstall-triggers" eq $action) then
(
    $action,
    inst-trgr:uninstall-triggers($install-config)
)
else if ("install-servers" eq $action) then
(   
    $action,
    inst-app:install-servers($install-config)
)
else if ("uninstall-servers" eq $action) then
(
   $action,
    inst-app:uninstall-servers($install-config)
)
else if ("uninstall-content" eq $action) then
(
    $action,
    inst-load:remove-content($install-config)
)
else if ("install-content" eq $action) then
(
   $action,
    inst-load:load-content($install-config)
)
else if ("load-pipelines" eq $action) then
(
    $action,
    inst-cpf:load-pipelines($install-config)
)
else if ("create-domains" eq $action) then
(
    $action,
    inst-cpf:create-domains($install-config)
)
else if ("create-configurations" eq $action) then
(
    $action,
    inst-cpf:create-configurations($install-config)
)
else if ("install-cpf" eq $action) then
(
    $action,
    inst-cpf:install-cpf($install-config)
)
else if ("restart" eq $action) then
(
    $action,
    xdmp:restart((),"Installation")
)
else
    text{"Invalid Action: ", $action}

    
return $ACTION    
    