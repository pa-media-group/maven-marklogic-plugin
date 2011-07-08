xquery version "1.0-ml";

module namespace inst-load = 'http://www.marklogic.com/ps/lib/lib-load.xqy';
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";
import module namespace cvt = "http://marklogic.com/cpf/convert" at "/MarkLogic/conversion/convert.xqy";
declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUTS :::
   <load-content>
        <copy-files>
            <from-filesystem root="{$FILESYSTEM-ROOT}/Content"/> 
            <from-archive root="/Content/data"/>
            <to-database  database="Content" root="/"/>
        </copy-files>
        <copy-files>
            <from-filesystem root="{$FILESYSTEM-ROOT}/Modules"/>
            <from-archive root="/Modules/"/>
            <to-database  database="Modules" root="/"/>
        </copy-files>
     </load-content>
:::)

declare function inst-load:remove-content($install-config) {
    "undefined"
};

declare function inst-load:load-content($install-config) {
    for $copyFiles in $install-config/conf:load-content/conf:copy-files
        let $from-filesystem := $copyFiles/conf:from-filesystem/@root
        let $to-database :=  inst-db:mk-database-name-from-string($install-config, $copyFiles/conf:to-database/@database)
        let $rules := $copyFiles/conf:from-filesystem/conf:rules
        return (
            inst-load:install-content($from-filesystem,$to-database, $rules)
        )
};

declare function inst-load:install-content($from-filesystem, $to-database, $rules) {
    inst-load:process-dir($from-filesystem,$from-filesystem, $to-database, $rules)
};

declare function inst-load:process-dir($base, $directory, $to-database, $rules) {
    for $entry in xdmp:filesystem-directory($directory)//dir:entry
        let $filename := $entry/dir:filename/text()
        let $fullpath := $entry/dir:pathname/text()
        let $rule := $rules/conf:rule[@root = cvt:basepath($fullpath)]
        let $format := $rule/@format
        let $format := if($format) then $format else "VOID"
        return
        (
            if (".svn" eq $filename or ".DS_Store" eq $filename) then ()
            else if ($entry/dir:type/text() = "directory") then
                inst-load:process-dir($base, fn:concat($directory, "/", $filename), $to-database, $rules)
            else (
                if (fn:not($rule) or fn:matches($filename, $rule/@match)) then
                    let $uri := fn:replace($fullpath, $base, "")
                    let $trash := xdmp:log(fn:concat("Importing Content ", $filename, " with URI ", $uri))
                    let $qry := 
                        "xquery version '1.0-ml'; 
                        declare variable $fn as xs:string external;
                        declare variable $uri as xs:string external;
                        declare variable $format as xs:string external;
                        xdmp:document-load( $fn, 
                            <options xmlns='xdmp:document-load'>
                                <uri>{$uri}</uri>
                                {if($format eq 'VOID') then () else <format>{$format}</format>}
                            </options> )"
                            
                    let $qry := if("thesaurus" eq $format) then
                        "xquery version '1.0-ml'; 
                        import module namespace thsr='http://marklogic.com/xdmp/thesaurus' at '/MarkLogic/thesaurus.xqy';
                        declare variable $fn as xs:string external;
                        declare variable $uri as xs:string external;
                        declare variable $format as xs:string external;
                        
                        thsr:load($fn,$uri)"
                        else $qry
                    
                    return
                        xdmp:eval( $qry, (xs:QName("fn"), $fullpath, xs:QName("uri"), $uri, xs:QName("format"), $format), 
                        <options xmlns="xdmp:eval">
                            <database>{xdmp:database($to-database)}</database>
                        </options>
                    )
                else ()
            )
        )
};