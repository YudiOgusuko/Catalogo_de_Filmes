package br.Catalogo.de.Filmes.service;

import java.lang.reflect.Field;

public class VerificarCampos {

    public static boolean todosCamposNull(Object object) {
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
}
