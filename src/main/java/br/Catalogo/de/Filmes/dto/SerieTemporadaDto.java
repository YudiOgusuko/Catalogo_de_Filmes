package br.Catalogo.de.Filmes.dto;

import br.Catalogo.de.Filmes.dto.seriesData.SerieTemporadas;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;

import java.util.List;

@Builder
@JsonPropertyOrder({
        "temporada",
        "anoTemporada",
        "episódios"
})
public record SerieTemporadaDto(Integer temporada,
                                Integer anoTemporada,
                                List<SerieEpisodiosTemporadaDto> episodios) {

    public SerieTemporadaDto(SerieTemporadas serieTemporadas) {
        this(serieTemporadas.temporada(),
              extrairAno(serieTemporadas),
              serieTemporadas.serieEpisodioPorTemporadas().stream().map(SerieEpisodiosTemporadaDto::new).toList());
    }

    private static Integer extrairAno(SerieTemporadas serieTemporadas) {
        if (serieTemporadas.serieEpisodioPorTemporadas() == null || serieTemporadas.serieEpisodioPorTemporadas().isEmpty()) {
            return null;
        }

        String ano = serieTemporadas.serieEpisodioPorTemporadas().get(0).dataLancamento();

        if (ano == null || ano.isBlank() || ano.equalsIgnoreCase("N/A")) {
            return null;
        }
        return Integer.parseInt(ano.trim().substring(0, 4));
    }

}
