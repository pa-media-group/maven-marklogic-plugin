xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-cpf = 'http://www.marklogic.com/ps/lib/lib-cpf.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst = "http://www.marklogic.com/ps/lib/lib-install.xqy" at "/lib/lib-install.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";
import module namespace dom = "http://marklogic.com/cpf/domains" at "/MarkLogic/cpf/domains.xqy";
import module namespace p = "http://marklogic.com/cpf/pipelines" at "/MarkLogic/cpf/pipelines.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
    <database name="DatabaseName">
        <content-processing user="" role="" modules="" root="" default-domain="">
            <load-pipelines>
                <from-filesystem file="{$FILESYSTEM-ROOT}/Modules/CPF/pipelines/pipeline.xml"/>
            </load-pipelines>
            <domain name="" document-scope="(directory|collection|document)" uri="/" depth="infinity|1" modules="" root="/">
                <pipeline name="Conversion Processing (Basic)"/>
                <pipeline name="Status Change Handling"/>
            </domain>
        </content-processing>
    </database>
:::)

declare function  inst-cpf:install-cpf($install-config)
{(
    inst-cpf:load-pipelines($install-config),
    inst-cpf:create-domains($install-config),
    inst-cpf:create-configurations($install-config)
)};

declare function inst-cpf:create-configurations($install-config)
{
    for $database in $install-config//conf:database
        let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
        let $triggers-database := admin:database-get-triggers-database(admin:get-configuration(), xdmp:database($database-name))
    
        let $cp := $database/conf:content-processing
        return
        if($cp) then
            let $LOG     := xdmp:log(text{"Installing CPF into Database: ",$database-name})
            let $user    := $cp/@user
            let $role    := $cp/@role
            let $modules := $cp/@modules
            let $modules := if($modules eq "0") then "file-system" else xdmp:database(inst-db:mk-database-name-from-string($install-config, $modules))
            let $root    := $cp/@root
            let $default := $cp/@default-domain
            let $default := if($default) then $default else ($cp/conf:domain/@name)[1]
            
            let $xqy := (::)'(::)
                xquery version "1.0-ml";
                import module namespace dom = "http://marklogic.com/cpf/domains" at "/MarkLogic/cpf/domains.xqy";
                declare variable $user as xs:string external;
                declare variable $role as xs:string external;
                declare variable $modules as xs:unsignedLong external;
                declare variable $root as xs:string external;
                declare variable $default as xs:string external;
                
                let $context := dom:evaluation-context( $modules, $root )
                let $default-id := fn:data(dom:get($default)/dom:domain-id)
                let $perms := (xdmp:permission($role, "read"), xdmp:permission($role, "execute"))
                return dom:configuration-create( $user, $context, $default-id, $perms )
                (::)'(::)
                                
            return xdmp:eval($xqy,
                (xs:QName("user"),    $user,
                xs:QName("role"),    $role,
                xs:QName("modules"), $modules,
                xs:QName("root"),    $root,
                xs:QName("default"), $default)
                ,  
                <options xmlns="xdmp:eval">
                    <database>{$triggers-database}</database>
                </options>
                )             
        else ()
};

declare function  inst-cpf:create-domains($install-config)
{
    for $database in $install-config//conf:database
        let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
        let $triggers-database := admin:database-get-triggers-database(admin:get-configuration(), xdmp:database($database-name))
        return
            for $domain in $database/conf:content-processing/conf:domain
                let $domain-name    := inst:validate-string("CPF/Domain", "Attribute: domain-name",     $domain/@name, ())            
                let $description    := inst:validate-string("CPF/Domain", "Attribute: description",     $domain/@description, "No Idea!")
                let $document-scope := inst:validate-string("CPF/Domain", "Attribute: document-scope",  $domain/@document-scope, "directory")
                let $uri            := inst:validate-string("CPF/Domain", "Attribute: uri",             $domain/@uri, "/")
                let $depth          := inst:validate-string("CPF/Domain", "Attribute: depth",           $domain/@depth, "infinity")
                let $modules        := inst:validate-string("CPF/Domain", "Attribute: modules",         $domain/@modules, "0")
                let $root           := inst:validate-string("CPF/Domain", "Attribute: root",            $domain/@root, "/")
                let $pipeline-names := inst:validate-string("CPF/Domain", "Element: pipeline",          $domain/conf:pipeline/@name, ())
                
                let $LOG := xdmp:log(text{"Adding domain: ",$domain-name, " into Database: ",$database-name})
                  
                let $xqy := (::)'(::)
                        xquery version "1.0-ml";
                        import module namespace dom = "http://marklogic.com/cpf/domains" at "/MarkLogic/cpf/domains.xqy";
                        import module namespace p = "http://marklogic.com/cpf/pipelines" at "/MarkLogic/cpf/pipelines.xqy";
                        
                        declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";
          
          				declare variable $domain-name external;
          				declare variable $description external;
                        declare variable $scope external;
                        declare variable $context external;
                        declare variable $domain external;
                        
                        let $pipeline-names := $domain/conf:pipeline/@name
                        
                        let $pipelines := for $pipeline-name in $pipeline-names
                                return p:get($pipeline-name)/p:pipeline-id
                                       
                        return dom:create( $domain-name, $description, $scope, $context, $pipelines, () )
                        (::)'(::)
                                     
                let $scope := dom:domain-scope( $document-scope, $uri, $depth)
                
                let $context := dom:evaluation-context(
                        xdmp:database(inst-db:mk-database-name-from-string($install-config, $modules)), $root )
          
                let $vars := (
                	xs:QName("domain-name"), $domain-name,
                	xs:QName("description"), $description,
                    xs:QName("scope"), $scope,
                    xs:QName("context"), $context,
                    xs:QName("domain"), $domain
                    )
                    
                let $opts := <options xmlns="xdmp:eval"><database>{$triggers-database}</database></options>
                    
                return xdmp:eval($xqy, $vars, $opts)                                    
};

declare function  inst-cpf:load-pipelines($install-config)
{
    for $database in $install-config//conf:database
        let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
        let $triggers-database := admin:database-get-triggers-database(admin:get-configuration(), xdmp:database($database-name)) 
        return for $file in $database/conf:content-processing/conf:load-pipelines/conf:from-filesystem/@file
           let $LOG := xdmp:log(text{"Installing pipeline: ",$file, " into Database: ",$database-name})
           let $xqy :=(::)'(::)
                    xquery version "1.0-ml";
                    import module namespace p = "http://marklogic.com/cpf/pipelines" at "/MarkLogic/cpf/pipelines.xqy";
                    declare variable $file as xs:string external; 
                    p:insert( xdmp:get($file)/* )
                    (::)'(::)
                            
            return xdmp:eval($xqy, (xs:QName("file"), $file),  
                        <options xmlns="xdmp:eval">
                           <database>{$triggers-database}</database>
                         </options>)
};
