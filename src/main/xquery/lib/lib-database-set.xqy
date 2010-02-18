xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst-db-set = 'http://www.marklogic.com/ps/lib/lib-database-set.xqy';
import module namespace admin = "http://marklogic.com/xdmp/admin" at "/MarkLogic/admin.xqy";
import module namespace inst-db = "http://www.marklogic.com/ps/lib/lib-database.xqy" at "/lib/lib-database.xqy";

declare namespace inst-conf = "http://www.marklogic.com/ps/install/config.xqy";

(::: INPUT :::
<element>
    <database name="{DatabaseName}">

    <set name="attribute-value-positions"			    value=""/>
	<set name="collection-lexicon"					    value=""/>
	<set name="directory-creation"					    value=""/>
	<set name="element-value-positions"                 value=""/>
	<set name="element-word-positions"		            value=""/>
	<set name="enabled"						            value=""/>
	<set name="expunge-locks"					        value=""/>
	<set name="fast-case-sensitive-searches"			value=""/>
	<set name="fast-diacritic-sensitive-searches"		value=""/>
	<set name="fast-element-character-searches"			value=""/>
	<set name="fast-element-phrase-searches"			value=""/>
	<set name="fast-element-trailing-wildcard-searches"	value=""/>
	<set name="fast-element-word-searches"				value=""/>
	<set name="fast-phrase-searches"				    value=""/>
	<set name="fast-reverse-searches"				    value=""/>
	<set name="format-compatibility"				    value=""/>
	<set name="in-memory-limit"					        value=""/>
	<set name="in-memory-list-size"					    value=""/>
	<set name="in-memory-range-index-size"				value=""/>
	<set name="in-memory-reverse-index-size"			value=""/>
	<set name="in-memory-tree-size"					    value=""/>
	<set name="index-detection"					        value=""/>
	<set name="inherit-collections"					    value=""/>
	<set name="inherit-permissions"					    value=""/>
	<set name="inherit-quality"					        value=""/>
	<set name="journal-size"					        value=""/>
	<set name="language"						        value=""/>
	<set name="maintain-directory-last-modified"		value=""/>
	<set name="maintain-last-modified"				    value=""/>
	<set name="merge-enable"					        value=""/>
	<set name="merge-max-size"					        value=""/>
	<set name="merge-min-ratio"					        value=""/>
	<set name="merge-min-size"					        value=""/>
	<set name="merge-timestamp"					        value=""/>
	<set name="name"						            value=""/>
	<set name="one-character-searches"				    value=""/>
	<set name="positions-list-max-size"				    value=""/>
	<set name="preallocate-journals"				    value=""/>
	<set name="preload-mapped-data"					    value=""/>
	<set name="reindexer-enable"					    value=""/>
	<set name="reindexer-throttle"					    value=""/>
	<set name="reindexer-timestamp"					    value=""/>
	<set name="stemmed-searches"					    value=""/>
	<set name="tf-normalization"					    value=""/>
	<set name="three-character-searches"				value=""/>
	<set name="three-character-word-positions"			value=""/>
	<set name="trailing-wildcard-searches"				value=""/>
	<set name="trailing-wildcard-word-positions"		value=""/>
    <set name="two-character-searches"                  value=""/>
    <set name="uri-lexicon"                             value=""/>
    <set name="word-positions"                               value="(true|false)"/>
    <set name="word-query-fast-case-sensitive-searches"      value="(true|false)"/>
    <set name="word-query-fast-diacritic-sensitive-searches" value="(true|false)"/>
    <set name="word-query-fast-phrase-searches"              value="(true|false)"/>
    <set name="word-query-include-document-root"             value="(true|false)"/>
    <set name="word-query-one-character-searches"            value="(true|false)"/>
    <set name="word-query-stemmed-searches"                  value="(true|false)"/>
    <set name="word-query-three-character-searches"          value="(true|false)"/>
    <set name="word-query-three-character-word-positions"    value="(true|false)"/>
    <set name="word-query-trailing-wildcard-searches"        value="(true|false)"/>
    <set name="word-query-trailing-wildcard-word-positions"  value="(true|false)"/>
    <set name="word-query-two-character-searches"            value="(true|false)"/>
    <set name="word-query-word-searches"                     value="(true|false)"/>
    <set name="word-searches"                                value="(true|false)"/> 

    </database>

    <database/>
	...
	...

</element>
:::)

declare function inst-db-set:do-sets($install-config)
{
    for $database in $install-config/inst-conf:database
        let $database-name := inst-db:mk-database-name($install-config, $database)
        return
            for $set in $database/inst-conf:set
                let $set1 := inst-db-set:do-database-set-1($database-name, $set)
                let $set2 := inst-db-set:do-database-set-2($database-name, $set)
                return ()
};

