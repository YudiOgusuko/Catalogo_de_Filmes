package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.enums.Genero;
import br.Catalogo.de.Filmes.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface IFilmeRepository extends JpaRepository<Filme, Long> {

    List<Filme> findByTituloContainingIgnoreCase(String filme);

    Optional<Filme> findByTituloIgnoreCase(String filme);

    boolean existsByTituloIgnoreCase(String tituloFilme);

    List<Filme> findAllByGenero(Genero genero);

    List<Filme> findAllByAtoresContainingIgnoreCase(String ator);

    @Query("SELECT f FROM Filme f WHERE f.avaliacao != 'N/A' ORDER BY CAST(f.avaliacao AS double) DESC LIMIT 5")
    List<Filme> findTop5();

    @Query("SELECT f FROM Filme f WHERE f.avaliacao != 'N/A' AND CAST(f.avaliacao AS double) <= :avaliacao")
    List<Filme> findByAvaliacaoMaxima(Double avaliacao);

    @Query("SELECT f FROM Filme f WHERE f.avaliacao != 'N/A' AND CAST(f.avaliacao AS double) >= :avaliacao")
    List<Filme> findByAvaliacaoMinima(Double avaliacao);
}
