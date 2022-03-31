// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.firestore;

import static com.google.firebase.firestore.TestUtil.query;
import static com.google.firebase.firestore.testutil.TestUtil.andFilters;
import static com.google.firebase.firestore.testutil.TestUtil.filter;
import static com.google.firebase.firestore.testutil.TestUtil.orFilters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.firebase.firestore.core.CompositeFilter;
import com.google.firebase.firestore.core.FieldFilter;
import com.google.firebase.firestore.core.FieldFilter.Operator;
import com.google.firebase.firestore.core.Filter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
// There is already a QueryTest under integration-tests. So we name it QueryRoboTest here.
public class QueryRoboTest {

  @Test
  public void testEquals() {
    Query foo = query("foo");
    Query fooDup = query("foo");
    Query bar = query("bar");
    assertEquals(foo, fooDup);
    assertNotEquals(foo, bar);

    assertEquals(foo.hashCode(), fooDup.hashCode());
    assertNotEquals(foo.hashCode(), bar.hashCode());
  }

  @Test
  public void testFindFilterWithOperator() {
    CompositeFilter compositeFilter =
        andFilters(
            orFilters(filter("a", "==", "b"), filter("c", ">", "d")),
            orFilters(filter("a", "==", "b"), filter("c", "!=", "d")));
    FieldFilter fieldFilter = filter("r", "<", "s");
    List<Filter> filters = Arrays.asList(compositeFilter, fieldFilter);

    // Check that '==', '>', and '!=' (nested within the composite filter) and '<' (in the
    // field filter) are found.
    Query query1 = query("coll");
    Operator foundEqual =
        query1.findFilterWithOperator(filters, Collections.singletonList(Operator.EQUAL));
    Operator foundGreaterThan =
        query1.findFilterWithOperator(filters, Collections.singletonList(Operator.GREATER_THAN));
    Operator foundNotEqual =
        query1.findFilterWithOperator(filters, Collections.singletonList(Operator.NOT_EQUAL));
    Operator foundLessThan =
        query1.findFilterWithOperator(filters, Collections.singletonList(Operator.LESS_THAN));
    assertNotNull(foundEqual);
    assertNotNull(foundGreaterThan);
    assertNotNull(foundNotEqual);
    assertNotNull(foundLessThan);

    // Check that all other operators cannot be found.
    Operator foundOtherOperators =
        query1.findFilterWithOperator(
            filters,
            Arrays.asList(
                Operator.LESS_THAN_OR_EQUAL,
                Operator.GREATER_THAN_OR_EQUAL,
                Operator.ARRAY_CONTAINS,
                Operator.ARRAY_CONTAINS_ANY,
                Operator.IN,
                Operator.NOT_IN));
    assertNull(foundOtherOperators);
  }
}