declare function  inst-db-set:do-database-set-1($database-name, $set)
{
    let $database-id := xdmp:database($database-name)
    let $name  := $set/@name
    let $value := $set/@value
    
    let $LOG := xdmp:log(text{"Setting Property:", $name, "in Database:", $database-name, "to:", $value})
    
    let $config := admin:get-configuration()
    let $config := 
        try {
             if ("attribute-value-positions"                    eq $name) then admin:database-set-attribute-value-positions($config, $database-id, fn:boolean($value))
        else if ("collection-lexicon"                           eq $name) then admin:database-set-collection-lexicon($config, $database-id, fn:boolean($value))
        else if ("directory-creation"                           eq $name) then admin:database-set-directory-creation($config, $database-id, xs:string($value))    
        else if ("element-value-positions"                      eq $name) then admin:database-set-element-value-positions($config, $database-id, fn:boolean($value))
        else if ("element-word-positions"                       eq $name) then admin:database-set-element-word-positions($config, $database-id, fn:boolean($value))
        else if ("enabled"                                      eq $name) then admin:database-set-enabled($config, $database-id, fn:boolean($value))
        else if ("expunge-locks"                                eq $name) then admin:database-set-expunge-locks($config, $database-id, xs:string($value))
        else if ("fast-case-sensitive-searches"                 eq $name) then admin:database-set-fast-case-sensitive-searches($config, $database-id, fn:boolean($value))
        else if ("fast-diacritic-sensitive-searches"            eq $name) then admin:database-set-fast-diacritic-sensitive-searches($config, $database-id, fn:boolean($value))
        else if ("fast-element-character-searches"              eq $name) then admin:database-set-fast-element-character-searches($config, $database-id, fn:boolean($value))
        else if ("fast-element-phrase-searches"                 eq $name) then admin:database-set-fast-element-phrase-searches($config, $database-id, fn:boolean($value))
        else if ("fast-element-trailing-wildcard-searches"      eq $name) then admin:database-set-fast-element-trailing-wildcard-searches($config, $database-id, fn:boolean($value))
        else if ("fast-element-word-searches"                   eq $name) then admin:database-set-fast-element-word-searches($config, $database-id, fn:boolean($value))
        else if ("fast-phrase-searches"                         eq $name) then admin:database-set-fast-phrase-searches($config, $database-id, fn:boolean($value))
        else if ("fast-reverse-searches"                        eq $name) then admin:database-set-fast-reverse-searches($config, $database-id, fn:boolean($value))
        else if ("format-compatibility"                         eq $name) then admin:database-set-format-compatibility($config, $database-id, xs:string($value))
        else if ("in-memory-limit"                              eq $name) then admin:database-set-in-memory-limit($config, $database-id, xs:unsignedInt($value))
        else if ("in-memory-list-size"                          eq $name) then admin:database-set-in-memory-list-size($config, $database-id, xs:unsignedInt($value))
        else if ("in-memory-range-index-size"                   eq $name) then admin:database-set-in-memory-range-index-size($config, $database-id, xs:unsignedInt($value))
        else if ("in-memory-reverse-index-size"                 eq $name) then admin:database-set-in-memory-reverse-index-size($config, $database-id, xs:unsignedInt($value))
        else if ("in-memory-tree-size"                          eq $name) then admin:database-set-in-memory-tree-size($config, $database-id, xs:unsignedInt($value))
        else if ("index-detection"                              eq $name) then admin:database-set-index-detection($config, $database-id, xs:string($value))
        else if ("inherit-collections"                          eq $name) then admin:database-set-inherit-collections($config, $database-id, fn:boolean($value))
        else if ("inherit-permissions"                          eq $name) then admin:database-set-inherit-permissions($config, $database-id, fn:boolean($value))
        else if ("inherit-quality"                              eq $name) then admin:database-set-inherit-quality($config, $database-id, fn:boolean($value))
        else if ("journal-size"                                 eq $name) then admin:database-set-journal-size($config, $database-id, xs:unsignedInt($value))
        else    $config                                  
        }
        catch ($e) {(xdmp:log(text{"### Skipping setting database property:",$name,"(may be an invalid value)->",$value}), $config)}
        
    return admin:save-configuration-without-restart($config)
};

