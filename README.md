# Software Architecture 1

Software Architecture 1 course at University of Esslingen.

## Requirements

To run the course examples please install `docker` and `docker-compose` on your system. Any further tool requirements are handled through docker images.

## Usage

To run application enter:

```sh
cd docker
docker-compose up -d
```

A docker MySQL service should now be running in the background. You can shut it down via `docker-compose down` (executed inside the `docker` directory).

The MySQL instance uses a dedicated docker volume to store its database in. This database will be empty at first. **Please note that you will need to initialize the MySQL database yourself!** To help you with this, a MySQL Workbench Model is provided with this repository (see `sa1_licensemanager.mwb`). Synchronize it against your database and you should be good-to-go. You can of course also use it to investigate the database design.

Additionally a dev-container is started. This container packages all necessary tools to proceed. To save yourself the hassle of installing all the tools manually, do yourself a favor and execute the subsequent commands **inside** that container. You can get a shell inside the container by running:

```sh
docker execute -it docker_dev_1
# Note that the name of the container may vary. Check the output of 'docker-compose up -d'.
```

You now have a shell inside the dev-container. The project is automatically mounted under `/workspace`. Please navigate to that directory and follow the subsequent instructions.

You can also use vscode's `ms-vscode-remote.remote-containers` extension to do **all this and more automatically** for you! ;)

### Building and Running Webapps

To build the provided tomcat webapps execute the `gradlew` (on UNIX/Linux) or `gradlew.bat` (on Windows) with the task `war`; e.g.:

```sh
cd tomcat/licensemanager
./gradlew war
```

This should generate a `build/libs` directory in which you'll find the `*.war` file. Put this `*.war` file in your `tomcat/webapps` directory. Your tomcat server should automatically load the `*.war` file and make it available under `http://localhost:8080/<project-name>`.

**Alternatively you can start tomcat directly via gradle.** To allow this to work, stop your system tomcat, as it would otherwise interfere with the local instance we're about to create. Now to start a local tomcat, again, execute `gradlew` or `gradlew.bat`, this time with the task `appRun`; e.g.:

```sh
cd tomcat/licensemanager
./gradlew appRun
```

The local tomcat is again accessible via `http://localhost:8080/<project-name>`.

### Running Tests

The `licensemanager` application ships with two sets of tests: `unit-tests` and `integration-tests`.

To execute them, run:

```sh
cd tomcat/licensemanager
./gradlew test
./gradlew integrationTest # this task will spawn its own tomcat, execute the test, and shut it back down
```

Note that the task `integrationTest` is not *yet* part of the meta-task `test`.

### Viewing the Code

As a sane person would use vscode, simply installing the `ms-vscode-remote.remote-containers` extension on your host would suffice. Any additional extensions then loaded through the `.devcontainer/devcontainer.json` configuration, once the workspace is loaded (if life could always be this easy, right?). To help the java language server a bit, run `./gradlew eclipse` to generate a `.classpath` file. This should enable indexing and code-completion. To make it work, open the sub-directory as workspace (`CTRL + k + o` -> navigate to `tomcat/licensemanager` -> hit "OK").

I personally recommend vscode, but the gradle build chain also generates eclipse-compatible `.classpath` and `.project` by running `./gradlew eclipse`. This is not tested/verified - Good Luck!

### Getting it Secure

Have a look at `docker/generate-certificate.sh` to get HTTPS to work. Note that you will also have to change the ReactJS REST endpoint inside `.../src/main/frontend/src/ServerInfo.js`.
