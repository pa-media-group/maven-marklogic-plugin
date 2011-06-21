# Marklogic Plugin for Maven

The MarkLogic Plugin is used to install and configure MarkLogic Server applications

The MarkLogic Plugin has the following goals:

+ marklogic:bootstrap is used create the necessary bootstrap configuration that the MarkLogic Plugin requires for executing its goals.
+ marklogic:bootstrap-uninstall is used to remove the bootstrap configuration created by the marklogic:bootstrap goal.
+ marklogic:install is used to install and configure the database(s), application server(s), etc. in the specified configuration.
+ marklogic:uninstall is used to remove all database(s), application server(s), etc. created by the marklogic:install goal.
+ marklogic:record-load is used to load data into a MarkLogic database using the RecordLoader utility.

## Configuration

The basic configuration required is as follows.

    <project>
    ...
      <build>
        <plugins>
          <plugin>
            <groupId>com.marklogic.maven</groupId>
            <artifactId>maven-marklogic-plugin</artifactId>
            <version>0.2-SNAPSHOT</version>
            <configuration>
            ...
            </configuration>
          </plugin>
        </plugins>
      </build>   
    ...
    </project>
    
### Bootstrap XCC (XDBC) Server

The bootstrap server provides the entrypoint for installing and configuring a Marklogic application, and provides the
following configurable properties

#### xdbcPort

The port assigned to the bootstrap XCC server.

    <xdbcPort>8997</xdbcPort>
    
#### xdbcName

The identifier name assigned to the bootstrap XCC server.

    <xdbcName>8997-Bootstrap</xdbcName>

#### xdbcModulesDatabase

The name of the modules database to be created for the bootstrap server.

When installing the bootstrap a modules database will be created and the 
install libraries will be uploaded to this database.

    <xdbcModulesDatabase>Bootstrap-Modules</xdbcModulesDatabase>
    
It is possible to set the modules to be located on the filesystem by using the
value 'file-system', in which case the module root is the location on disk that
the modules should exist.

**Note:** If using file-system the XQuery libraries will need to be installed manually. 
    
#### xdbcModuleRoot

The location within the modules database that the bootstrap files should be located.

    <xdbcModuleRoot>/</xdbcModuleRoot>
    
Or if using file-system modules location then this is the location within the filesystem.
    
#### database

The name of the database that the bootstrap XCC server should connect to as default.

    <database>Documents</database>
    
### Install Configuration

There are two ways of configuring the installation of the Marklogic application, 
inline (preferred) and external.

#### environment

The current environment to install.

    <environment>development</environment>
    
####  installConfigurationFile

This specifies the usage of an external configuration file instead of using the 
configuration specified in the active environment block.

    <installConfigurationFile>${basedir}/src/main/marklogic/configuration.xml</installConfigurationFile>
    
**Note:** Whilst this is used instead of the configuration in the environment, 
if the resources and pipeline-resources elements are specified for the current
environment then these will still be deployed as configured.

#### environments (preferred inline configuration method)

Environments specified within the configuration block are used to generate a
configuration XML containing the install instructions for the current environment.

Individual environments are specified within the environments block.

    <environments>
      <environment>
        <name>development</name>
        ...
      </environment>
      <environment>
        <name>testing</name>
        ...
      </environment>
      <environment>
        <name>production</name>
        ...
      </environment>
      ...
    </environments>
    
An environment contains using the following parameters

#### name (required)

The unique name for the environment.

    <name>development</name>

#### applicationName (required)
    
The name for the application, this should be of the form __[A-Za-z][A-Za-z0-9-]*__
and is used as a common prefix for the server and databases.
                  
    <applicationName>Test-Dev</applicationName>

#### title (required)

The title for the application.
    
    <title>Test (Development)</title>
    
#### filesystem-root

The filesystem-root for the application.

    <filesystem-root>/</filesystem-root>

#### databases

