

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

Snapshots
<img width="1575" height="402" alt="image" src="https://github.com/user-attachments/assets/56678a10-984e-4cc8-8e9c-d7a8a3c7f5fe" />

<img width="1908" height="903" alt="image" src="https://github.com/user-attachments/assets/7f7a1f08-1fca-4d1c-a986-ed19eba7488e" />

<img width="1919" height="993" alt="image" src="https://github.com/user-attachments/assets/571dc0c9-3d11-4cfc-967c-8ab9e9b838b2" />
