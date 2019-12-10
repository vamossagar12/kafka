package org.apache.kafka.jmh.streams.processor.internals;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.metrics.Metrics;

import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.internals.*;
import org.apache.kafka.streams.processor.internals.metrics.StreamsMetricsImpl;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.internals.RocksDBStore;
import org.apache.kafka.streams.state.internals.ThreadCache;
import org.openjdk.jmh.annotations.*;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.common.utils.Utils.mkEntry;
import static org.apache.kafka.common.utils.Utils.mkMap;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RocksDbPrefixStateStoreBenchmark {

    private int counter;

    private static final int DISTINCT_KEYS = 10_000;

    private static final String KEY = "the_key_to_use";

    private static final String VALUE = "the quick brown fox jumped over the lazy dog the olympics are about to start";

    private final Bytes[] keys = new Bytes[DISTINCT_KEYS];

    private final byte[][] values = new byte[DISTINCT_KEYS][];

    private RocksDBStore prefixedStateStore;

    @Setup(Level.Trial)
    public void setUp() {
        for (int i = 0; i < DISTINCT_KEYS; ++i) {
            keys[i] = Bytes.wrap((KEY + i).getBytes(StandardCharsets.UTF_8));
            values[i] = (VALUE + i).getBytes(StandardCharsets.UTF_8);
        }

        prefixedStateStore = (RocksDBStore)Stores.persistentKeyValueStore("prefix-state-store").get();

        final StreamsMetricsImpl metrics = new StreamsMetricsImpl(new Metrics(), "prefix-test-metrics", StreamsConfig.METRICS_LATEST);
        final ThreadCache cache = new ThreadCache(new LogContext("prefixTestCache "), 1_000_000_000, metrics);
        final StreamsConfig config = new StreamsConfig(mkMap(
                mkEntry(StreamsConfig.APPLICATION_ID_CONFIG, "prefix-test"),
                mkEntry(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "prefix-test")
        ));

        final TaskId id = new TaskId(0, 0);
        final ProcessorStateManager stateMgr;
        stateMgr = new ProcessorStateManager(
                id,
                Task.TaskType.ACTIVE,
                false,
                new LogContext("jmh"),
                new StateDirectory(config, Time.SYSTEM, true),
                null,
                Collections.emptyMap(),
                Collections.emptyList()
        );

        final ProcessorContextImpl context = new ProcessorContextImpl(
                id,
                null,
                config,
                null,
                stateMgr,
                metrics,
                cache
        );

        context.setRecordContext(new ProcessorRecordContext(0, 0, 0, "topic", new RecordHeaders()));
        prefixedStateStore.init(context, prefixedStateStore);

    }

    @Benchmark
    public KeyValueIterator<Bytes, byte[]> testCachePerformance() {
        counter++;
        final int index = counter % DISTINCT_KEYS;
        // Put multiple keys with the same prefix patterns. This way, the prefix seek use case would be catered.
        final Bytes key = keys[index];
        prefixedStateStore.put(key, values[index]);
        int numIterations = 10;
        int nextIndex = index + 1;
        while (numIterations >= 0) {
            Bytes newKey = keys[nextIndex % DISTINCT_KEYS];
            prefixedStateStore.put(newKey, values[nextIndex % DISTINCT_KEYS]);
            numIterations--;
            nextIndex++;
        }

        return prefixedStateStore.prefixScan(Bytes.wrap(KEY.getBytes()));
    }

}
