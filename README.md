Introduction
============

This Rabbit Holes project is a web application that contains quite a few security holes. It attempts to demonstrate how a Java web application could be easily vulnerable if secure coding practices are ignored.

This application uses

* Spring Framework 3.2.X
* Spring Security 3.2.0

Prerequisites
===============

This application has the following software prerequisites:

* JDK 7
* Maven 3

Running The Application
========================

This section describes the steps which are required to run the application.

Preparations
-------------

* Create a MySQL database for the application.

Configuration
--------------

You can configure the application by following these steps:

1.  Configure the database connection. The database connection is configured in the file *profiles/dev/config.properties*.

        db.driver=com.mysql.jdbc.Driver
        db.url=jdbc:mysql://localhost:3306/rabbitholes
        db.username=rabbitholes
        db.password=password

Running The Application
------------------------

You can run the application in development mode using the following command at the command prompt:

    mvn jetty:run

If you have MySQL configured, you can run the application in production mode using:

    mvn jetty:run -P prod

If you want to deploy the application to Tomcat, you have to use Tomcat 7 or newer.

Running Tests
=============

1.  You can run unit tests by running the following command at the command prompt:

        mvn test -P dev