declare function  inst-db-set:do-database-set-2($database-name, $set)
{
    let $database-id := xdmp:database($database-name)
    let $name  := $set/@name
    let $value := $set/@value
    
    let $LOG := xdmp:log(text{"Setting Property:", $name, "in Database:", $database-name, "to:", $value})
    
    let $config := admin:get-configuration()
    let $config := 
        try {
             if ("language"                                     eq $name) then admin:database-set-language($config, $database-id, xs:string($value))
        else if ("maintain-directory-last-modified"             eq $name) then admin:database-set-maintain-directory-last-modified($config, $database-id, fn:boolean($value))
        else if ("maintain-last-modified"                       eq $name) then admin:database-set-merge-enable($config, $database-id, fn:boolean($value))
        else if ("merge-enable"                                 eq $name) then admin:database-set-merge-max-size($config, $database-id, fn:boolean($value))
        else if ("merge-max-size"                               eq $name) then admin:database-set-merge-max-size($config, $database-id, xs:unsignedInt($value))
        else if ("merge-min-ratio"                              eq $name) then admin:database-set-merge-min-ratio($config, $database-id, xs:unsignedInt($value))
        else if ("merge-min-size"                               eq $name) then admin:database-set-merge-min-size($config, $database-id, xs:unsignedInt($value))
        else if ("merge-timestamp"                              eq $name) then admin:database-set-merge-timestamp($config, $database-id, xs:unsignedLong($value))
        else if ("name"                                         eq $name) then admin:database-set-name($config, $database-id, fn:boolean($value))
        else if ("one-character-searches"                       eq $name) then admin:database-set-one-character-searches($config, $database-id, fn:boolean($value))
        else if ("positions-list-max-size"                      eq $name) then admin:database-set-positions-list-max-size($config, $database-id, xs:unsignedInt($value))
        else if ("preallocate-journals"                         eq $name) then admin:database-set-preallocate-journals($config, $database-id, fn:boolean($value))
        else if ("preload-mapped-data"                          eq $name) then admin:database-set-preload-mapped-data($config, $database-id, fn:boolean($value))
        else if ("reindexer-enable"                             eq $name) then admin:database-set-reindexer-enable($config, $database-id, fn:boolean($value))
        else if ("reindexer-throttle"                           eq $name) then admin:database-set-reindexer-throttle($config, $database-id, xs:unsignedInt($value))
        else if ("reindexer-timestamp"                          eq $name) then admin:database-set-reindexer-timestamp($config, $database-id, xs:unsignedInt($value))
        else if ("stemmed-searches"                             eq $name) then admin:database-set-stemmed-searches($config, $database-id, xs:string($value))
        else if ("tf-normalization"                             eq $name) then admin:database-set-tf-normalization($config, $database-id, fn:boolean($value))
        else if ("three-character-searches"                     eq $name) then admin:database-set-three-character-searches($config, $database-id, fn:boolean($value))
        else if ("three-character-word-positions"               eq $name) then admin:database-set-three-character-word-positions($config, $database-id, fn:boolean($value))
        else if ("trailing-wildcard-searches"                   eq $name) then admin:database-set-trailing-wildcard-searches($config, $database-id, fn:boolean($value))
        else if ("trailing-wildcard-word-positions"             eq $name) then admin:database-set-trailing-wildcard-word-positions($config, $database-id, fn:boolean($value))
        else if ("two-character-searches"                       eq $name) then admin:database-set-two-character-searches($config, $database-id, fn:boolean($value))
        else if ("uri-lexicon"                                  eq $name) then admin:database-set-uri-lexicon($config, $database-id, fn:boolean($value))
        else if ("word-positions"                               eq $name) then admin:database-set-word-positions($config, $database-id, fn:boolean($value))
        else if ("word-query-fast-case-sensitive-searches"      eq $name) then admin:database-set-word-query-fast-case-sensitive-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-fast-diacritic-sensitive-searches" eq $name) then admin:database-set-word-query-fast-diacritic-sensitive-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-fast-phrase-searches"              eq $name) then admin:database-set-word-query-fast-phrase-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-include-document-root"             eq $name) then admin:database-set-word-query-include-document-root($config, $database-id, fn:boolean($value))
        else if ("word-query-one-character-searches"            eq $name) then admin:database-set-word-query-one-character-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-stemmed-searches"                  eq $name) then admin:database-set-word-query-stemmed-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-three-character-searches"          eq $name) then admin:database-set-word-query-three-character-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-three-character-word-positions"    eq $name) then admin:database-set-word-query-three-character-word-positions($config, $database-id, fn:boolean($value))
        else if ("word-query-trailing-wildcard-searches"        eq $name) then admin:database-set-word-query-trailing-wildcard-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-trailing-wildcard-word-positions"  eq $name) then admin:database-set-word-query-trailing-wildcard-word-positions($config, $database-id, fn:boolean($value))
        else if ("word-query-two-character-searches"            eq $name) then admin:database-set-word-query-two-character-searches($config, $database-id, fn:boolean($value))
        else if ("word-query-word-searches"                     eq $name) then admin:database-set-word-query-word-searches($config, $database-id, fn:boolean($value))
        else if ("word-searches"                                eq $name) then admin:database-set-word-searches($config, $database-id, fn:boolean($value))
        else    $config                                  
        }
        catch ($e) {(xdmp:log(text{"### Skipping setting database property:",$name,"(may be an invalid value)->",$value}), $config)}
        
    return admin:save-configuration-without-restart($config)
};

