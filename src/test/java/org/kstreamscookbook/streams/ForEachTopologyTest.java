package org.kstreamscookbook.streams;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.Test;
import org.kstreamscookbook.TopologyBuilder;
import org.kstreamscookbook.TopologyTestBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ForEachTopologyTest extends TopologyTestBase {

    private List<String> outputList = new ArrayList<>();

    @Override
    protected TopologyBuilder withTopologyBuilder() {
        return new ForEachTopology().withOutputList(outputList);
    }

    @Test
    void testFiltered() {
        IntegerSerializer integerSerializer = new IntegerSerializer();
        ConsumerRecordFactory<Integer, Integer> factory =
                new ConsumerRecordFactory<>(FilterTopology.INPUT_TOPIC, integerSerializer, integerSerializer);

        // send in some purchases
        // NOTE: we have to send a timestamp when sending Integers or Longs as both key and value to distinguish between
        // factory.create(K, V) and factory.create(V, timestampMs:long)
        // NOTE: Kafka has 3 notions of time - the test method below allows us to simulate Event time
        testDriver.pipeInput(factory.create(1001, 1000, new Date().getTime()));
        testDriver.pipeInput(factory.create(1001, 500, new Date().getTime()));
        testDriver.pipeInput(factory.create(1002, 1200, new Date().getTime()));

        assertTrue(outputList.contains("1001:1000"));
        assertFalse(outputList.contains("1001:500"));
        assertTrue(outputList.contains("1002:1200"));

    }

}