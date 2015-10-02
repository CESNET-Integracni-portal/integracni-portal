## DEPLOYMENT

#### Prerequisites: 
- java 8
- installed tomcat server (developed with tomcat 7)
- installed PostgreSQL server

### 1) integracni-portal

**prerequisites**: installed maven 3

1. run `mvn clean install` in project root

2. move `target/integracni-portal.war` to `TOMCAT_HOME/webapps`
 
3. copy `etc/portal.properties` and `etc/cesnet.properties` to some folder on server (e.g. `/var/lib/tomcat7/conf/`) and edit them accordingally (set addresses, passwords, users, ...)

4. add the `-Dportal.config.location=file://<path-to-folder-above>/` VM argument to JAVA_OPTS in `TOMCAT_HOME/bin/setenv.sh`:

   - on Linux `export JAVA_OPTS="-Dportal.config.location=file:///var/lib/tomcat7/conf/"`
   - on Windows `set JAVA_OPTS=%JAVA_OPTS% -Dportal.config.location=file:///C:\path\to\conf\folder\`


### 2) integracni-portal-ui

**prerequisites**: 

- installed NodeJS
- if gulp and bower are not installed, install them globally
  
        npm install gulp -g
        npm install bower -g

1. run `npm install` in project root

2. if needed, change configuration in `config/env_prod.json`
 - rootContext - the root path to application on tomcat (e.g. 'integracni-portal-ui' for integracni-portal-ui.war)
 - baseUrl - base url to REST API (e.g. 'http://example:8080/integracni-portal/rest/v0.2')
 - oauthUrl - url to oauth endpoint (e.g. 'http://example:8080/integracni-portal')
 
3. run `gulp package` in project root

4. move `build/integracni-portal-ui-*.war` file to `TOMCAT_HOME/webapps` (and possibly rename to integrani-portail-ui.war to match the root context above)

