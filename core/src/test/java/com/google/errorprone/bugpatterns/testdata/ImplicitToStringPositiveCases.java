/*
 * Copyright 2012 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.bugpatterns.testdata;

import java.util.Optional;

/** @author noamlewis@google.com (Noam Lewis) */
public class ImplicitToStringPositiveCases {

  public void plus() {
    Optional<String> thingy = Optional.of("place");

    // BUG: Diagnostic contains: Implicit toString() - did you mean some other String-valued method instead?
    String result = "path/" + thingy;

    // BUG: Diagnostic contains: Implicit toString() - did you mean some other String-valued method instead?
    String result2 = String.format("path/%s", thingy);

  }



}
