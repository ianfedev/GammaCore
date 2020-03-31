package net.seocraft.commons.core.serializer.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        System.out.println(jsonParser.getText());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String date = jsonParser.getText();
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}