package ru.javawebinar.topjava.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        private static DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen,
                              SerializerProvider arg2)
                throws IOException, JsonProcessingException {

            gen.writeString(formatter.format(value));
        }
    }