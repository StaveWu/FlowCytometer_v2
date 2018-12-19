package application.channel.Converter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class IntegerPropertyConverter implements AttributeConverter<IntegerProperty, Integer> {

    @Override
    public Integer convertToDatabaseColumn(IntegerProperty attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.get();
    }

    @Override
    public IntegerProperty convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return new SimpleIntegerProperty();
        }
        return new SimpleIntegerProperty(dbData);
    }
}
