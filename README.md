# TIBCO-FOS-Stubs
This repository contains stubs of TIBCO FOS Process Components.

Documentation: [Stubs](https://sky.atlassian.net/wiki/spaces/CRM/pages/1167655428/Stubs)

# Directory Structure
Following is the project directory structure:
```
TIBCO-FOS-Stubs
|- pom.xml
|- .gitignore
|- README.md
|- lib
    |- com
        |- ...
    |- javax
        |- ...
    |- org
        |- ...
|- src
    |- main
        |- java
            |- ...
        |- resorces
            |- ...
```
Folder `lib` is treated as a local repository for building this maven project. So all the dependencies (`.jar` files) reside inside `lib` folder.

Folder `src/main/resources` contains the configuration files required for executing stubs.

Folder `src/main/java` contains the source code in `.java` files.

# Steps to run TIBCO FOS Stubs
### Pre-requisite
* Installation of Java JDK 
* Installation of Maven
* TIBCO EMS (Message Oriented Middleware Provider) Server is up and running.

Maven is used as build tool for building, deploying and running this Java project.

### Steps
**Step 1**: Configure environment variables
```
TIBCO_EMS_SERVER_HOST
TIBCO_EMS_SERVER_PORT
TIBCO_EMS_USERNAME
TIBCO_EMS_PASSWORD
TIBCO_EMS_CERTIFICATE_PATH
```

Connection string for TIBCO EMS Server:
```
ssl://$TIBCO_EMS_SERVER_HOST:$TIBCO_EMS_SERVER_PORT
```

**Step 2**: Clean and Install all dependencies
```
mvn clean install
```

**Step 3**:  Execute Stubs with main method
```
mvn exec:java  -Dexec.mainClass=Stubs
```