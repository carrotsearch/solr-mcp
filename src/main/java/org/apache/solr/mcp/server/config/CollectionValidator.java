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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Validates collection access against a configurable allowlist.
 *
 * <p>
 * When the {@code solr.collections} property is set to a comma-separated list
 * of collection names, only those collections will be exposed through MCP. When
 * the property is empty or not set, all collections are allowed.
 */
@Component
public class CollectionValidator {

	private final Set<String> allowedCollections;

	public CollectionValidator(SolrConfigurationProperties properties) {
		if (properties.collections() != null && !properties.collections().isBlank()) {
			this.allowedCollections = Arrays.stream(properties.collections().split(",")).map(String::trim)
					.filter(s -> !s.isEmpty()).collect(Collectors.toUnmodifiableSet());
		} else {
			this.allowedCollections = Collections.emptySet();
		}
	}

	/**
	 * Returns true if all collections are allowed (no allowlist configured).
	 */
	public boolean isAllCollectionsAllowed() {
		return allowedCollections.isEmpty();
	}

	/**
	 * Returns true if the given collection is allowed.
	 */
	public boolean isAllowed(String collection) {
		return allowedCollections.isEmpty() || allowedCollections.contains(collection);
	}

	/**
	 * Filters a list of collections to only include allowed ones.
	 */
	public List<String> filterAllowed(List<String> collections) {
		if (allowedCollections.isEmpty()) {
			return collections;
		}
		return collections.stream().filter(allowedCollections::contains).collect(Collectors.toList());
	}

	/**
	 * Throws IllegalArgumentException if the collection is not allowed.
	 */
	public void assertAllowed(String collection) {
		if (!isAllowed(collection)) {
			throw new IllegalArgumentException("Collection not allowed: " + collection);
		}
	}

}
