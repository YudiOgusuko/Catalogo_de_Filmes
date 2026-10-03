package br.Catalogo.de.Filmes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogoDeFilmesApplication {

	public static void main(String[] args) {

		String apiKey = System.getenv("omdb.api.key");
		String apiUrl = System.getenv("omdb.api.url");
		String iaApiKey = System.getenv("GEMINI_API_KEY");

		SpringApplication.run(CatalogoDeFilmesApplication.class, args);
	}
}
