# Flux-Server

This is the Flux-Server component of the Flux-Coordinator system.

## How to run this application in development mode

**Prerequisites**: You need to install [SBT](https://www.scala-sbt.org/) and Java 8. Java has to be in your path variable. Also, follow the [Installing the database](./README.md#installing-the-database) guide before trying to run the application.

1. Navigate to the root folder of this repository.
2. Execute sbt to start the sbt service (optional).
3. If you are already in the sbt shell, you can now use the `run` command to start the service on `localhost:9000`. If you are not in the sbt shell, you can use the command `sbt run` to start the service on `localhost:9000`.

Running in production mode lets your application auto-compile when you have made changes to the project. This might be very useful during the development of the project, but makes your application slower. If you need to run the application in a productive environment, read the next chapter.

IntelliJ IDEA offers an optional plugin for the Play Framework. Using this plugin, you can import the project as an sbt project into the IDE and let the IDE handle the interactions with the SBT shell.

## How to create a production build

1. Run the SBT shell from the root folder of this repository.
2. Use the `dist` command to create a distribution build. A new folder will be created (path will be output into the console). The folder contains two script files to run the application on Windows and on Linux systems.

The production build makes some optimizations to make your application faster. There are also some security concerns you should be aware of. Before deploying your application to a productive environment and for more informations, please visit [this](https://www.playframework.com/documentation/2.6.x/Deploying) page.

## Installing the database

To make the server work, you need to install PostgreSQL 9.6. The default user and password configured for the connection with the database are "postgres" and "postgres". **Please change the credentials for a production deployment**.

After installing the DBMS, you need to create a new database called *flux* and run the [CREATE_DB.sql](./CREATE_DB.sql) script on that database in order to create the schema required by the application.