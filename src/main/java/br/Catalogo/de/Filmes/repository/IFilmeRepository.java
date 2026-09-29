package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.model.Filme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IFilmeRepository extends JpaRepository<Filme, Long> {

    Optional<Filme> findByTituloIgnoreCase(String filme);

    boolean existsByTituloIgnoreCase(String tituloFilme);
}
