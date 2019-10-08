package org.kstreamscookbook.streams.grouped;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.jupiter.api.Test;
import org.kstreamscookbook.TopologyBuilder;
import org.kstreamscookbook.TopologyTestBase;

class CountByKeyTopologyTest extends TopologyTestBase {

    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    private StringSerializer stringSerializer = new StringSerializer();
    private StringDeserializer stringDeserializer = new StringDeserializer();
    private LongDeserializer longDeserializer = new LongDeserializer();

    @Override
    protected TopologyBuilder withTopologyBuilder() {
        return new CountByKeyTopology(INPUT_TOPIC, OUTPUT_TOPIC);
    }

    @Test
    public void testCounts() {
        ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(stringSerializer, stringSerializer);

        testDriver.pipeInput(factory.create(INPUT_TOPIC, "a", "one"));
        testDriver.pipeInput(factory.create(INPUT_TOPIC, "b", "test"));
        testDriver.pipeInput(factory.create(INPUT_TOPIC, "b", "test"));
        testDriver.pipeInput(factory.create(INPUT_TOPIC, "a", "test"));
        testDriver.pipeInput(factory.create(INPUT_TOPIC, "a", "test"));
        testDriver.pipeInput(factory.create(INPUT_TOPIC, "b", "test"));


        OutputVerifier.compareKeyValue(readNextRecord(), "a", 1l);
        OutputVerifier.compareKeyValue(readNextRecord(), "b", 1l);
        OutputVerifier.compareKeyValue(readNextRecord(), "b", 2l);
        OutputVerifier.compareKeyValue(readNextRecord(), "a", 2l);
        OutputVerifier.compareKeyValue(readNextRecord(), "a", 3l);
        OutputVerifier.compareKeyValue(readNextRecord(), "b", 3l);
    }

    private ProducerRecord<String, Long> readNextRecord() {
        return testDriver.readOutput(OUTPUT_TOPIC, stringDeserializer, longDeserializer);
    }
}