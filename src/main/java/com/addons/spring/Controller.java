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
package com.addons.spring;

import org.springframework.web.bind.annotation.GetMapping;

/** Handles all requests to the application server to display the correct webpage.  */
@org.springframework.stereotype.Controller
public class Controller {

  /** Returns the index page that will be displayed when the add-on opens in a new tab. */
  @GetMapping(value = {"/"})
  public String index() { return "index"; }

  /** Returns the add-on discovery page that will be displayed when the iframe is first
   * opened in Classroom.
   */
  @GetMapping(value = {"/addon-discovery"})
  public String addon_discovery() { return "addon-discovery"; }

}
