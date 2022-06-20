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
public class ImplicitToStringNegativeCases {
  public void plus(Optional<String> thingy) {

    if (thingy.isPresent()) {
      String result = "path/" + thingy.get();
      String formatted = String.format("some %s", thingy.get());

    }
    throw new IllegalArgumentException(String.format("this is wrong: %s", thingy));
  }
}
