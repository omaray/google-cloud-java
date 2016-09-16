/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * EDITING INSTRUCTIONS
 * This file is referenced in Datastore's javadoc. Any change to this file should be reflected in
 * Datastore's javadoc.
 */

package com.google.cloud.examples.datastore.snippets;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

/**
 * This class contains a number of snippets for the {@link Datastore} interface.
 */
public class DatastoreSnippets {

  private final Datastore datastore;

  public DatastoreSnippets(Datastore datastore) {
    this.datastore = datastore;
  }

  /**
   * Example of committing a transaction
   */
  // [TARGET commit()]
  public Key commitTransaction() {
    // [START commitTransaction]
    // create an entity
    KeyFactory keyFactory = datastore.newKeyFactory().kind("someKind");
    Key key = datastore.allocateId(keyFactory.newKey());
    Entity entity = Entity.builder(key).set("description", "calling commit()").build();

    // add the entity and commit
    Transaction txn = datastore.newTransaction();
    try {
      txn.put(entity);
      txn.commit();
    } catch (DatastoreException ex) {
      // handle exception
    }
    // [END commitTransaction]
    return key;
  }
}