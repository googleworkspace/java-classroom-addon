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

1. Install [Java 11+](https://adoptopenjdk.net/).

2. Create a [Google Cloud project](https://console.cloud.google.com/projectcreate).
Enable the following in the API Library:
    *   [Google Workspace Marketplace (GWM) SDK](https://console.cloud.google.com/apis/library/appsmarket-component.googleapis.com)
    *   [Google Classroom API](https://console.cloud.google.com/apis/library/classroom.googleapis.com)

    Visit the
    [developer site](https://developers.google.com/classroom/eap/add-ons-alpha/build-classroom-addon#step_3_google_workspace_marketplace_listing)
    for configuration instructions for the GWM SDK. You will also need to
    [install the add-on](https://developers.google.com/classroom/eap/add-ons-alpha/creating-simple-add-on#visit_the_unlisted_url_for_your_add-on_to_install_it)
    for it to be visible in Google Classroom.
3. Clone this repository:

     ```posix-terminal
     git clone https://github.com/<org>/<repo>/
     ```

4. Visit your project's [Credentials](https://console.cloud.google.com/apis/credentials) page. Create two credentials in the project:
    *   An **API Key**. You can leave it as **Unrestricted** for the purposes of these examples.
    *   An **OAuth client ID**.
        *   The application type should be **Web application**.
        *   Add `<your server>/callback` as an **Authorized redirect URI**. For example,
        `https://localhost:5000/callback`

    Return to the Credentials page once both have been created, then:
      * Download the **OAuth2 client credentials** as JSON. The credentials file will not be used for the
        first step, the `step_01_basic_app` module. For all other steps or modules, place the `client_secret.json`
        file in the `src/main/resources` directory of the module. You may have to rename the file to
        `client_secret.json` if it downloaded as a different name.
      * If you change the name or location of the file, ensure that you update the `CLIENT_SECRET_FILE` variable in the
        `AuthService.java` file  accordingly. We have already included the `client_secret.json` file path in the `.gitignore`
        of the top level directory, but if you change place it in a different location than the one specified, please update
        the `.gitignore` accordingly.

5. The walkthroughs are organized in modules. Each module represents a different step in the walkthrough. The `pom.xml`
   in the parent directory defines the modules contained in this project and the various dependencies needed for the modules.

6. To test the add-on within the Classroom add-on iframe, you will need to run the application server with SSL encryption.
We recommend using [mkcert](https://github.com/FiloSottile/mkcert) to generate keys for `localhost` if you would like to
host the server on your local machine. Use the following commands to generate a certificate using mkcert:

    ```posix-terminal
    mkcert -install

    mkcert -pkcs12 -p12-file <path_to_keystore> <domain_name>
    ```

    You may store the keystore file in the parent directory or within the modules themselves. For convenience, storing
the keystore file in the parent directory might be simpler to manage. Another option is to store the keystore file in
the modules themselves to further separate the modules as their own applications. Wherever you create and store the
keystore file, update the `application.properties` `server.ssl.key-store=<PATH_TO_KEYSTORE>` property for the specific
module that you would like to run with the relative path to the keystore. For example, if you create the keystore file
in the parent directory, update application.properties to `server.ssl.key-store=../keystore-file-name.p12`. Ensure that
you add the path to the keystore in the top level directory's `.gitignore` to make sure it is not
accidentally pushed to a remote repository. Lastly, the `domain_name` in the mkcert command above is the domain you will
be running the project on (for example, `localhost`).

7. This project uses Maven to build the project and manage dependencies, and includes the Maven wrapper that will
ensure a successful build without needing to install Maven itself. The project includes two executables:
`mvnw` for Unix and `mvnw.cmd` for Windows. Running `./mvnw --version` on Unix will display the Apache Maven version
among other information.

Run the Project
------------
You can run the project through the IDE of your choice. You can also run the project in a terminal window.
Open a terminal window and run the app from the directory of the module you would like to run. In order to run the
project, the modules will need to be configured appropriately.

I. Set up the `step_01_basic_app` walkthrough. Navigate to the `step_01_basic_app` directory and ensure that you have
updated `application.properties` with the path to the keystore file described in Step 6 of the Project Setup section
of this README file.

II. Set up the `step_02_sign_in` walkthrough by ensuring that you have:
1. Updated `application.properties` with the path to the keystore file described in Step 6 of the Project Setup section
of this README file. Ensure that you add the path to the keystore in the top level directory's `.gitignore` to make sure
it is not accidentally pushed to a remote repository.
2. Put the `client_secret.json` file in the `src/main/resources/` directory so it can be read in the `AuthService.java` file.
_Ensure that the path to the client secret file is in the top level directory's `.gitignore` so that you do not accidentally
push your client secrets to a remote repository._
3. Updated the `REDIRECT_URI` variable in the `AuthService.java` file to the redirect URI you
specified in your Cloud project (for example, `https://localhost:5000/callback`).
4. Updated `AuthController.java` `/callback` endpoint to match your redirect URI.

III. Set up the `step_03_query_parameters` walkthrough by ensuring that you have:
1. Updated `application.properties` with the path to the keystore file. Ensure that you add the path to the keystore in the
top level directory's `.gitignore` to make sure it is not accidentally pushed to a remote repository.
2. Put the `client_secret.json` file in the `src/main/resources/` directory so it can be read in the `AuthService.java` file.
_Ensure that the path to the client secret file is in the top level directory's `.gitignore` so that you do not accidentally
push your client secrets to a remote repository._
3. Updated the `REDIRECT_URI` variable in the `AuthService.java` file to the redirect URI you
specified in your Cloud project OAuth client credentials (for example, `https://localhost:5000/callback`).
4. Updated `AuthController.java` `/callback` endpoint to match your redirect URI.
5. Updated `application.properties` with values for `spring.datasource.username` and `spring.datasource.password`.
Make sure to update these fields before you run the application. This application uses an H2 database
that is generated when the application is run.  If you change the values for username and password in `application.properties`
and stop and re-run the app, an error will be thrown. To set new values for the username and password, delete the
folder that is generated at the path specified in `spring.datasource.url` and re-run the app with the new username
and password values set.

To run a specific module, run the following command on Unix:

   ```posix-terminal
   ./mvnw spring-boot:run -pl <module_name>
   ``` 

or on Windows:

   ```posix-terminal
   cd <module>
   mvnw.cmd spring-boot:run
   ```
This will launch the server at `https://localhost:5000` or at the port you specified in `application.properties`.
Note that if you stop and re-start the server, any attributes stored in the session will be cleared.

To load the application, either open the app in your browser or select the app in the **Add-ons** menu when creating
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
