// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy of
// the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations under
// the License.
package com.example.step_03_query_parameters;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** Handles the implementation logic of requests to the application server. */
@Service
public class AuthService {
  private static final String REDIRECT_URI = "https://localhost:5000/callback";
  private static final String CLIENT_SECRET_FILE = "client_secret.json";
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final File dataDirectory = new File("credentialStore");

  private static final String[] REQUIRED_SCOPES = {
      "https://www.googleapis.com/auth/userinfo.profile",
      "https://www.googleapis.com/auth/userinfo.email",
      "https://www.googleapis.com/auth/classroom.addons.teacher",
      "https://www.googleapis.com/auth/classroom.addons.student"
  };

  /** Creates and returns a Collection object with all requested scopes.
   * @return Collection of scopes requested by the application.
   */
  public static Collection<String> getScopes() {
    return new ArrayList<>(Arrays.asList(REQUIRED_SCOPES));
  }

  /** Reads the client secret file downloaded from GCP.
   * @return GoogleClientSecrets read in from client secret file.
   * @throws Exception if loading client secrets is unsuccessful. */
  public GoogleClientSecrets getClientSecrets() throws Exception {
    try {
      InputStream in = QueryParametersApplication.class.getClassLoader()
          .getResourceAsStream(CLIENT_SECRET_FILE);
      if (in == null) {
        throw new FileNotFoundException("Client secret file not found: " + CLIENT_SECRET_FILE);
      }
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
          new InputStreamReader(in));
      return clientSecrets;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Creates and returns FileDataStoreFactory object to store credentials.
   * @return FileDataStoreFactory dataStore used to save and obtain users ids mapped to Credentials.
   * @throws IOException if creating the dataStore is unsuccessful.
   */
  public FileDataStoreFactory getCredentialDataStore() throws IOException {
    FileDataStoreFactory dataStore = new FileDataStoreFactory(dataDirectory);
    return dataStore;
  }

  /** Builds and returns authorization code flow.
   * @return GoogleAuthorizationCodeFlow object used to retrieve an access token and refresh token
   * for the application.
   * @throws Exception if reading client secrets or building code flow object is unsuccessful.
   */
  public GoogleAuthorizationCodeFlow getFlow() throws Exception {
    try {
      GoogleAuthorizationCodeFlow authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
          HTTP_TRANSPORT,
          JSON_FACTORY,
          getClientSecrets(),
          getScopes())
          .setAccessType("offline")
          .setDataStoreFactory(getCredentialDataStore())
          .build();
      return authorizationCodeFlow;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Builds and returns a map with the authorization URL, which allows the user to give the app
   * permission to their account, and the state parameter, which is used to prevent cross site
   * request forgery.
   * @param login_hint query parameter provided by Classroom to facilitate repeated sign-in.
   * @return map with the authorization URL and state parameter.
   * @throws Exception if building the authorization URL is unsuccessful.
   */
  public HashMap authorize(String login_hint) throws Exception {
    HashMap<String, String> authDataMap = new HashMap<>();
    try {
      String state = new BigInteger(130, new SecureRandom()).toString(32);
      authDataMap.put("state", state);

      GoogleAuthorizationCodeFlow flow = getFlow();
      String authUrl = flow
          .newAuthorizationUrl()
          .setState(state)
          .set("login_hint", login_hint)
          .setRedirectUri(REDIRECT_URI)
          .build();
      String url = authUrl;
      authDataMap.put("url", url);

      return authDataMap;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Returns the required credentials to access Google APIs and persists the credentials to the
   * FileDataStoreFactory set up for the application.
   * @param authorizationCode the authorization code provided by the authorization URL that is used
   * to obtain credentials.
   * @return the credentials that were retrieved from the authorization flow.
   * @throws Exception if retrieving credentials is unsuccessful.
   */
  public Credential getAndSaveCredentials(String authorizationCode) throws Exception {
    try {
      GoogleAuthorizationCodeFlow flow = getFlow();
      GoogleClientSecrets googleClientSecrets = getClientSecrets();
      TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
          .setClientAuthentication(new ClientParametersAuthentication(
              googleClientSecrets.getWeb().getClientId(),
              googleClientSecrets.getWeb().getClientSecret()))
          .setRedirectUri(REDIRECT_URI).execute();

      // Obtaining the id_token will help determine which user signed in to the application.
      String idTokenString = tokenResponse.get("id_token").toString();

      // Validate the id_token using the GoogleIdTokenVerifier object.
      GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY)
          .setAudience(Collections.singletonList(googleClientSecrets.getWeb().getClientId()))
          .build();
      GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);

      if (idToken == null) {
        throw new Exception("Invalid ID token.");
      }

      // Obtain the user id from the id_token.
      Payload payload = idToken.getPayload();
      String userId = payload.getSubject();

      // Save the user id and credentials to the configured FileDataStoreFactory.
      Credential credential = flow.createAndStoreCredential(tokenResponse, userId);

      return credential;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Find credentials in the data store based on a specific user id.
   * @param userId key to find in the file data store.
   * @return Credential object to be returned if a matching key is found in the data store. Null if
   * the key does not exist.
   * @throws Exception if building flow object or checking for userId key is unsuccessful. */
  public Credential loadFromCredentialDataStore(String userId) throws Exception {
    try {
      GoogleAuthorizationCodeFlow flow = getFlow();
      Credential credential = flow.loadCredential(userId);
      return credential;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Obtains the Userinfo object by passing in the required credentials.
   * @param credentials retrieved from the authorization flow.
   * @return the Userinfo object for the currently signed-in user.
   * @throws Exception if creating UserInfo service or obtaining the Userinfo object is
   * unsuccessful.
   */
  public Userinfo getUserInfo(Credential credentials) throws Exception {
    try {
      Oauth2 userInfoService = new Oauth2.Builder(new NetHttpTransport(), new GsonFactory(),
          credentials).build();
      Userinfo userinfo = userInfoService.userinfo().get().execute();
      return userinfo;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Revokes the app's permissions to the user's account.
   * @param credentials retrieved from the authorization flow.
   * @return response entity returned from the HTTP call to obtain response information.
   * @throws RestClientException if the POST request to the revoke endpoint is unsuccessful.
   */
  public ResponseEntity<String> revokeCredentials(Credential credentials) throws RestClientException {
    try {
      String accessToken = credentials.getAccessToken();
      String url = "https://oauth2.googleapis.com/revoke?token=" + accessToken;

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
      HttpEntity<Object> httpEntity = new HttpEntity<Object>(httpHeaders);
      ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.POST,
          httpEntity, String.class);

      return responseEntity;
    } catch (RestClientException e) {
      e.printStackTrace();
      throw e;
    }
  }

}
