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

* `tomcat/`
* `nodejs/`

These are volumes, linked into the newly created service containers.

Apart from the volumes you should also have a tomcat service running, accessible
from your host machine via `http://localhost:8080`. A MySQL service should also
be running on `localhost:5000`.
