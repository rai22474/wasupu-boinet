package io.wasupu.boinet.encoder;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import net.logstash.logback.decorate.JsonFactoryDecorator;

public class ISO8601DateDecorator implements JsonFactoryDecorator {

    @Override
    public MappingJsonFactory decorate(MappingJsonFactory factory) {
        var codec = factory.getCodec();
        codec.setDateFormat(new ISO8601DateFormat());
        return factory;
    }
}
