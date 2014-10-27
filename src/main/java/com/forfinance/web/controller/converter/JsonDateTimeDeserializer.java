package com.forfinance.web.controller.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@JacksonStdImpl
public class JsonDateTimeDeserializer extends StdDeserializer<Date> {
    private static final long serialVersionUID = 7604875765905056364L;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public JsonDateTimeDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (str.length() == 0) {
                return getEmptyValue();
            }
            try {
                return dateFormat.parse(str);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Failed to parse Date value[" + str + "] (format: yyyyMMddHHmmss): " + e.getMessage());
            }
        }
        return super._parseDate(jp, ctxt);
    }

}
