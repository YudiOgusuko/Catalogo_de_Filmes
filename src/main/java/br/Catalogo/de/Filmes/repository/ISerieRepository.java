package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloIgnoreCase(String serie);

    boolean existsByTituloIgnoreCase(String tituloSerie);
}
