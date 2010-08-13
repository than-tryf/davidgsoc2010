David Mini CMS

This is a Google Summer of Code 2010 project
Student: Federico Paparoni
Mentor: by Bertrand Delacretaz

The goal is to create a mini-CMS with Sling, that demonstrates Sling best practices.
Full documentation available at https://cwiki.apache.org/confluence/display/SLING/GSoC+2010+mini-CMS+project

INSTALLATION 

1) Make sure your Apache Sling is started
2) Launch the next maven command on the two project "core" and "libraries"

mvn -P autoInstallBundle clean install -Dsling.url=http://localhost:8080/system/console
 
3) Browse David at http://localhost:8080/content/david.html