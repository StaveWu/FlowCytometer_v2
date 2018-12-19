package application.channel.Converter;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringPropertyConverter implements AttributeConverter<StringProperty, String> {
    @Override
    public String convertToDatabaseColumn(StringProperty attribute) {
        if (attribute == null) {
            return "";
        }
        return attribute.get();
    }

    @Override
    public StringProperty convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new SimpleStringProperty();
        }
        return new SimpleStringProperty(dbData);
    }
}
