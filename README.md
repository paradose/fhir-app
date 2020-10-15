## FHIR application 
An application connected to Monash's FHIR server built around OOP design principles and patterns using Java. Using HAPI to make API requests to the server, the application monitors patient's cholesterol and blood pressure levels in real time for the given practitioner. 

### Setup
The following must be installed:
* git
* Java
* JDK 1.8
* Maven

### Instructions:

At the terminal navigate to the root of the project folder "Assignment-2-FHIR",
 and then run the maven package command to create the executable of the project.
```
cd Assignment-2-FHIR
mvn package 
```

Access the JAR executable within the target folder.
```
java -jar target\Assignment-3-FHIR-1.0-SNAPSHOT-jar-with-dependencies
```
