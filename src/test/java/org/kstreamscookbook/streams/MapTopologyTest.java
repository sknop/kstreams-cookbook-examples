package org.kstreamscookbook.streams;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.jupiter.api.Test;
import org.kstreamscookbook.TopologyBuilder;
import org.kstreamscookbook.TopologyTestBase;

class MapTopologyTest extends TopologyTestBase {

    @Override
    protected TopologyBuilder withTopologyBuilder() {
        return new MapTopology();
    }

    @Test
    void testCopied() {
        StringSerializer stringSerializer = new StringSerializer();
        ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(stringSerializer, stringSerializer);
        testDriver.pipeInput(factory.create(MapTopology.INPUT_TOPIC, "key", "value"));

        StringDeserializer stringDeserializer = new StringDeserializer();
        ProducerRecord<String, String> producerRecord = testDriver.readOutput(MapTopology.OUTPUT_TOPIC, stringDeserializer, stringDeserializer);

        OutputVerifier.compareKeyValue(producerRecord, "k", "VALUE");
    }
}