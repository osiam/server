package org.osiam.ng.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 23.05.13
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class JsonDateSerializer extends JsonSerializer<Date> {

    private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(dateTimeFormatter.print(value.getTime()));
    }
}
