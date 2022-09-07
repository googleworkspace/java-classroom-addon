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
package com.example.sign_in;

import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

  private static final String[] REQUIRED_SCOPES = {
      "https://www.googleapis.com/auth/userinfo.profile",
      "https://www.googleapis.com/auth/userinfo.email",
      "https://www.googleapis.com/auth/classroom.addons.teacher",
      "https://www.googleapis.com/auth/classroom.addons.student"
  };

  /** Creates and returns Collection object with all requested scopes.
   * @return Collection of scopes requested by the application.
   */
  public static Collection<String> getScopes() {
    return new ArrayList<>(Arrays.asList(REQUIRED_SCOPES));
  }

  /** Reads the client secret file downloaded from GCP.
   * @return GoogleClientSecrets read in from client secret file. */
  public GoogleClientSecrets getClientSecrets() throws Exception {
    try {
      InputStream in = SignInApplication.class.getClassLoader()
          .getResourceAsStream(CLIENT_SECRET_FILE);
      if (in == null) {
        throw new FileNotFoundException("Client secret file not found: " + CLIENT_SECRET_FILE);
      }
      GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
          new InputStreamReader(in));
      return clientSecrets;
    } catch (Exception e) {
      throw e;
    }
  }

  /** Builds and returns authorization code flow.
   * @return GoogleAuthorizationCodeFlow object used to retrieve an access token and refresh token
   * for the application.
   * @throws IOException if reading client secrets or building code flow object is unsuccessful.
   */
  public GoogleAuthorizationCodeFlow getFlow() throws Exception {
    try {
      GoogleAuthorizationCodeFlow authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
          HTTP_TRANSPORT,
          JSON_FACTORY,
          getClientSecrets(),
          getScopes())
          .setAccessType("offline")
          .build();
      return authorizationCodeFlow;
    } catch (Exception e) {
      throw e;
    }
  }

  /** Builds and returns a map with the authorization url, which allows the user to give the app
   * permission to their account, and the state parameter, which is used to prevent cross site
   * request forgery.
   * @return map with the authorization url and state parameter.
   * @throws IOException if building the authorization url is unsuccessful.
   */
  public HashMap authorize() throws Exception {
    HashMap<String, String> authDataMap = new HashMap<>();
    try {
      String state = new BigInteger(130, new SecureRandom()).toString(32);
      authDataMap.put("state", state);

      GoogleAuthorizationCodeFlow flow = getFlow();
      String authUrl = flow
          .newAuthorizationUrl()
          .setState(state)
          .setRedirectUri(REDIRECT_URI)
          .build();
      String url = authUrl;
      authDataMap.put("url", url);

      return authDataMap;
    } catch (Exception e) {
      throw e;
    }
  }

  /** Returns the required credentials to access Google APIs.
   * @param authorizationCode the authorization code provided by the authorization url that is used
   * to obtain credentials.
   * @return the credentials that were retrieved from the authorization flow.
   * @throws IOException if retrieving credentials is unsuccessful.
   */
  public Credential getCredentials(String authorizationCode) throws Exception {
    try {
      GoogleAuthorizationCodeFlow flow = getFlow();
      GoogleClientSecrets googleClientSecrets = getClientSecrets();
      TokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
          .setClientAuthentication(new ClientParametersAuthentication(
              googleClientSecrets.getWeb().getClientId(),
              googleClientSecrets.getWeb().getClientSecret()))
          .setRedirectUri(REDIRECT_URI).execute();

      Credential credential = flow.createAndStoreCredential(tokenResponse, null);
      return credential;
    } catch (Exception e) {
      throw e;
    }
  }

  /** Obtains the user's email by passing in the required credentials.
   * @param credentials retrieved from the authorization flow.
   * @return the currently signed-in user's email.
   * @throws IOException if creating UserInfo service or obtaining the currently signed-in user's
   * email is unsuccessful.
   */
  public Userinfo getUserInfo(Credential credentials) throws IOException {
    try {
      Oauth2 userInfoService = new Oauth2.Builder(new NetHttpTransport(), new GsonFactory(),
          credentials).build();
      Userinfo userinfo = userInfoService.userinfo().get().execute();
      return userinfo;
    } catch (Exception e) {
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
      throw e;
    }
  }

}
