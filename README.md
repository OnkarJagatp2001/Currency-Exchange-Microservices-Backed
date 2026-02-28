

### Docker Image Creation Flow
After code changes go to Maven sidebar option --> then under the Lifecycle -> Package double click on it (It will create Jar file)

### Command to create image
mvn spring-boot:build-image -DskipTests


Dependencies
<configuration>
    <image>
       <name>onkar22/mmv2-${project.artifactId}:${project.version}</name>
    </image>
    <pullPolicy>IF_NOT_PRESENT</pullPolicy>
</configuration>

### Create individual docker image
docker-compose up -d currency-conversion

### To stop running container
docker-compose stop currency-conversion

#### Restart the Stack
docker-compose down

### To start everything
docker-compose up -d

#### To check health of running container
docker stats

in28min github
https://github.com/in28minutes/spring-microservices-v3/tree/main/05.kubernetes