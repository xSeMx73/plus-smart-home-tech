package kafkaConfig;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AvroSerializer implements Serializer<SpecificRecordBase> {

    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public byte[] serialize(String s, SpecificRecordBase specificRecordBase) {
        if (specificRecordBase == null) {
            return null;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(out, null);
            DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(specificRecordBase.getSchema());
            writer.write(specificRecordBase, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Ошибка обработки данных топика [" + s + "]", e);
        }
    }
}