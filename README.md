

### Docker Image Creation Flow
After code changes go to Maven side bar option --> then under the Lifecycle -> Package double click on it (It will create Jar file)

Command to create image
mvn spring-boot:build-image -DskipTests


Dependecies
<configuration>
    <image>
       <name>onkar22/mmv2-${project.artifactId}:${project.version}</name>
    </image>
    <pullPolicy>IF_NOT_PRESENT</pullPolicy>
</configuration>
