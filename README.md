# Software Architecture 1

Software Architecture 1 course at University of Esslingen.

## Requirements

To run the course examples please install `docker-compose` on your system.
Any further tool requirements are handled through docker images.

## Usage

To run application enter:

```sh
cd docker
docker-compose up -d
```

You should now find two new folders in the project root:

* `tomcat/webapps`
* `nodejs/`

These are volumes, linked into the newly created service containers.

Apart from the volumes you should also have a tomcat service running, accessible
from your host machine via `http://localhost:8080`. A MySQL service should also
be running on `localhost:5000`.

### Building Webapps

To build the provided tomcat webapps execute the `gradlew` (on UNIX/Linux) or
`gradlew.bat` (on Windows) with the task `war`; e.g.:

```sh
cd tomcat/webdemo
./gradlew war
```

This should generate a `build/libs` directory in which you'll find the `*.war`
file. Put this `*.war` file in the `tomcat/webapps` directory. The tomcat server
should automatically load the `*.war` file and make it available under
`http://localhost:8080/<project-name>`.

Alternatively you can start tomcat directly via gradle. To allow this to work,
stop the docker-compose tomcat, as it would otherwise interfere with the local
instance we're about to create. Now to start a local tomcat again execute
`gradlew` or `gradlew.bat`, this time with the task `tomcatRunWar`; e.g.:

```sh
cd tomcat/webdemo
./gradlew tomcatRunWar
```

The local tomcat is again accessible via `http://localhost:8080/<project-name>`.
Note that due to the docker services being powered down, the MySQL database is
unavailable.
