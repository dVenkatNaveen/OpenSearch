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
 *    http://www.apache.org/licenses/LICENSE-2.0
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

package org.opensearch.monitor.jvm;

import org.opensearch.common.unit.ByteSizeValue;
import org.opensearch.core.common.Strings;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class JvmStatsTests extends OpenSearchTestCase {
    public void testJvmStats() throws IOException {
        JvmStats stats = JvmStats.jvmStats();
        assertNotNull(stats);
        assertNotNull(stats.getUptime());
        assertThat(stats.getUptime().millis(), greaterThan(0L));
        assertThat(stats.getTimestamp(), greaterThan(0L));

        // Mem
        JvmStats.Mem mem = stats.getMem();
        assertNotNull(mem);
        for (ByteSizeValue heap : Arrays.asList(mem.getHeapCommitted(), mem.getHeapMax(), mem.getHeapUsed(), mem.getNonHeapCommitted())) {
            assertNotNull(heap);
            assertThat(heap.getBytes(), greaterThanOrEqualTo(0L));
        }
        assertNotNull(mem.getHeapUsedPercent());
        assertThat(mem.getHeapUsedPercent(), anyOf(equalTo((short) -1), greaterThanOrEqualTo((short) 0)));

        // Threads
        JvmStats.Threads threads = stats.getThreads();
        assertNotNull(threads);
        assertThat(threads.getCount(), greaterThanOrEqualTo(0));
        assertThat(threads.getPeakCount(), greaterThanOrEqualTo(0));

        // GC
        JvmStats.GarbageCollectors gcs = stats.getGc();
        assertNotNull(gcs);

        JvmStats.GarbageCollector[] collectors = gcs.getCollectors();
        assertNotNull(collectors);
        assertThat(collectors.length, greaterThan(0));
        for (JvmStats.GarbageCollector collector : collectors) {
            assertTrue(Strings.hasText(collector.getName()));
            assertNotNull(collector.getCollectionTime());
            assertThat(collector.getCollectionTime().millis(), anyOf(equalTo(-1L), greaterThanOrEqualTo(0L)));
            assertThat(collector.getCollectionCount(), anyOf(equalTo(-1L), greaterThanOrEqualTo(0L)));
        }

        // Buffer Pools
        List<JvmStats.BufferPool> bufferPools = stats.getBufferPools();
        if (bufferPools != null) {
            for (JvmStats.BufferPool bufferPool : bufferPools) {
                assertNotNull(bufferPool);
                assertTrue(Strings.hasText(bufferPool.getName()));
                assertThat(bufferPool.getCount(), greaterThanOrEqualTo(0L));
                assertNotNull(bufferPool.getTotalCapacity());
                assertThat(bufferPool.getTotalCapacity().getBytes(), greaterThanOrEqualTo(0L));
                assertNotNull(bufferPool.getUsed());
                assertThat(bufferPool.getUsed().getBytes(), anyOf(equalTo(-1L), greaterThanOrEqualTo(0L)));
            }
        }

        // Classes
        JvmStats.Classes classes = stats.getClasses();
        assertNotNull(classes);
        assertThat(classes.getLoadedClassCount(), greaterThanOrEqualTo(0L));
        assertThat(classes.getTotalLoadedClassCount(), greaterThanOrEqualTo(0L));
        assertThat(classes.getUnloadedClassCount(), greaterThanOrEqualTo(0L));
    }
}
