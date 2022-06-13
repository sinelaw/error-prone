/*
 * Copyright 2021 The Error Prone Authors.
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

import com.google.errorprone.annotations.MustBeClosed;
import java.util.stream.Stream;

class MustBeClosedCheckerPositiveCases {

  class DoesNotImplementAutoCloseable {
    @MustBeClosed
    // BUG: Diagnostic contains: MustBeClosed should only annotate constructors of AutoCloseables.
    DoesNotImplementAutoCloseable() {}

    @MustBeClosed
    // BUG: Diagnostic contains: MustBeClosed should only annotate methods that return an
    // AutoCloseable.
    void doesNotReturnAutoCloseable() {}
  }

  class Closeable implements AutoCloseable {

    @Override
    public void close() {}

    public int method() {
      return 1;
    }
  }

  class Foo {

    @MustBeClosed
    Closeable mustBeClosedAnnotatedMethod() {
      return new Closeable();
    }

    void sameClass() {
      // BUG: Diagnostic contains:
      mustBeClosedAnnotatedMethod();
    }
  }

  class MustBeClosedAnnotatedConstructor extends Closeable {

    @MustBeClosed
    MustBeClosedAnnotatedConstructor() {}

    void sameClass() {
      // BUG: Diagnostic contains:
      new MustBeClosedAnnotatedConstructor();
    }
  }

  interface Lambda {

    Closeable expression();
  }

  void positiveCase1() {
    // BUG: Diagnostic contains:
    new Foo().mustBeClosedAnnotatedMethod();
  }

  void positiveCase2() {
    // BUG: Diagnostic contains:
    Closeable closeable = new Foo().mustBeClosedAnnotatedMethod();
  }

  void positiveCase3() {
    try {
      // BUG: Diagnostic contains:
      new Foo().mustBeClosedAnnotatedMethod();
    } finally {
    }
  }

  void positiveCase4() {
    try (Closeable c = new Foo().mustBeClosedAnnotatedMethod()) {
      // BUG: Diagnostic contains:
      new Foo().mustBeClosedAnnotatedMethod();
    }
  }

  void positiveCase5() {
    // BUG: Diagnostic contains:
    new MustBeClosedAnnotatedConstructor();
  }

  Closeable positiveCase6() {
    // BUG: Diagnostic contains:
    return new MustBeClosedAnnotatedConstructor();
  }

  Closeable positiveCase7() {
    // BUG: Diagnostic contains:
    return new Foo().mustBeClosedAnnotatedMethod();
  }

  void positiveCase8() {
    // Lambda has a fixless finding because no reasonable fix can be suggested
    Lambda expression =
        () -> {
          // BUG: Diagnostic contains:
          return new Foo().mustBeClosedAnnotatedMethod();
        };
  }

  void positiveCase9() {
    // TODO(b/235827063): BUG: Diagnostic contains:
    Lambda expression = new Foo()::mustBeClosedAnnotatedMethod;
  }

  void positiveCase10() {
    new Foo() {
      @Override
      public Closeable mustBeClosedAnnotatedMethod() {
        // BUG: Diagnostic contains:
        return new MustBeClosedAnnotatedConstructor();
      }
    };
  }

  int expressionDeclaredVariable() {
    // BUG: Diagnostic contains:
    int result = new Foo().mustBeClosedAnnotatedMethod().method();
    return result;
  }

  void tryWithResources_nonFinal() {
    Foo foo = new Foo();
    // BUG: Diagnostic contains:
    Closeable closeable = foo.mustBeClosedAnnotatedMethod();
    try {
      closeable = null;
    } finally {
      closeable.close();
    }
  }

  void tryWithResources_noClose() {
    Foo foo = new Foo();
    // BUG: Diagnostic contains:
    Closeable closeable = foo.mustBeClosedAnnotatedMethod();
    try {
    } finally {
    }
  }

  class CloseableFoo implements AutoCloseable {

    @MustBeClosed
    CloseableFoo() {}

    // Doesn't autoclose Foo on Stream close.
    Stream<String> stream() {
      return null;
    }

    @Override
    public void close() {}
  }

  void twrStream() {
    // BUG: Diagnostic contains:
    try (Stream<String> stream = new CloseableFoo().stream()) {}
  }

  void constructorsTransitivelyRequiredAnnotation() {
    abstract class Parent implements AutoCloseable {
      @MustBeClosed
      Parent() {}

      // BUG: Diagnostic contains: Invoked constructor is marked @MustBeClosed
      Parent(int i) {
        this();
      }
    }

    // BUG: Diagnostic contains: Implicitly invoked constructor is marked @MustBeClosed
    abstract class ChildDefaultConstructor extends Parent {}

    abstract class ChildExplicitConstructor extends Parent {
      // BUG: Diagnostic contains: Invoked constructor is marked @MustBeClosed
      ChildExplicitConstructor() {}

      // BUG: Diagnostic contains: Invoked constructor is marked @MustBeClosed
      ChildExplicitConstructor(int a) {
        super();
      }
    }
  }
}
