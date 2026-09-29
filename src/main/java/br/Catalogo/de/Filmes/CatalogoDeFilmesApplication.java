package br.Catalogo.de.Filmes;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogoDeFilmesApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(d ->
				System.setProperty(d.getKey(), d.getValue()));
		SpringApplication.run(CatalogoDeFilmesApplication.class, args);
	}
}
