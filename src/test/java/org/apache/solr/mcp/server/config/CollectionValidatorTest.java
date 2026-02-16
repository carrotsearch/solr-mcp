/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.mcp.server.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CollectionValidatorTest {

	@Test
	void nullCollections_AllowsEverything() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, null));

		assertTrue(validator.isAllCollectionsAllowed());
		assertTrue(validator.isAllowed("any_collection"));
		assertTrue(validator.isAllowed("another"));
	}

	@Test
	void emptyCollections_AllowsEverything() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, ""));

		assertTrue(validator.isAllCollectionsAllowed());
		assertTrue(validator.isAllowed("any_collection"));
	}

	@Test
	void blankCollections_AllowsEverything() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, "   "));

		assertTrue(validator.isAllCollectionsAllowed());
		assertTrue(validator.isAllowed("any_collection"));
	}

	@Test
	void singleCollection_OnlyAllowsThat() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, "core1"));

		assertFalse(validator.isAllCollectionsAllowed());
		assertTrue(validator.isAllowed("core1"));
		assertFalse(validator.isAllowed("core2"));
	}

	@Test
	void multipleCollections_AllowsOnlyListed() {
		CollectionValidator validator = new CollectionValidator(
				new SolrConfigurationProperties(null, "core1,core2,core3"));

		assertFalse(validator.isAllCollectionsAllowed());
		assertTrue(validator.isAllowed("core1"));
		assertTrue(validator.isAllowed("core2"));
		assertTrue(validator.isAllowed("core3"));
		assertFalse(validator.isAllowed("core4"));
	}

	@Test
	void collectionsWithSpaces_TrimsCorrectly() {
		CollectionValidator validator = new CollectionValidator(
				new SolrConfigurationProperties(null, " core1 , core2 , core3 "));

		assertTrue(validator.isAllowed("core1"));
		assertTrue(validator.isAllowed("core2"));
		assertTrue(validator.isAllowed("core3"));
		assertFalse(validator.isAllowed(" core1"));
	}

	@Test
	void filterAllowed_NoRestrictions() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, null));

		List<String> input = Arrays.asList("a", "b", "c");
		assertEquals(input, validator.filterAllowed(input));
	}

	@Test
	void filterAllowed_WithRestrictions() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, "a,c"));

		List<String> result = validator.filterAllowed(Arrays.asList("a", "b", "c", "d"));
		assertEquals(Arrays.asList("a", "c"), result);
	}

	@Test
	void assertAllowed_Passes() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, "core1"));

		assertDoesNotThrow(() -> validator.assertAllowed("core1"));
	}

	@Test
	void assertAllowed_ThrowsForDisallowed() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, "core1"));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> validator.assertAllowed("core2"));
		assertTrue(ex.getMessage().contains("core2"));
	}

	@Test
	void assertAllowed_NoRestrictions_AlwaysPasses() {
		CollectionValidator validator = new CollectionValidator(new SolrConfigurationProperties(null, null));

		assertDoesNotThrow(() -> validator.assertAllowed("anything"));
	}

}
