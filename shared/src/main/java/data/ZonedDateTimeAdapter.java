package data;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для сериализации и десериализации {@link ZonedDateTime} в строку и обратно для JAXB.
 */
public class ZonedDateTimeAdapter extends XmlAdapter<String, ZonedDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public ZonedDateTime unmarshal(String v) throws Exception {
        return ZonedDateTime.parse(v, formatter);
    }

    @Override
    public String marshal(ZonedDateTime v) throws Exception {
        return formatter.format(v);
    }
}
