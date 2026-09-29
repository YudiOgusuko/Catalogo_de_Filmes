package br.Catalogo.de.Filmes.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Utilitarios {

    public static boolean verificarCamposNull(Object object) {
        if(object == null) {
            return true;
        }

        for(Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if(field.get(object) != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public static String formatarDataEpTemporada(String data) {
        if (data == null || data.isEmpty() || data.equalsIgnoreCase("N/A")) {
            return null;
        }

        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(data, formatoEntrada);

        DateTimeFormatter formatoSaida = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "BR"));

        return localDate.format(formatoSaida);
    }

    public static String formatarDataEpisodio(String data) {
        if (data == null || data.isEmpty() || data.equalsIgnoreCase("N/A")) {
            return null;
        }

        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("en", "US"));
        LocalDate localDate = LocalDate.parse(data, formatoEntrada);

        DateTimeFormatter formatoSaida = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "BR"));

        return localDate.format(formatoSaida);
    }
}