A set of database nodes to be constructed

    <databases>
        <database name="Modules">
            <forest name="Modules">
                <forest-directory/>
            </forest>

            <attribute-range-index  scalar-type="string" parent-namespace=":::performance:::"
                                    parent-localname="profile" namespace="" localname="label" />
        </database>

        <database name="Content">
            <triggers-database name="Triggers"/>

            <set name="uri-lexicon"  value="true"/>
            <set name="word-searches"  value="true"/>
            <set name="word-positions" value="true"/>
            <set name="in-memory-list-size" value="256"/>
            <set name="in-memory-tree-size" value="16"/>
            <set name="element-value-positions" value="true"/>
            <set name="element-word-positions"  value="true"/>
            <set name="attribute-value-positions"  value="true"/>

            <add name="fragment-root"   namespace="urn:coralreef:logs" localname="logEntry"/>
            <add name="fragment-root"   namespace="urn:coralreef:user" localname="read-alert"/>
            <add name="fragment-root"   namespace="urn:coralreef" localname="changeLog"/>
            <add name="fragment-root"   namespace="urn:coralreef" localname="Identities"/>

            <forest name="Content">
                <forest-directory/>
            </forest>

            <element-word-lexicon   namespace="" localname="cr-status"/>
            <element-word-lexicon   namespace="" localname="cr-owner-credential"/>
            <element-word-lexicon   namespace="urn:coralreef" localname="UniqueId"/>
            <element-word-lexicon   namespace="urn:coralreef" localname="Identity"/>

            <element-range-index    scalar-type="dateTime" namespace="" localname="cr-upload-dateTime"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="Location"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="Identity"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="AcquiringUnit"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="ForensicUnit"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="HandsetMake"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="HandsetModel"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="ServiceProvider"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="Value"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef" localname="UniqueId"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef:alerting" localname="Realm"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef:alerting" localname="alertLevel"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef:alerting" localname="alertType"/>
            <element-range-index    scalar-type="dateTime" namespace="urn:coralreef:alerting" localname="alertDtg"/>
            <element-range-index    scalar-type="string" namespace="urn:coralreef:alerting" localname="userCredential"/>

            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef" parent-localname="Selector" namespace="" localname="SearchKey"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef" parent-localname="Identities" namespace="" localname="SearchKey"/>
            <attribute-range-index  scalar-type="date" parent-namespace="urn:coralreef" parent-localname="AcquireDate" namespace="" localname="NormalizedDate" />
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef:alerting" parent-localname="triggerDocument" namespace="" localname="Uri"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef:alerting" parent-localname="target" namespace="" localname="id"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef:user" parent-localname="read-alert" namespace="" localname="Uri"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef:target" parent-localname="Selector" namespace="" localname="SearchKey"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef:target" parent-localname="target" namespace="" localname="id"/>
            <attribute-range-index  scalar-type="string" parent-namespace="urn:coralreef" parent-localname="domexHistory" namespace="" localname="uniqueId" />
            <attribute-range-index  scalar-type="string" parent-namespace=":::performance:::" parent-localname="profile" namespace="" localname="label" />

            <content-processing user="admin" role="admin" modules="Modules" root="/" default-domain="Test">
                <load-pipelines>
                    <from-filesystem file="Installer/cpf/status-pipeline.xml"/>
                    <from-filesystem file="Installer/conversion/alternatives/basic-pipeline.xml"/>
                </load-pipelines>
                <domain name="Test" description="Test" document-scope="directory" uri="/Content/Raw/" depth="infinity" modules="Modules" root="/">
                    <pipeline name="HTML Conversion"/>
                    <pipeline name="Conversion Processing (Basic)"/>
                    <pipeline name="Status Change Handling"/>
                </domain>
            </content-processing>
        </database>

        <database name="Triggers">
            <forest name="Triggers">
                <forest-directory/>
            </forest>
        </database>
    </databases>
    
#### servers

A set of servers to be constructed.

    <servers>
        <server type="http"   name="Application" port="9000" group="Default" database="Content"   root="/opt/CR-MarkLogic/Modules" modules="0">
            <url-rewriter>rewrite.xqy</url-rewriter>
        </server>
        <server type="xdb"    name="Content"     port="9001" group="Default" database="Content"   root="/opt/CR-MarkLogic/Modules" modules="0"/>
        <server type="webdav" name="Content"     port="9002" group="Default" database="Content"   root="/" modules="0" authentication="application-level" default-user="admin"/>
        <server type="webdav" name="Modules"     port="9003" group="Default" database="Modules"   root="/" modules="0" authentication="application-level" default-user="admin"/>
        <server type="http"   name="Content"     port="9004" group="Default" database="Content"   root="/" modules="Content"/>
    </servers>
    
#### pipeline-resources

A custom FileSet describing pipeline XML files that should be deployed prior to
executing the install-cpf phase of the application installation. 

Database should be the triggers database for the application.

    <pipeline-resources>
        <resource>
            <database>Triggers</database>
            <directory>src/main/marklogic</directory>
            <includes>
                <include>html-convert.xml</include>
            </includes>
        </resource>
    </pipeline-resources>
    
#### resources

A custom FileSet describing files that should be loaded into the defined database
after the installation of the application.

    <resources>
        <resource>
            <database>Modules</database>
            <directory>src/main/xquery</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>**/*.xqy</include>
            </includes>
        </resource>
    </resources>