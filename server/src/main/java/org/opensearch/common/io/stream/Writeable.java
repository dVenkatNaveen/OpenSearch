/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.common.io.stream;

import org.opensearch.core.common.io.stream.BaseWriteable;

import java.io.IOException;

/**
 * Implementers can be written to a {@linkplain StreamOutput} and read from a {@linkplain StreamInput}. This allows them to be "thrown
 * across the wire" using OpenSearch's internal protocol. If the implementer also implements equals and hashCode then a copy made by
 * serializing and deserializing must be equal and have the same hashCode. It isn't required that such a copy be entirely unchanged.
 *
 * @opensearch.internal
 */
public interface Writeable extends BaseWriteable<StreamOutput> {

    /**
     * Write this into the {@linkplain StreamOutput}.
     */
    void writeTo(StreamOutput out) throws IOException;

    /**
     * Reference to a method that can write some object to a {@link StreamOutput}.
     * <p>
     * By convention this is a method from {@link StreamOutput} itself (e.g., {@link StreamOutput#writeString}). If the value can be
     * {@code null}, then the "optional" variant of methods should be used!
     * <p>
     * Most classes should implement {@link Writeable} and the {@link Writeable#writeTo(StreamOutput)} method should <em>use</em>
     * {@link StreamOutput} methods directly or this indirectly:
     * <pre><code>
     * public void writeTo(StreamOutput out) throws IOException {
     *     out.writeVInt(someValue);
     *     out.writeMapOfLists(someMap, StreamOutput::writeString, StreamOutput::writeString);
     * }
     * </code></pre>
     */
    @FunctionalInterface
    interface Writer<V> extends BaseWriteable.Writer<StreamOutput, V> {}

    /**
     * Reference to a method that can read some object from a stream. By convention this is a constructor that takes
     * {@linkplain StreamInput} as an argument for most classes and a static method for things like enums. Returning null from one of these
     * is always wrong - for that we use methods like {@link StreamInput#readOptionalWriteable(Reader)}.
     * <p>
     * As most classes will implement this via a constructor (or a static method in the case of enumerations), it's something that should
     * look like:
     * <pre><code>
     * public MyClass(final StreamInput in) throws IOException {
     *     this.someValue = in.readVInt();
     *     this.someMap = in.readMapOfLists(StreamInput::readString, StreamInput::readString);
     * }
     * </code></pre>
     */
    @FunctionalInterface
    interface Reader<V> extends BaseWriteable.Reader<StreamInput, V> {}

}
