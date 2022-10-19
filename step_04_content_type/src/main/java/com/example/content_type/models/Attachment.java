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
package com.example.content_type.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** An entity class that provides a model to store attachment information. */
@Entity
@Table(name = "attachments")
public class Attachment {

  /** The attachmentId is the unique identifier for the attachment. The @Id annotation specifies
   * that this is the primary key. */
  @Id
  @Column
  private String id;


  /** The image filename. */
  @Column
  private String image_filename;

  public Attachment() {}

  public Attachment(String id, String image_filename) {
    this.id = id;
    this.image_filename = image_filename;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public String getImage_filename() {
    return image_filename;
  }

  public void setImage_filename(String image_filename) {
    this.image_filename = image_filename;
  }
}
