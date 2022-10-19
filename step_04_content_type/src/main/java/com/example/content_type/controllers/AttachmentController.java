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
package com.example.content_type.controllers;

import com.example.content_type.models.Attachment;
import com.example.content_type.service.AttachmentService;
import com.example.content_type.service.AuthService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.model.AddOnAttachment;
import com.google.api.services.classroom.model.AddOnContext;
import com.google.api.services.classroom.model.EmbedUri;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.api.services.classroom.Classroom;

/** Handles Attachment related requests to the application server. */
@org.springframework.stereotype.Controller
public class AttachmentController {
  /** Declare AuthService to be used in the AttachmentService class constructor. */
  private final AuthService authService;

  /** Declare AttachmentService to be used in the AttachmentController class constructor. */
  private final AttachmentService attachmentService;

  /** AttachmentController constructor. Uses constructor injection to instantiate the
   * AttachmentService class.
   * @param attachmentService the service class that handles implementation logic of requests for
   * attachments.
   */
  public AttachmentController(AuthService authService, AttachmentService attachmentService) {
    this.authService = authService;
    this.attachmentService = attachmentService;
  }

  /** Displays a list of attachments the teacher can choose from. */
  @RequestMapping(value = {"/attachment-options"}, method = {RequestMethod.GET, RequestMethod.POST})
  public String getAttachmentOptions() {
    return "attachment-options";
  }

