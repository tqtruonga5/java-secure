Introduction
============

This Rabbit Holes project is a web application that contains quite a few security holes. It attempts to demonstrate how a Java web application could be easily vulnerable if secure coding practices are ignored.

This application uses

* Servlet 3.1
* Jsp 2.1

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

* Install a MySQL database for the application(Optional if you use the embedded H2 database).

Configuration
--------------

You can configure the application by following these steps:

1.  Config the database connection in the file resources/database.properties.
```
       connectionString=jdbc:h2:/tmp/rabbitholedb
       username=
       password=
```
2. Config the location of the upload files in the file resources/application.properties
```
       upload.location=/tmp/upload/
```
Running The Application
------------------------

You can run the application in development mode using the following command at the command prompt:

    mvn jetty:run

If you want to deploy the application to Tomcat, you have to use Tomcat 7 or newer.
