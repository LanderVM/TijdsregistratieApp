# MobileTRM Quarkus Back-end

The Quarkus back-end serves as an API for web clients and the MobileTRM Android app.

## Prerequisites

* IntelliJ
* JDK 21 (from [Eclipse Temurin](https://adoptium.net/en-GB/temurin/releases/?package=jdk))
* Docker Desktop and Testcontainers (
  follow [this](https://dev.azure.com/R-JavaCC/008%20J%20Mobile%20TRM%20Geo/_wiki/wikis/008-J-Mobile-TRM-Geo.wiki/90/Quarkus-with-MariaDB)
  tutorial)

## Installing and running the application

Make sure you've made a ``.env`` file in the root of the project with the following configuration:

````env
OIDC_SECRET=auth_0_secret_here
OIDC_TEST_USERNAME=test_user_email_here
OIDC_TEST_PASSWORD=test_user_password_here
````

> **Note:** You can find these
> values [here](https://dev.azure.com/R-JavaCC/008%20J%20Mobile%20TRM%20Geo/_wiki/wikis/008-J-Mobile-TRM-Geo.wiki/130/Environment-variables#).


Quarkus Dev Services will automatically configure and launch containers for your database and other dependencies.  
Make sure you've installed and configured Docker Desktop and Testcontainers as
seen [here](https://dev.azure.com/R-JavaCC/008%20J%20Mobile%20TRM%20Geo/_wiki/wikis/008-J-Mobile-TRM-Geo.wiki/90/Quarkus-with-MariaDB).

You can use the following script to run the Quarkus application in dev-mode:

```shell script
./mvnw compile quarkus:dev
```

> **Note**: if you get an error trying to run the Maven script above due to your *JAVA_HOME* not being set,
> follow [this](https://www.baeldung.com/java-home-on-windows-mac-os-x-linux) tutorial.

## Testing

### View API Endpoints

Make sure you've started the application beforehand by running the script as mentioned in
***Installing and running the application***.

Once the application has started, you can navigate to http://localhost:8080/. <br/>
(TODO) You can check out all the REST API endpoints at http://localhost:8000/q/swagger.

### Automatic Testing

Right-click the test classes and press "Run".

### Integration tests

The tests in this project will run automatically during compilation, which can be run using this script:

```shell script
./mvnw package
```

If these tests were to fail, compilation will fail as well.

### Manual Testing

You can run the tests directly in IntelliJ by navigating to [unit tests](src/test/java/world/inetumrealdolmen/domain)
or [integration tests](src/test/java/world/inetumrealdolmen/dto).

In order to run integration tests manually in IntelliJ, make sure you've built the application using this script first:

```shell script
./mvnw package -DskipTests
```

## Contributing

When creating new entities, please be sure to extend from BaseEntity and by
following [these](https://www.sqlstyle.guide/) SQL naming conventions in your Jakarta annotations.