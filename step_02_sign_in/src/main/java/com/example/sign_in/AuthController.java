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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.oauth2.model.Userinfo;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles requests to the application server. */
@org.springframework.stereotype.Controller
public class AuthController {
  /** Create an AuthService object by calling the class constructor.*/
  AuthService authService = new AuthService();

  /** Returns the index page that will be displayed when the add-on opens in a new tab.
   * @param model the Model interface to pass error information that is displayed on the error page.
   * @return the index page template if successful, or the onError function to handle and display
   * the error message.
   */
  @GetMapping(value = {"/"})
  public String index(Model model) {
    try {
      return "index";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Returns the add-on discovery page that will be displayed when the iframe is first
   * opened in Classroom.
   * @param session the current session.
   * @param model the Model interface to pass error information that is displayed on the error page.
   * @return the authorization page if the session does not exist or the credentials attribute is
   * not present in the session, the addon-discovery page if credentials are present in the session,
   * or the onError function to handle and display the error message.
   */
  @GetMapping(value = {"/addon-discovery"})
  public String addon_discovery(HttpSession session, Model model) {
    try {
      if (session == null || session.getAttribute("credentials") == null) {
        return "authorization";
      }
      return "addon-discovery";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Redirects the sign-in pop-up to the authorization url.
   * @param response the current response to pass information to.
   * @param session the current session.
   * @throws IOException if redirection to the authorization url is unsuccessful.
   */
  @GetMapping(value = {"/authorize"})
  public void authorize(HttpServletResponse response, HttpSession session) throws Exception {
    try {
      HashMap authDataMap = authService.authorize();
      String authUrl = authDataMap.get("url").toString();
      String state = authDataMap.get("state").toString();
      session.setAttribute("state", state);
      response.sendRedirect(authUrl);
    } catch (Exception e) {
      throw e;
    }
  }

  /** Handles the redirect URL to grant the application access to the user's account.
   * @param request the current request used to obtain the authorization code and state parameter
   * from.
   * @param session the current session.
   * @param model the Model interface to pass error information that is displayed on the error page.
   * @return the close-pop-up template if authorization is successful, or the onError function to
   * handle and display the error message.
   */
  @GetMapping(value = {"/callback"})
  public String callback(HttpServletRequest request, HttpServletResponse response,
      HttpSession session, Model model) {
    try {
      String authCode = request.getParameter("code");
      String requestState = request.getParameter("state");

      String sessionState = session.getAttribute("state").toString();
      if (!requestState.equals(sessionState)) {
        response.setStatus(401);
        return onError("Invalid state parameter.", model);
      }

      Credential credentials = authService.getCredentials(authCode);
      session.setAttribute("credentials", credentials);
      return "close-pop-up";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Returns the test request page with the user's email.
   * @param session the current session.
   * @param model the Model interface to pass error information that is displayed on the error page.
   * @return the test page that displays the current user's email or the onError function to handle
   * and display the error message.
   */
  @GetMapping(value = {"/test"})
  public String test(HttpSession session, Model model) {
    try {
      Credential credentials = (Credential) session.getAttribute("credentials");
      Userinfo userInfo = authService.getUserInfo(credentials);
      String userInfoEmail = userInfo.getEmail();
      if (userInfoEmail != null) {
        model.addAttribute("userEmail", userInfoEmail);
      } else {
        return onError("Could not get user email.", model);
      }
      return "test";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Clears the credentials in the session and returns the authorization page.
   * @param session the current session.
   * @return the sign-out confirmation page.
   */
  @GetMapping(value = {"/clear"})
  public String clear(HttpSession session, Model model) {
    try {
      if (session != null && session.getAttribute("credentials") != null) {
        session.removeAttribute("credentials");
      }
      return "sign-out";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Revokes the app's permissions and returns the authorization page.
   * @param session the current session.
   * @return the authorization page.
   */
  @GetMapping(value = {"/revoke"})
  public String revoke(HttpSession session, Model model) {
    try {
      if (session != null && session.getAttribute("credentials") != null) {
        Credential credentials = (Credential) session.getAttribute("credentials");
        ResponseEntity responseEntity = authService.revokeCredentials(credentials);
        Integer httpStatusCode = responseEntity.getStatusCodeValue();

        if (httpStatusCode != 200) {
          return onError("There was an issue revoking access: " +
              responseEntity.getStatusCode(), model);
        }
        session.removeAttribute("credentials");
      }
      return "authorization";
    } catch (Exception e) {
      return onError(e.getMessage(), model);
    }
  }

  /** Handles application errors.
   * @param errorMessage message to be displayed on the error page.
   * @param model the Model interface to pass error information to display on the error page.
   * @return the error page.
   */
  @GetMapping(value = {"/error"})
  public String onError(String errorMessage, Model model) {
    model.addAttribute("error", errorMessage);
    return "error";
  }

}
