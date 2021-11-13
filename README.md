# TIBCO-FOS-Stubs
This repository contains stubs of TIBCO FOS Process Components.

Documentation: [Stubs](https://sky.atlassian.net/wiki/spaces/CRM/pages/1167655428/Stubs)

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
```

**Step 2**: Clean and Install all dependencies
```
mvn clean install
```

**Step 3**:  Execute Stubs with main method
```
mvn exec:java  -Dexec.mainClass=Stubs
```