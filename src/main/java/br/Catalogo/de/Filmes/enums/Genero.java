package br.Catalogo.de.Filmes.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Genero {

    DRAMA("Drama"),
    FANTASY("Fantasia"),
    HORROR("Terror"),
    ACTION("Ação"),
    ADVENTURE("Aventura"),
    COMEDY("Comédia"),
    CRIME("Crime"),
    THRILLER("Suspense"),
    ANIMATION("Animação"),
    BIOGRAPHY ("Biografia"),
    DOCUMENTARY("Documentário"),
    FAMILY ("Família"),
    FILMNOIR ("Filme Noir"),
    HISTORY ("História"),
    MUSIC ("Música"),
    MUSICAL ("Musical"),
    MYSTERY ("Mistério"),
    ROMANCE ("Romance"),
    SCIFI ("Ficção Científica"),
    SPORT ("Esporte"),
    WAR ("Guerra"),
    WESTERN ("Faroeste");

    private String generoEmPortugues;

    public static Genero pegarGenero(String genero) {
        if(genero == null || genero.isBlank()) {
            return null;
        }

        for(Genero g : Genero.values()) {
            if(genero.equalsIgnoreCase(g.name()) || genero.equalsIgnoreCase(g.generoEmPortugues)) {
                return g;
            }
        }
        return null;
    }

    public static String pegarStringGenero(String genero) {
        if(genero == null || genero.isBlank()) {
            return null;
        }

        for(Genero g : Genero.values()) {
            if(genero.equalsIgnoreCase(g.name())) {
                return g.generoEmPortugues;
            }
        }
        return null;
    }

    public static String pegarGeneroString(Genero genero) {
        if(genero == null) {
            return null;
        }

        for(Genero g : Genero.values()) {
            if(genero.equals(g)) {
                return g.generoEmPortugues;
            }
        }
        return null;
    }
}
