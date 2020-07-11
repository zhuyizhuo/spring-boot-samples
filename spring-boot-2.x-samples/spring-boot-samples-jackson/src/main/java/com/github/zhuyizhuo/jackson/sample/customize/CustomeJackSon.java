package com.github.zhuyizhuo.jackson.sample.customize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.zhuyizhuo.jackson.sample.util.HashIdUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * 加上 @JsonComponent 注解 则将会作为全局该类型的序列化反序列化处理器
 */
//@JsonComponent
public class CustomeJackSon {

    public static class Serialize extends JsonSerializer<Long> {

        @Override
        public void serialize(Long id, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(HashIdUtils.encode(id));
        }
    }

    public static class Deserializer extends JsonDeserializer<Long> {

        @Override
        public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            String id = jsonParser.getValueAsString();
            if (id != null){
                try {
                    return HashIdUtils.decode2Long(id);
                } catch (NumberFormatException e) {
                    throw new JsonParseException(jsonParser, id, e);
                }
            }
            return null;
        }
    }
}
