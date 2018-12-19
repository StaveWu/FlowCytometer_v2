package application.channel.Converter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DoublePropertyConverter implements AttributeConverter<DoubleProperty, Double> {
    @Override
    public Double convertToDatabaseColumn(DoubleProperty attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.get();
    }

    @Override
    public DoubleProperty convertToEntityAttribute(Double dbData) {
        if (dbData == null) {
            return new SimpleDoubleProperty();
        }
        return new SimpleDoubleProperty(dbData);
    }
}
