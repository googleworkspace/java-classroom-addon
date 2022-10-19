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
package com.example.content_type.service;

import com.example.content_type.models.Attachment;
import com.example.content_type.repository.AttachmentRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomRequestInitializer;
import com.google.api.services.classroom.model.AddOnAttachment;
import com.google.api.services.classroom.model.AddOnContext;
import java.io.IOException;
import org.springframework.stereotype.Service;

/** Handles Attachment related implementation logic of requests to the application server. */
@Service
public class AttachmentService {
  private static final String APPLICATION_NAME = "Google Classroom Add-ons Java Sample App";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
  private static final ClassroomRequestInitializer KEY_INITIALIZER = new ClassroomRequestInitializer(System.getenv("GOOGLE_API_KEY"));

  /** Declare AttachmentRepository to be used in the class constructor. */
  private final AttachmentRepository attachmentRepository;

  /** AttachmentService constructor. Uses constructor injection to instantiate the
   * AttachmentRepository class.
   * @param attachmentRepository the class that interacts with Attachment objects stored in
   * persistent storage.
   * @throws Exception if creating AttachmentService is unsuccessful.
   */
  public AttachmentService(AttachmentRepository attachmentRepository) throws Exception {
    this.attachmentRepository = attachmentRepository;
  }
  /**
   * @param credential the credentials to be passed into the Classroom Builder method.
   * @return Classroom an instance of Classroom service used to make calls to the API.
   */
  public Classroom buildClassroomService(Credential credential) {
    try {
      return new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
          .setClassroomRequestInitializer(KEY_INITIALIZER)
          .setApplicationName(APPLICATION_NAME)
          .build();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public AddOnAttachment createAddOnAttachment(Classroom classroom, String courseId,
       String itemId, Object addOnToken, AddOnAttachment addOnAttachment) throws IOException {
    return classroom.courses().courseWork().addOnAttachments()
        .create(
            courseId,
            itemId,
            addOnAttachment)
        .set("addOnToken", addOnToken)
        .execute();
  }

  public AddOnContext getAddOnContext(Classroom classroom, String courseId, String itemId)
      throws IOException {
    return classroom.courses().courseWork().getAddOnContext(
        courseId,
        itemId
    ).execute();
  }

  public Attachment getAttachmentFromRepository(String attachmentId) {
    return attachmentRepository.findById(attachmentId).get();
  }

  public Attachment saveAttachmentToRepository(String attachmentId, String image_filename) {
    return attachmentRepository.save(new Attachment(attachmentId, image_filename));
  }
}