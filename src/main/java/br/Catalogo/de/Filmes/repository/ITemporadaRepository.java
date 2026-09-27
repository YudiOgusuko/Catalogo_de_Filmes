package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.model.Episodio;
import br.Catalogo.de.Filmes.model.Temporadas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITemporadaRepository extends JpaRepository<Temporadas, Long> {
}
