xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-idx = 'http://www.marklogic.com/ps/lib/lib-index.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst = "http://www.marklogic.com/ps/lib/lib-install.xqy" at "/lib/lib-install.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
    <database name="DatabaseName">
        <element-word-lexicon 	scalar-type="string" namespace="Namespace" localname="ElementName"/>
        <element-range-index  	scalar-type="string" namespace="Namespace" localname="ElementName"/>
        <attrubute-word-lexicon scalar-type="string" parent-namespace="NameSpace" parent-localname="ElementName" namespace="Namespace" localname="AttributeName"/>
        <attribute-range-index	scalar-type="string" parent-namespace="NameSpace" parent-localname="ElementName" namespace="Namespace" localname="AttributeName"/>
        
        <geospatial-element-pair-index parent-namespace="" parent-localname="" lat-namespace="" lat-localname="" lon-namespace="" lon-localname="" coordinate-system="" range-value-positions=""/>
        <geospatial-element-attribute-pair-index parent-namespace="" parent-localname="" lat-namespace="" lat-localname="" lon-namespace="" lon-localname="" coordinate-system="" range-value-positions=""/>
        <geospatial-element-index namespace="" localname="" coordinate-system="" range-value-positions=""/>
        <geospatial-element-child-index parent-namespace="" parent-localname="" namespace="" localname="" coordinate-system="" range-value-positions=""/>
        
    </database>  
:::)


declare function  inst-idx:install-indices($install-config)
{
    inst-idx:install-or-uninstall-indices($install-config, fn:true())
};

declare function  inst-idx:uninstall-indices($install-config)
{
    inst-idx:install-or-uninstall-indices($install-config, fn:false())
};

declare function  inst-idx:install-or-uninstall-indices($install-config, $add)
{
    for $database in $install-config//conf:database 
       let $indices := (
        $database/conf:element-word-lexicon,
        $database/conf:attribute-word-lexicon,
        $database/conf:element-range-index,
        $database/conf:attribute-range-index,
        $database/conf:geospatial-element-pair-index,
        $database/conf:geospatial-element-attribute-pair-index,
        $database/conf:geospatial-element-child-index,
        $database/conf:geospatial-element-index
        )
       
       (: change to remove index :)
       return if($indices) then inst-idx:add-or-remove-index($install-config, $database, $indices, $add) else ()
};


