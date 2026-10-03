// Endereço base do back-end Spring Boot (ajuste a porta se precisar)
const API = "http://localhost:8080/omdbapi.com";

const TIPOS = {
    filme: { base: "filmes", param: "filme" },
    serie: { base: "series", param: "serie" }
};

// chave do filtro -> [método do controller, nome do parâmetro]
const FILTROS_API = {
    anoMin: ["filtrarPorAnoMinimo", "ano"],
    anoMax: ["filtrarPorAnoMaximo", "ano"],
    avalMin: ["filtrarPorAvaliacaoMinima", "avaliacao"],
    avalMax: ["filtrarPorAvaliacaoMaxima", "avaliacao"],
    tempMin: ["filtrarPorTemporadaMinima", "temporada"],
    tempMax: ["filtrarPorTemporadaMaxima", "temporada"]
};

async function req(caminho, params = {}, metodo = "GET") {
    const url = new URL(`${API}/${caminho}`);
    Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));

    let res;
    try {
        res = await fetch(url, { method: metodo });
    } catch {
        throw new Error(`Não consegui falar com o servidor em ${API}. Ele está rodando e com CORS liberado?`);
    }

    const texto = await res.text();
    let dados = null;
    try { dados = texto ? JSON.parse(texto) : null; } catch { dados = texto; }

    if (!res.ok) {
        const msg = (dados && (dados.message || dados.mensagem || dados.error)) ||
            (typeof dados === "string" && dados) || `Erro ${res.status}`;
        throw new Error(msg);
    }
    return dados;
}

const Api = {
    todos: t => req(`${TIPOS[t].base}/all`),
    buscar: (t, nome) => req(`${TIPOS[t].base}/buscar`, { [TIPOS[t].param]: nome }),
    tituloIgual: (t, nome) => req(`${TIPOS[t].base}/tituloIgual`, { [TIPOS[t].param]: nome }),
    genero: (t, genero) => req(`${TIPOS[t].base}/buscarPorGenero`, { genero }),
    ator: (t, ator) => req(`${TIPOS[t].base}/buscarPorAtor`, { ator }),
    top5: t => req(`${TIPOS[t].base}/${t === "filme" ? "top5Filmes" : "top5Series"}`),
    filtro: (t, chave, valor) => {
        const [metodo, param] = FILTROS_API[chave];
        return req(`${TIPOS[t].base}/${metodo}`, { [param]: valor });
    },
    adicionar: (t, nome) => req(`${TIPOS[t].base}/add`, { [TIPOS[t].param]: nome }, "POST"),
    deletar: (t, nome) => req(`${TIPOS[t].base}/delete`, { [TIPOS[t].param]: nome }, "DELETE"),
    deletarTodos: t => req(`${TIPOS[t].base}/deleteAll`, {}, "DELETE"),
    top5Episodios: serie => req("series/top5Episodios", { serie }),
    temporada: (serie, temporada) => req("series/pegarTemporada", { serie, temporada }),
    episodio: (serie, temporada, episodio) => req("series/pegarEpisodio", { serie, temporada, episodio })
};
