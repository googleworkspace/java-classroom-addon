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
package com.example.basic_app;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Handles all requests to the application server to display the correct webpage.  */
@org.springframework.stereotype.Controller
public class BasicAppController {

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
   * @param model the Model interface to pass error information that is displayed on the error page.
   * @return the addon-discovery page.
   */
  @GetMapping(value = {"/addon-discovery"})
  public String addon_discovery(Model model) {
    try {
      return "addon-discovery";
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
