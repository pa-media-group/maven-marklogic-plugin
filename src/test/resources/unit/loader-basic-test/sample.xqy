xquery version '1.0-ml';

declare variable $some_property as xs:string external;

let $message := 'Hello World!'
return
<results>
   <message>{$message} - {$some_property}</message>
</results>