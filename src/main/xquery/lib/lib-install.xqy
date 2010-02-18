xquery version "1.0-ml";
(: Copyright 2009, Mark Logic Corporation. All Rights Reserved. :)

module namespace inst = 'http://www.marklogic.com/ps/lib/lib-install.xqy';

declare namespace conf = "http://www.marklogic.com/ps/install/config.xqy";

declare function  inst:validate-string($context as xs:string, $item-description as xs:string, $value as xs:string*, $default as xs:string*)  as xs:string*
{
    if($value) then $value
    else if($value eq "") then $value
    else if($default) then ($default, xdmp:log(text{"-- NOTICE --",$context,
        "- Setting default value:",
        fn:concat("'",$default,"'"),
        "for:",
        $item-description
        }))
    else xdmp:log(text{"-- ERROR --",$context,
        "- Missing value/s for:",
        $item-description
        })
};