Google Classroom add-ons Java Examples
========================================

This project hosts web applications that demonstrate the implementation of a Google
Classroom add-on using Java. Current examples use the [Spring Boot framework](https://spring.io/).

Documentation
-------------

These examples are intended to accompany the guided walkthroughs on the
[Classroom Add-ons developer site](https://developers.google.com/classroom/eap/add-ons-alpha).
Please refer to the site for implementation details.

Requirements
------------
*   Java 11+

Project Setup
------------

1. Create a [Google Cloud Platform (GCP) project](https://console.cloud.google.com/projectcreate).
Enable the following in the API Library:
    *   [Google Workspace Marketplace (GWM) SDK](https://console.cloud.google.com/apis/library/appsmarket-component.googleapis.com)
    *   [Google Classroom API](https://console.cloud.google.com/apis/library/classroom.googleapis.com)

    Visit the
    [developer site](https://developers.google.com/classroom/eap/add-ons-alpha/build-classroom-addon#step_3_google_workspace_marketplace_listing)
    for configuration instructions for the GWM SDK. You will also need to
    [install the add-on](https://developers.google.com/classroom/eap/add-ons-alpha/creating-simple-add-on#visit_the_unlisted_url_for_your_add-on_to_install_it)
    for it to be visible in Google Classroom.

2. Visit your project's [Credentials](https://console.cloud.google.com/apis/credentials) page. Create two credentials in the project:
    *   An **API Key**. You can leave it as **Unrestricted** for the purposes of these examples.
    *   An **OAuth client ID**.
        *   The application type should be **Web application**.
        *   Add `<your server>/callback` as an **Authorized redirect URI**. For example,
        `https://localhost:5000/callback`

    Return to the Credentials page once both have been created, then:
      *   Copy your **API Key** and assign it to the environment variable `GOOGLE_API_KEY`:
          ```shell
          export GOOGLE_API_KEY=<YOUR_COPIED_API_KEY>
          ```
      *   Download the **OAuth2 client credentials** as JSON.

3. Install [Java 11+](https://adoptopenjdk.net/).

4. Clone this repository:

    ```posix-terminal
    git clone https://github.com/<org>/<repo>/
    ```

5. Open the project in an IDE that supports Spring applications. Some popular IDEs include IntelliJ or Eclipse.
This project uses Maven to build the project and manage dependencies, and includes the Maven wrapper
that will ensure a successful build without needing to install Maven itself. The project includes two executables:
`mvnw` for Unix and `mvnw.cmd` for Windows. Running `./mvnw --version` on Unix will display the Apache Maven
version among other information. The `pom.xml` file defines the project's dependencies needed to run the application.
One of the dependencies this project uses is Thymeleaf which limits repetitive code across HTML files through the
use of fragments.

6. To test the add-on within the Classroom add-on iframe, you will need to run the application server with SSL encryption.
We recommend using [mkcert](https://github.com/FiloSottile/mkcert) to generate keys for `localhost` if you would like to
host the server on your local machine. Use the following commands to generate a certificate using mkcert:

    ```posix-terminal
    mkcert -install

    mkcert -pkcs12 -p12-file <path_to_keystore> <domain_name>
    ```

    As an example, the `path_to_keystore` can be `src/main/resources/<name_of_keystore>`. The domain name is 
    the domain you will be running the project on (for example, `localhost`). Then, in `application.properties`,
    set `server.ssl.key-store=<path_to_keystore>` where the `path_to_keystore` is the path you included when
    running the second mkcert command above.

7. Launch the server by running the `main` method in the `Application.java` file. In IntelliJ, for example, you
can either right-click `Application.java` > `Run 'Application'` in the `src/main/java/com/addons/spring` directory
or open the `Application.java` file to click on the green arrow to the left of the `main(String[] args)` method
signature. Alternatively, you can run the project in a terminal window:

    ```posix-terminal
    ./mvnw spring-boot:run
    ```

   or on Windows:

    ```posix-terminal
    mvnw.cmd spring-boot:run
    ```

    This will launch the server at `https://localhost:5000` or at the port you specified in `application.properties`.

8. To load the application, either open the app in your browser or select the app in the **Add-ons** menu when creating
an Assignment in [Google Classroom](https://classroom.google.com).

Useful Resources
-------------

<!-- *   [Issue tracker](https://github.com/<org>/<repo>/issues) -->
*   [Add-ons Guide](https://developers.google.com/classroom/eap/add-ons-alpha)
*   [Add-ons Reference](https://developers.google.com/classroom/eap/add-ons-alpha/reference/rest)
*   [Using OAuth 2.0 for Web Server Applications](https://developers.google.com/identity/protocols/oauth2/web-server#creatingclient)
*   [OAuth 2.0 Scopes](https://developers.google.com/identity/protocols/oauth2/scopes)
*   [Google Classroom Discovery API](https://googleapis.github.io/google-api-Java-client/docs/dyn/classroom_v1.html)
*   [Google OAuth2 Discovery API](https://googleapis.github.io/google-api-Java-client/docs/dyn/oauth2_v2.html)
*   [Classroom API Support](https://developers.google.com/classroom/eap/add-ons-alpha/support)

Authors
-------

*   [Mahima Desetty](https://github.com/mahima-desetty)
