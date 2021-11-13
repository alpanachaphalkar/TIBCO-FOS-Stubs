# TIBCO-FOS-Stubs
This repository contains stubs of TIBCO FOS Process Components.

Documentation: [Stubs](https://sky.atlassian.net/wiki/spaces/CRM/pages/1167655428/Stubs)

# Steps to run stubs
### Requirements
* Installation of Java JDK 
* Installation of Maven
* TIBCO EMS (MOM Provider) is up and running.

Maven is used as build tool for building, deploying and running this Java project.

### Steps
**Step 1**: Clean and Install all dependencies
```
mvn clean install
```
**Step 2**:  Execute Stubs with main method
```
mvn exec:java  -Dexec.mainClass=Stubs
```