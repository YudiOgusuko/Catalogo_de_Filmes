package br.Catalogo.de.Filmes.service;

import br.Catalogo.de.Filmes.enums.Genero;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConverterGeneros implements AttributeConverter<Genero, String> {

    @Override
    public String convertToDatabaseColumn(Genero attribute) {
        if(attribute == null) {
            return null;
        }
        return attribute.getGeneroEmPortugues();
    }

    @Override
    public Genero convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        return Genero.pegarGenero(dbData);
    }
}
