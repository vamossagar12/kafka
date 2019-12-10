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
package org.apache.kafka.streams.state.internals;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.rocksdb.RocksIterator;

import java.nio.ByteBuffer;
import java.util.Set;

class RocksDBPrefixIterator extends RocksDbIterator {
    private byte[] rawPrefix;

    RocksDBPrefixIterator(final String name,
                          final RocksIterator newIterator,
                          final Set<KeyValueIterator<Bytes, byte[]>> openIterators,
                          final Bytes prefix) {
        super(name, newIterator, openIterators);
        this.rawPrefix = prefix.get();
        newIterator.seek(rawPrefix);
    }

    private boolean prefixEquals(final byte[] x, final byte[] y) {
        final int min = Math.min(x.length, y.length);
        final ByteBuffer xSlice = ByteBuffer.wrap(x, 0, min);
        final ByteBuffer ySlice = ByteBuffer.wrap(y, 0, min);
        return xSlice.equals(ySlice);
    }

    @Override
    public KeyValue<Bytes, byte[]> makeNext() {
        final KeyValue<Bytes, byte[]> next = super.makeNext();
        if (next == null) return allDone();
        else {
            if (prefixEquals(this.rawPrefix, next.key.get())) return next;
            else return allDone();
        }
    }
}