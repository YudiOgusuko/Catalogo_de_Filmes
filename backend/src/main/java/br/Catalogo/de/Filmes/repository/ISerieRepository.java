package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.enums.Genero;
import br.Catalogo.de.Filmes.model.Episodio;
import br.Catalogo.de.Filmes.model.Serie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ISerieRepository extends JpaRepository<Serie, Long> {

    List<Serie> findByTituloContainingIgnoreCase(String serie);

    Optional<Serie> findByTituloIgnoreCase(String serie);

    boolean existsByTituloIgnoreCase(String tituloSerie);

    List<Serie> findAllByGenero(Genero genero);

    List<Serie> findAllByAtoresContainingIgnoreCase(String ator);

    @Query("SELECT s FROM Serie s WHERE s.avaliacao != 'N/A' ORDER BY CAST(s.avaliacao AS double) DESC LIMIT 5")
    List<Serie> findTop5();

    @Query("SELECT e FROM Serie s JOIN s.episodiosList e WHERE s.id = :id AND e.avaliacao != 'N/A' ORDER BY CAST(e.avaliacao AS double) DESC")
    List<Episodio> findTop5Episodios(Long id, Pageable pageable);

    List<Serie> findByTemporadasLessThanEqual(Integer temporada);

    List<Serie> findByTemporadasGreaterThanEqual(Integer temporada);

    @Query("SELECT s FROM Serie s WHERE s.avaliacao != 'N/A' AND CAST(s.avaliacao AS double) >= :avaliacao")
    List<Serie> findByAvaliacaoMinima(Double avaliacao);

    @Query("SELECT s FROM Serie s WHERE s.avaliacao != 'N/A' AND CAST(s.avaliacao AS double) <= :avaliacao")
    List<Serie> findByAvaliacaoMaxima(Double avaliacao);
}
