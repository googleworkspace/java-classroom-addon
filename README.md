Google Classroom add-ons Java Examples
========================================

This project hosts web applications that demonstrate the implmentation of a Google
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

1.  Create a [Google Cloud Platform (GCP) project](https://console.cloud.google.com/projectcreate).
Enable the following in the API Library:
    *   [Google Workspace Marketplace (GWM) SDK](https://console.cloud.google.com/apis/library/appsmarket-component.googleapis.com)
    *   [Google Classroom API](https://console.cloud.google.com/apis/library/classroom.googleapis.com)

    Visit the
    [developer site](https://developers.google.com/classroom/eap/add-ons-alpha/build-classroom-addon#step_3_google_workspace_marketplace_listing)
    for configuration instructions for the GWM SDK. You will also need to
    [install the add-on](https://developers.google.com/classroom/eap/add-ons-alpha/creating-simple-add-on#visit_the_unlisted_url_for_your_add-on_to_install_it)
    for it to be visible in Google Classroom.

1.  Visit your project's [Credentials](https://console.cloud.google.com/apis/credentials) page. Create two credentials in the project:
    *   An **API Key**. You can leave it as **Unrestricted** for the purposes of these examples.
    *   An **OAuth client ID**.
        *   The application type should be **Web application**.
        *   Add `<your server>/callback` as an **Authorized redirect URI**. For example,
        `https://localhost:5000/callback`

    Return to the Credentials page once both have been created, then:
      *   Copy your **API Key** and assign it to the environment variable `GOOGLE_API_KEY`:
          ```shell
          export GOOGLE_API_KEY=YOUR_COPIED_API_KEY
          ```
      *   Download the **OAuth2 client credentials** as JSON.

1.  Install [Java 11+](https://https://adoptopenjdk.net/) and ensure that `pip` is available:

    ```posix-terminal
    Java -m ensurepip --upgrade
    ```

1.  Clone this repository:

    ```posix-terminal
    git clone https://github.com/<org>/<repo>/
    ```

1.  Load the project in your IDE of choice.

1.  Launch the project by ???

1.  To load your app, either open the app in your browser or click the **Add-ons** button when creating an Assignment in [Google Classroom](https://classroom.google.com).

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

*   [ADD YOUR GITHUB ID HERE](https://github.com/YOUR_GITHUB_ID)