  /***/
  @RequestMapping(value = {"/create-attachment"}, method = {RequestMethod.POST})
  public String create_attachment(
      HttpSession session, HttpServletRequest request,
      @RequestParam(value = "angkor", required = false)String angkor,
      @RequestParam(value = "eiffel", required = false)String eiffel,
      @RequestParam(value = "himeji", required = false)String himeji,
      @RequestParam(value = "taj", required = false)String taj,
      Model model) throws Exception {
    try {
      // Reading the parameters passed from Classroom
      ArrayList<String> attachments = new ArrayList<>();
      ArrayList<AddOnAttachment> request_objects = new ArrayList<>();
      ArrayList<AddOnAttachment> response_objects = new ArrayList<>();

      if (angkor != null && angkor.equals("on")) {
        attachments.add("angkor-wat.jpg");
      }
      if (eiffel != null && eiffel.equals("on")) {
        attachments.add("eiffel-tower.jpeg");
      }
      if (himeji != null && himeji.equals("on")) {
        attachments.add("himeji-castle.jpeg");
      }
      if (taj != null && taj.equals("on")) {
        attachments.add("taj-mahal.jpeg");
      }

      if (attachments.size() == 0) {
        return onError("No attachments were selected.", model);
      }

      for (int i = 1; i <= attachments.size(); i++) {
        AddOnAttachment addOnAttachmentRequest = new AddOnAttachment();
        addOnAttachmentRequest.setTeacherViewUri(new EmbedUri().setUri(
            request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/load-content-attachment"));
        addOnAttachmentRequest.setStudentViewUri(new EmbedUri().setUri(
            request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() + "/load-content-attachment"));
        addOnAttachmentRequest.setTitle("Attachment " + i);
        request_objects.add(addOnAttachmentRequest);

        /** Setting up the Classroom service with the credentials. */
        Classroom classroom;
        Object credentials = session.getAttribute("credentials");
        if (credentials != null) {
          classroom = attachmentService.buildClassroomService((Credential) credentials);
        } else if (session.getAttribute("login_hint") != null) {
          credentials = authService.loadFromCredentialDataStore(
              session.getAttribute("login_hint").toString());
          classroom = attachmentService.buildClassroomService((Credential) credentials);
        } else {
          /** Return an error if credentials are not stored or detected in the session.*/
          return onError("Do not have the required credentials.", model);
        }

        // Create the addOnAttachment
        String courseId = session.getAttribute("courseId").toString();
        String itemId = session.getAttribute("itemId").toString();
        Object addOnToken = session.getAttribute("addOnToken");
        AddOnAttachment createAddOnResponse = attachmentService.createAddOnAttachment(
            classroom, courseId, itemId, addOnToken, addOnAttachmentRequest);
        response_objects.add(createAddOnResponse);

        String image_filename = attachments.get(i - 1);
        attachmentService.saveAttachmentToRepository(createAddOnResponse.getId(), image_filename);
      }

      model.addAttribute("numAttachments", attachments.size());
      model.addAttribute("requests", request_objects);
      model.addAttribute("responses", response_objects);

      return "create-attachment";
    } catch (GoogleJsonResponseException e) {
      if (e.getStatusCode() == 403) {
        if (e.getDetails().getMessage().startsWith("@InvalidAddOnToken")) {
          return onError("Please sign out of all accounts in your browser and try again.", model);
        } else if (e.getDetails().getMessage().startsWith("@ExpiredAddOnToken")) {
          return onError("Please sign in again.", model);
        }
      }
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  @RequestMapping(value = {"/load-content-attachment"}, method = {RequestMethod.GET})
  public String loadAttachment(HttpServletRequest request, HttpSession session, Model model) throws Exception {
    try {
      /** Reading the parameters passed from Classroom */
      if (request.getParameter("itemId") != null) {
        session.setAttribute("itemId", request.getParameter("itemId"));
      }
      if (request.getParameter("courseId") != null) {
        session.setAttribute("courseId", request.getParameter("courseId"));
      }
      if (request.getParameter("attachmentId") != null) {
        session.setAttribute("attachmentId", request.getParameter("attachmentId"));
      }
      if (request.getParameter("login_hint") != null) {
        session.setAttribute("login_hint", request.getParameter("login_hint"));
      }
      session.setAttribute("targetPage", "loadAttachmentPage");

      // Setting up the Classroom service with the credentials
      Classroom classroom;
      Object credentials = session.getAttribute("credentials");
      if (credentials != null) {
        classroom = attachmentService.buildClassroomService((Credential) credentials);
      } else if (session.getAttribute("login_hint") != null) {
        credentials = authService.loadFromCredentialDataStore(
            session.getAttribute("login_hint").toString());
        if (credentials == null) {
          return "authorization";
        }
        classroom = attachmentService.buildClassroomService((Credential) credentials);
      } else {
        /** Return an error if credentials are not stored or detected in the session.*/
        return onError("Do not have the required credentials.", model);
      }

      // Figuring out which view to display - teacher or student
      String courseId = session.getAttribute("courseId").toString();
      String itemId = session.getAttribute("itemId").toString();
      AddOnContext addOnContext = attachmentService.getAddOnContext(classroom, courseId, itemId);

      String attachmentId =  session.getAttribute("attachmentId").toString();
      Attachment attachment = attachmentService.getAttachmentFromRepository(attachmentId);
      String imageFilename = "images/" + attachment.getImage_filename();
      model.addAttribute("imageFilename", imageFilename);

      String userContext;
      String userMessage;
      if (addOnContext.get("studentContext") != null) {
        userContext = "student";
        userMessage = "Take a look at the following landmark image!";
      } else {
        userContext = "teacher";
        userMessage = "You attached the following landmark images to this assignment.";
      }
      model.addAttribute("userContext", userContext);
      model.addAttribute("userMessage", userMessage);

      return "show-content-attachment";
    } catch (GoogleJsonResponseException | TokenResponseException e) {
      if (e instanceof GoogleJsonResponseException) {
        if (e.getStatusCode() == 401) {
          return "authorization";
        } else if (e.getStatusCode() == 404) {
          return onError("The attachment you created cannot be found. Please make sure "
              + "you are signed in with the correct account and try again.", model);
        }
      } else if (e instanceof TokenResponseException && ((TokenResponseException) e)
          .getDetails().getError().equals("invalid_grant")) {
        return "authorization";
      }
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  /** Handles application errors.
   * @param errorMessage message to be displayed on the error page.
   * @param model the Model interface used to display information on the error page.
   * @return the error page.
   */
  public String onError(String errorMessage, Model model) {
    model.addAttribute("error", errorMessage);
    return "error";
  }
}
