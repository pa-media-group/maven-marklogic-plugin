xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-trgr = 'http://www.marklogic.com/ps/lib/lib-trigger.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst = "http://www.marklogic.com/ps/lib/lib-install.xqy" at "/lib/lib-install.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";
import module namespace trgr="http://marklogic.com/xdmp/triggers" at "/MarkLogic/triggers.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
    <database name="DatabaseName">
      <trigger name="trigger-name" descr="">
        <event type="data|online">
          <!-- data only -->
          <scope type="directory|collection|document" depth="1">URI</scope>
          <content type="document|property|all-properties" 
                   kind="create|update|delete"          <!-- document type only -->
                   prop-ns="" prop-name="" />           <!-- property type only -->
          <when>pre-commit|post-commit</when>
        </event>
        <module database="modules" path="/">add-serch-metadata.xqy</module>
        <enabled />
        <disable-recursive-triggering />
      </trigger>
    </database>  
:::)

declare function  inst-trgr:install-triggers($install-config)
{
    for $database in $install-config//conf:database 
       let $triggers := $database/conf:trigger       
       return if($triggers) then inst-trgr:add-trigger($install-config, $database, $triggers) else ()
};

declare function  inst-trgr:uninstall-triggers($install-config)
{
  let $q := (::)'(::)
      xquery version "1.0-ml";
      import module namespace trgr="http://marklogic.com/xdmp/triggers" at "/MarkLogic/triggers.xqy";
      declare variable $trigger-name external;
      try { trgr:remove-trigger($trigger-name) } catch($e) { xdmp:log(text{$e//*:message, $trigger-name}) }
  (::)'(::)

  return for $database in $install-config//conf:database
  let $triggers := $database/conf:trigger
  let $content-database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
  let $triggers-database := admin:database-get-triggers-database(admin:get-configuration(), xdmp:database($content-database-name))
  return if($triggers) then 
    for $trigger in $triggers
    return xdmp:eval( $q
                    , (xs:QName('trigger-name'), $trigger/@name/fn:string())
                    , <options xmlns="xdmp:eval">
                        <database>{$triggers-database}</database>
                      </options> )
  else ()
};

declare function inst-trgr:add-trigger($install-config as node(), $database as element(conf:database), $triggers as element(conf:trigger)*) {
    let $LOG := xdmp:log("Inside: add-trigger()")
        
    for $trigger in $triggers
        (:: Get the triggering event ::)    
        let $event := if ($trigger/conf:event/@type = "online") 
        then trgr:trigger-database-online-event($trigger/conf:event/fn:string()) 
        else
          (:: Get the scope of the trigger ::)
          let $scope := $trigger/conf:event/conf:scope
          let $uri := $scope/fn:string()
          let $type := $scope/@type                  
          let $scope := 
          (::) if ($type = "directory")  then trgr:directory-scope($uri, $scope/@depth)
          else if ($type = "collection") then trgr:collection-scope($uri)
          else if ($type = "document")   then trgr:document-scope($uri)
          else ()
          
          (:: Get what type of content update should invoke the trigger ::)
          let $content := $trigger/conf:event/conf:content
          let $type := $content/@type
          let $content :=
          (::) if ($type = "document") then trgr:document-content($content/@kind)            
          else if ($type = "property") then trgr:property-content(fn:QName($content/@prop-ns, $content/@prop-name))            
          else if ($type = "any-property") then trgr:any-property-content()
          else ()
          
          (:: Get when the trigger should be invoked ::)
          let $when := if($trigger/conf:event/conf:when/fn:string() = "pre-commit")
          then trgr:pre-commit() else trgr:post-commit()
                    
          return trgr:trigger-data-event($scope, $content, $when)
        
        (:: Get the module node ::)
        let $module := $trigger/conf:module

        let $_ := xdmp:log(text{"Adding trigger <", $trigger/@name, "> on ", $database/@name})
        
        let $database-name := inst-db:mk-database-name-from-string($install-config, $module/@database)
        let $content-database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)                              
        let $triggers-database := admin:database-get-triggers-database(admin:get-configuration(), xdmp:database($content-database-name))
                
        let $query := (::)'(::)
        xquery version "1.0-ml";
        
        import module namespace trgr = "http://marklogic.com/xdmp/triggers" at "/MarkLogic/triggers.xqy";
        
        declare variable $name as xs:string external;
        declare variable $desc as xs:string external;
        declare variable $event as element() external;
        declare variable $module as element(trgr:module) external;
        declare variable $enabled as xs:boolean external;
        declare variable $permissions as element() external; 
        declare variable $recursive as xs:boolean external;
        
        trgr:create-trigger( $name, $desc, $event, $module, $enabled
                           , xdmp:default-permissions(), $recursive)
        (::)'(::)
        
        let $name := $trigger/@name
        let $desc := ($trigger/@descr, "")[1]
        let $modules := trgr:trigger-module( xdmp:database($database-name)
                                           , $module/@path
                                           , $module/fn:string() )
        let $enabled := fn:exists($trigger/conf:enabled)
        let $recursive := fn:empty($trigger/conf:disable-recursive-triggering)
                
        (:: Create the trigger ::)
        let $_ := xdmp:eval($query,
          ( xs:QName("name"), $name
          , xs:QName("desc"), $desc
          , xs:QName("event"), $event
          , xs:QName("module"), $modules
          , xs:QName("enabled"), $enabled
          , xs:QName("recursive"), $recursive
          )        
          ,
          <options xmlns="xdmp:eval">
            <database>{$triggers-database}</database>
          </options>
        )
  
        return ()
};