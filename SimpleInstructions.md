# How to deploy  and run #

  * Start Apache Sling using the launchpad/builder standalone jar, current trunk, or using a container.

  * Build the project using the next maven command: `mvn -P autoInstallBundle clean install -Dsling.url=http://localhost:8080/system/console`

  * Autenthicate with admin user in your Apache Sling instance (this is only because actually the authentication isn't implemented)

  * Start your browser at http://localhost:8080/content/david.html


