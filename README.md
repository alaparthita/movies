Clone the repo and run the following command to start the movies app. DB is included in the repo. This has dependency on ratings app. Make sure to run ratings app first. 
./gradlew bootRun

OpenApi UI - application api endpoints
http://localhost:8080/swagger-ui/index.html

Application Health endpoint
http://localhost:8080/actuator/health

Application metrics endpoint (Prometheus)
http://localhost:8080/actuator/prometheus

- Tracing can be enabled levaraging open telemetry agent and environment variables

- Common logging format can be reffered in logback-spring.xml

- Separate applications yaml files for each environment

- This service invokes ratings endpoint on port 8081. The url is externalized in application.yaml

- Created index for genres, releaseDate in movies DB

- Implementation for searching generes can be improved through redesign (ex:- adding virtual columns, indexing json field data  etc...) 

- Lot of other improvements like Api authorization have to be managed by proxying through Api gateway.

- All the requested apis were tested using Postman. Unit tests are not completely done due to time constraints.

- Docker image can be built using Docker file

- api spec is available in the project root api-spec.yaml. This spec can also be downloaded from running app http://localhost:8080/v3/api-docs.yaml