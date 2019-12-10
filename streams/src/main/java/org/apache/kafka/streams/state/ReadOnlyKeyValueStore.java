/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.state;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.errors.InvalidStateStoreException;

/**
 * A key-value store that only supports read operations.
 * Implementations should be thread-safe as concurrent reads and writes are expected.
 *
 * Please note that this contract defines the thread-safe read functionality only; it does not
 * guarantee anything about whether the actual instance is writable by another thread, or
 * whether it uses some locking mechanism under the hood. For this reason, making dependencies
 * between the read and write operations on different StateStore instances can cause concurrency
 * problems like deadlock.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public interface ReadOnlyKeyValueStore<K, V> {

    /**
     * Get the value corresponding to this key.
     *
     * @param key The key to fetch
     * @return The value or null if no value is found.
     * @throws NullPointerException If null is used for key.
     * @throws InvalidStateStoreException if the store is not initialized
     */
    V get(K key);

    /**
     * Get an iterator over a given range of keys. This iterator must be closed after use.
     * The returned iterator must be safe from {@link java.util.ConcurrentModificationException}s
     * and must not return null values. No ordering guarantees are provided.
     * @param from The first key that could be in the range
     * @param to The last key that could be in the range
     * @return The iterator for this range.
     * @throws NullPointerException If null is used for from or to.
     * @throws InvalidStateStoreException if the store is not initialized
     */
    KeyValueIterator<K, V> range(K from, K to);

    /**
     * Return an iterator over all keys in this store. This iterator must be closed after use.
     * The returned iterator must be safe from {@link java.util.ConcurrentModificationException}s
     * and must not return null values. No ordering guarantees are provided.
     * @return An iterator of all key/value pairs in the store.
     * @throws InvalidStateStoreException if the store is not initialized
     */
    KeyValueIterator<K, V> all();

    /**
     * Return an approximate count of key-value mappings in this store.
     *
     * The count is not guaranteed to be exact in order to accommodate stores
     * where an exact count is expensive to calculate.
     *
     * @return an approximate count of key-value mappings in the store.
     * @throws InvalidStateStoreException if the store is not initialized
     */
    long approximateNumEntries();

    /**
     * Get an iterator over keys which have the specified prefix. The type of the prefix can be different from that of
     * the key. That's why, callers should also pass a serializer for the prefix to convert the prefix into the
     * format in which the keys are stored underneath in the stores
     * @param prefix The prefix.
     * @param prefixKeySerializer Serializer for the Prefix key type
     * @param <PS> Prefix Serializer type
     * @param <P> Prefix Type.
     * @return The iterator for keys having the specified prefix.
     */
    <PS extends Serializer<P>, P> KeyValueIterator<K, V> prefixScan(P prefix, PS prefixKeySerializer);
}
