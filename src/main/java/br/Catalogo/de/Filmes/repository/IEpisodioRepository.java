package br.Catalogo.de.Filmes.repository;

import br.Catalogo.de.Filmes.model.Episodio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEpisodioRepository extends JpaRepository<Episodio, Long> {
}
