xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-fld = 'http://www.marklogic.com/ps/lib/lib-field.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst = "http://www.marklogic.com/ps/lib/lib-install.xqy" at "/lib/lib-install.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
    <database name="DatabaseName">
        <field name="" include-root="">
            <include namespace="" localname="" weight="" attribute-namespace="" attribute-localname="" attribute-value=""/>
            <include namespace="" localname="" weight="" attribute-namespace="" attribute-localname="" attribute-value=""/>
            <exclude namespace="" localname=""/>
            <exclude namespace="" localname=""/>
            <lexicon collation=""/>
    </database>  
:::)

declare function  inst-fld:install-fields($install-config)
{
    for $database in $install-config//conf:database 
       let $fields := $database/conf:field       
       return if($fields) then inst-fld:add-field($install-config, $database, $fields) else ()
};

declare function  inst-fld:uninstall-fields($install-config)
{
    for $database in $install-config//conf:database 
       let $fields := $database/conf:field       
       return if($fields) then inst-fld:remove-field($install-config, $database, $fields) else ()
};

declare function inst-fld:add-field($install-config as node(), $database as element(conf:database), $fields as element(conf:field)*) {
    let $LOG := xdmp:log("Inside: add-field()")
    let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
    let $database := xdmp:database($database-name)
    
    
    for $field in $fields
        let $config := admin:get-configuration()
        let $newfield := admin:database-field($field/@name, $field/@include-root)
        let $config := admin:database-add-field($config, $database, $newfield)
        let $included-elements := 
            for $include in $field/conf:include
                return admin:database-included-element($include/@element-namespace, $include/@localname, $include/@weight, 
                        $include/@attribute-namespace, $include/@attribute-localname, $include/@attribute-value) 
        let $excluded-elements :=
            for $exclude in $field/conf:exclude
                return admin:database-excluded-element($exclude/@element-namespace, $exclude/@localname) 
                
        let $config := 
            if ($included-elements) then 
                admin:database-add-field-included-element($config, $database, $field/@name, $included-elements)
            else 
                $config
                
        let $config := 
            if ($excluded-elements) then
                admin:database-add-field-excluded-element($config, $database, $field/@name, $excluded-elements)
            else
                $config
                
        let $config := 
            if ($field/conf:lexicon) then
                admin:database-add-field-word-lexicon($config, $database, $field/@name, 
                    admin:database-word-lexicon($field/conf:lexicon/@collation))
            else
                $config 
        let $config := admin:save-configuration($config)
        return ()        
};

declare function inst-fld:remove-field($install-config as node(), $database as element(conf:database), $fields as element(conf:field)*)
{
    let $LOG := xdmp:log("Inside: remove-field()")
    let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
    let $database := xdmp:database($database-name)
    
    for $field in $fields
    let $config := admin:get-configuration()
    let $config :=
        try {admin:database-delete-field($config, $database, $field/@name)}
        catch($e) {$config}
        
    let $config := admin:save-configuration($config)
    return ()   
};