/*
 * Copyright 2022 The Error Prone Authors.
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

package com.google.errorprone.bugpatterns;

import static com.google.errorprone.BugPattern.SeverityLevel.SUGGESTION;

import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.Fix;
import com.google.errorprone.predicates.TypePredicate;
import com.google.errorprone.predicates.TypePredicates;
import com.google.errorprone.suppliers.Suppliers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author noamlewis@google.com (Noam Lewis)
 */
@BugPattern(
    summary = "Implicit toString() - did you mean some other String-valued method instead?",
    severity = SUGGESTION)
public class ImplicitToString extends AbstractToString {

  @Override
  protected TypePredicate typePredicate() {
    return TypePredicates.not(
        TypePredicates.anyOf(
            (type, state) -> type.isNumeric() || type.isPrimitive(),
            TypePredicates.isDescendantOf(Suppliers.typeFromClass(Enum.class)),
            TypePredicates.isExactType(Suppliers.typeFromClass(String.class)),
            TypePredicates.isExactType(Suppliers.typeFromClass(UUID.class)),
            TypePredicates.isExactType(Suppliers.typeFromClass(URI.class)),
            TypePredicates.isExactType(Suppliers.typeFromClass(Pattern.class)),
            TypePredicates.isExactType(Suppliers.typeFromClass(Instant.class))));
  }

  @Override
  protected Optional<Fix> implicitToStringFix(ExpressionTree tree, VisitorState state) {
    return Optional.empty();
  }

  @Override
  protected Optional<Fix> toStringFix(Tree parent, ExpressionTree expression, VisitorState state) {
    return Optional.empty();
  }

  @Override
  protected boolean allowableToStringKind(ToStringKind toStringKind) {
    switch (toStringKind) {
      case IMPLICIT:
        return false;
      case EXPLICIT:
      case FORMAT_METHOD:
      case FLOGGER:
      case NONE:
        return true;
    }
    throw new IllegalArgumentException(String.format("Invalid kind: %s", toStringKind));
  }

  @Override
  protected void handleStringifiedTree(
      Tree parent, ExpressionTree tree, ToStringKind toStringKind, VisitorState state) {
    // If the implicit toString() is in a 'throw ...' expression, ignore it
    // - it's most probably used to format the exception message.
    // TODO: Also allow calls inside an implemenation of toString(), and in calls to
    // Preconditions.checkArgument() and in exception super-constructors
    for (Tree ancestor : state.getPath()) {
      if (ancestor instanceof ThrowTree) {
        return;
      }
    }
    super.handleStringifiedTree(parent, tree, toStringKind, state);
  }
}