declare function inst-idx:add-or-remove-index($install-config, $database, $install-indices as element()*, $add)
{
    let $DEFAULT-NAMESPACE              := ""
    let $DEFAULT-COLLATION              := fn:default-collation()
    let $DEFAULT-COORDINATE-SYSTEM      := "wgs84"
    let $DEFAULT-RANGE-VALUE-POSITIONS  := "false"
    

    let $database-name := inst-db:mk-database-name-from-string($install-config, $database/@name)
	let $LOG := xdmp:log(text{ if ($add) then "Creating" else "Removing", " Indices for database: ", $database-name})
	let $database := xdmp:database($database-name)
	
	for $install-index in $install-indices
	
        let $LOG := xdmp:log(text{if ($add) then "Creating" else "Removing", " Index: ", xdmp:describe($install-index)}) 

        let $range-value-positions  := xs:boolean(inst:validate-string("Database/Index", "Attribute: range-value-positions", $install-index/@range-value-positions, $DEFAULT-RANGE-VALUE-POSITIONS))
        
        return 
                typeswitch($install-index)
                case element(conf:element-word-lexicon) return
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ())
                    let $collation              := inst:validate-string("Database/Index", "Attribute: collation",           $install-index/@collation, $DEFAULT-COLLATION)
                    return
                    if ($add) then
                        inst-idx:add-element-word-lexicon($database, $namespace, $localname, $collation)
                    else
                        inst-idx:remove-element-word-lexicon($database, $namespace, $localname, $collation)
     
                case element(conf:element-range-index) return
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ())
                    let $scalar-type            := inst:validate-string("Database/Index", "Attribute: scalar-type",         $install-index/@scalar-type, ())
                    let $collation              := inst:validate-string("Database/Index", "Attribute: collation",           $install-index/@collation, $DEFAULT-COLLATION)
                    return
                    if ($add) then
                        inst-idx:add-range-element-index($database, $scalar-type, $namespace, $localname, $collation, $range-value-positions)
                    else 
                        inst-idx:remove-range-element-index($database, $scalar-type, $namespace, $localname, $collation, $range-value-positions) 
    
                case element(conf:attribute-word-lexicon) return
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ())
                    let $parent-namespace       := inst:validate-string("Database/Index", "Attribute: parent-namespace",    $install-index/@parent-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $parent-localname       := inst:validate-string("Database/Index", "Attribute: parent-localname",    $install-index/@parent-localname, ())
                    let $collation              := inst:validate-string("Database/Index", "Attribute: collation",           $install-index/@collation, $DEFAULT-COLLATION)
                    return
                    if ($add) then
                        inst-idx:add-element-attribute-word-lexicon($database, $parent-namespace, $parent-localname, $namespace, $localname, $collation)
                    else
                        inst-idx:remove-element-attribute-word-lexicon($database, $parent-namespace, $parent-localname, $namespace, $localname, $collation)
                    
     
                case element(conf:attribute-range-index) return
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ()) 
                    let $scalar-type            := inst:validate-string("Database/Index", "Attribute: scalar-type",         $install-index/@scalar-type, ())
                    let $parent-namespace       := inst:validate-string("Database/Index", "Attribute: parent-namespace",    $install-index/@parent-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $parent-localname       := inst:validate-string("Database/Index", "Attribute: parent-localname",    $install-index/@parent-localname, ())
                    let $collation              := inst:validate-string("Database/Index", "Attribute: collation",           $install-index/@collation, $DEFAULT-COLLATION)
                    return
                    if ($add) then
                        inst-idx:add-range-element-attribute-index($database, $scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
                    else
                        inst-idx:remove-range-element-attribute-index($database, $scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
                    
                  
                case element(conf:geospatial-element-pair-index) return
                    let $parent-namespace       := inst:validate-string("Database/Index", "Attribute: parent-namespace",    $install-index/@parent-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $parent-localname       := inst:validate-string("Database/Index", "Attribute: parent-localname",    $install-index/@parent-localname, ())
                    let $lat-namespace          := inst:validate-string("Database/Index", "Attribute: lat-namespace",       $install-index/@lat-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $lat-localname          := inst:validate-string("Database/Index", "Attribute: lat-localname",       $install-index/@lat-localname, ())
                    let $lon-namespace          := inst:validate-string("Database/Index", "Attribute: lon-namespace",       $install-index/@lon-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $lon-localname          := inst:validate-string("Database/Index", "Attribute: lon-localname",       $install-index/@lon-localname, ())
                    let $coordinate-system      := inst:validate-string("Database/Index", "Attribute: coordinate-system",   $install-index/@coordinate-system, $DEFAULT-COORDINATE-SYSTEM)
                    return
                    if ($add) then
                        inst-idx:add-geospatial-element-pair-index($database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
                    else
                        inst-idx:remove-geospatial-element-pair-index($database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
                    
                case element(conf:geospatial-element-attribute-pair-index) return
                    let $parent-namespace       := inst:validate-string("Database/Index", "Attribute: parent-namespace",    $install-index/@parent-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $parent-localname       := inst:validate-string("Database/Index", "Attribute: parent-localname",    $install-index/@parent-localname, ())
                    let $lat-namespace          := inst:validate-string("Database/Index", "Attribute: lat-namespace",       $install-index/@lat-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $lat-localname          := inst:validate-string("Database/Index", "Attribute: lat-localname",       $install-index/@lat-localname, ())
                    let $lon-namespace          := inst:validate-string("Database/Index", "Attribute: lon-namespace",       $install-index/@lon-namespace, $DEFAULT-COORDINATE-SYSTEM)
                    let $lon-localname          := inst:validate-string("Database/Index", "Attribute: lon-localname",       $install-index/@lon-localname, ())
                    let $coordinate-system      := inst:validate-string("Database/Index", "Attribute: coordinate-system",   $install-index/@coordinate-system, $DEFAULT-COORDINATE-SYSTEM)
                    return
                    if ($add) then
                        inst-idx:add-geospatial-element-attribute-pair-index($database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
                    else
                        inst-idx:remove-geospatial-element-attribute-pair-index($database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
                    
                case element(conf:geospatial-element-child-index)return 
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ())
                    let $parent-namespace       := inst:validate-string("Database/Index", "Attribute: parent-namespace",    $install-index/@parent-namespace, $DEFAULT-NAMESPACE)
                    let $parent-localname       := inst:validate-string("Database/Index", "Attribute: parent-localname",    $install-index/@parent-localname, ())
                    let $lat-namespace          := inst:validate-string("Database/Index", "Attribute: lat-namespace",       $install-index/@lat-namespace, $DEFAULT-NAMESPACE)
                    let $lat-localname          := inst:validate-string("Database/Index", "Attribute: lat-localname",       $install-index/@lat-localname, ())
                    let $lon-namespace          := inst:validate-string("Database/Index", "Attribute: lon-namespace",       $install-index/@lon-namespace, $DEFAULT-NAMESPACE)
                    let $lon-localname          := inst:validate-string("Database/Index", "Attribute: lon-localname",       $install-index/@lon-localname, ())
                    let $coordinate-system      := inst:validate-string("Database/Index", "Attribute: coordinate-system",   $install-index/@coordinate-system, $DEFAULT-COORDINATE-SYSTEM)
                    return
                    if ($add) then
                        inst-idx:add-geospatial-element-child-index($database, $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
                    else
                        inst-idx:remove-geospatial-element-child-index($database, $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
                                        
                case element(conf:geospatial-element-index) return
                    let $namespace              := inst:validate-string("Database/Index", "Attribute: namespace",           $install-index/@namespace, $DEFAULT-NAMESPACE)
                    let $localname              := inst:validate-string("Database/Index", "Attribute: localname",           $install-index/@localname, ())
                    let $coordinate-system      := inst:validate-string("Database/Index", "Attribute: coordinate-system",   $install-index/@coordinate-system, $DEFAULT-COORDINATE-SYSTEM)
                    return
                    if ($add) then
                        inst-idx:add-geospatial-element-index($database, $namespace, $localname, $coordinate-system, $range-value-positions)
                    else
                        inst-idx:remove-geospatial-element-index($database, $namespace, $localname, $coordinate-system, $range-value-positions)
                                       
                default
                    return xdmp:log(text{"### Invalid index type configuration: ", $install-index})
};


declare function inst-idx:add-geospatial-element-pair-index(
$database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-geospatial-element-pair-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-pair-index(
    $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-add-geospatial-element-pair-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping add-geospatial-element-pair-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:remove-geospatial-element-pair-index(
$database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-geospatial-element-pair-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-pair-index(
    $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-delete-geospatial-element-pair-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping remove-geospatial-element-pair-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:add-geospatial-element-attribute-pair-index(
$database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-geospatial-element-attribute-pair-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-attribute-pair-index(
    $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-add-geospatial-element-attribute-pair-index($config, $database, $geospec)}
        catch ($e) {(xdmp:log("skipping add-geospatial-element-attribute-pair-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:remove-geospatial-element-attribute-pair-index(
$database, $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-geospatial-element-attribute-pair-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-attribute-pair-index(
    $parent-namespace, $parent-localname, $lat-namespace, $lat-localname, $lon-namespace, $lon-localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-delete-geospatial-element-attribute-pair-index($config, $database, $geospec)}
        catch ($e) {(xdmp:log("skipping remove-geospatial-element-attribute-pair-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:add-geospatial-element-child-index(
$database, $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-geospatial-element-child-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-child-index(
    $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-add-geospatial-element-child-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping add-geospatial-element-child-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:remove-geospatial-element-child-index(
$database, $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-geospatial-element-child-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-child-index(
    $parent-namespace, $parent-localname, $namespace, $localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-delete-geospatial-element-child-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping remove-geospatial-element-child-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:add-geospatial-element-index(
$database, $namespace, $localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-geospatial-element-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-index(
    $namespace, $localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-add-geospatial-element-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping add-geospatial-element-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:remove-geospatial-element-index(
$database, $namespace, $localname, $coordinate-system, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-geospatial-element-index()"})
    let $config := admin:get-configuration()
    let $geospec := admin:database-geospatial-element-index(
    $namespace, $localname, $coordinate-system, $range-value-positions)
    let $config := 
        try { admin:database-delete-geospatial-element-index($config, $database, $geospec) }
        catch ($e) {(xdmp:log("skipping remove-geospatial-element-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $geospec
};

declare function inst-idx:add-element-word-lexicon($database, $namespace, $localname, $collation)
{
    let $LOG := xdmp:log(text{"Inside: add-element-word-lexicon()", $namespace, $localname, $collation})
    let $config := admin:get-configuration()
    let $lexicon  := admin:database-element-word-lexicon(xs:string($namespace), $localname, $collation)
    let $config := 
        try { admin:database-add-element-word-lexicon($config, $database, $lexicon) }
        catch ($e) {(xdmp:log("skipping add-element-word-lexicon(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $lexicon
};

declare function inst-idx:remove-element-word-lexicon($database, $namespace, $localname, $collation)
{
    let $LOG := xdmp:log(text{"Inside: remove-element-word-lexicon()", $namespace, $localname, $collation})
    let $config := admin:get-configuration()
    let $lexicon  := admin:database-element-word-lexicon(xs:string($namespace), $localname, $collation)
    let $config := 
        try { admin:database-delete-element-word-lexicon($config, $database, $lexicon) }
        catch ($e) {(xdmp:log("skipping remove-element-word-lexicon(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $lexicon
};

declare function inst-idx:add-range-element-index($database, $scalar-type, $namespace, $localname, $collation, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-range-element-index()"})
    let $config := admin:get-configuration()
    let $index := admin:database-range-element-index($scalar-type, $namespace, $localname, $collation, $range-value-positions)
    let $config :=
        try { admin:database-add-range-element-index($config, $database, $index) }
        catch ($e) {(xdmp:log("skipping add-range-element-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $index
};

declare function inst-idx:remove-range-element-index($database, $scalar-type, $namespace, $localname, $collation, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-range-element-index()"})
    let $config := admin:get-configuration()
    let $index := admin:database-range-element-index($scalar-type, $namespace, $localname, $collation, $range-value-positions)
    let $config :=
        try { admin:database-delete-range-element-index($config, $database, $index) }
        catch ($e) {(xdmp:log("skipping remove-range-element-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $index
};

declare function inst-idx:add-element-attribute-word-lexicon($database, $parent-namespace, $parent-localname, $namespace, $localname, $collation)
{
    let $LOG := xdmp:log(text{"Inside: add-element-attribute-word-lexicon()"})
    let $config := admin:get-configuration()
    let $lexicon := admin:database-element-attribute-word-lexicon($parent-namespace, $parent-localname, $namespace, $localname, $collation)
    let $config := 
        try { admin:database-add-element-attribute-word-lexicon($config, $database, $lexicon) }
        catch ($e) {(xdmp:log("skipping add-element-attribute-word-lexicon(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $lexicon
};

declare function inst-idx:remove-element-attribute-word-lexicon($database, $parent-namespace, $parent-localname, $namespace, $localname, $collation)
{
    let $LOG := xdmp:log(text{"Inside: remove-element-attribute-word-lexicon()"})
    let $config := admin:get-configuration()
    let $lexicon := admin:database-element-attribute-word-lexicon($parent-namespace, $parent-localname, $namespace, $localname, $collation)
    let $config := 
        try { admin:database-delete-element-attribute-word-lexicon($config, $database, $lexicon) }
        catch ($e) {(xdmp:log("skipping remove-element-attribute-word-lexicon(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $lexicon
};

declare function inst-idx:add-range-element-attribute-index($database, $scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: add-range-element-attribute-index()"})
    let $config := admin:get-configuration()
    let $index := admin:database-range-element-attribute-index($scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
    let $config :=
        try { admin:database-add-range-element-attribute-index($config, $database, $index) }
        catch ($e) {(xdmp:log("skipping add-range-element-attribute-index(), already present"), $config)}
    let $config := admin:save-configuration($config)
    return $index
};

declare function inst-idx:remove-range-element-attribute-index($database, $scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
{
    let $LOG := xdmp:log(text{"Inside: remove-range-element-attribute-index()"})
    let $config := admin:get-configuration()
    let $index := admin:database-range-element-attribute-index($scalar-type, $parent-namespace, $parent-localname, $namespace, $localname, $collation, $range-value-positions)
    let $config :=
        try { admin:database-delete-range-element-attribute-index($config, $database, $index) }
        catch ($e) {(xdmp:log("skipping remove-range-element-attribute-index(), not present"), $config)}
    let $config := admin:save-configuration($config)
    return $index
};
