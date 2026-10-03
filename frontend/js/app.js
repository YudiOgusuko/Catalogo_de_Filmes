const GENEROS = [
    ["DRAMA", "Drama"], ["FANTASY", "Fantasia"], ["HORROR", "Terror"], ["ACTION", "Ação"],
    ["ADVENTURE", "Aventura"], ["COMEDY", "Comédia"], ["CRIME", "Crime"], ["THRILLER", "Suspense"],
    ["ANIMATION", "Animação"], ["BIOGRAPHY", "Biografia"], ["DOCUMENTARY", "Documentário"],
    ["FAMILY", "Família"], ["FILMNOIR", "Filme Noir"], ["HISTORY", "História"], ["MUSIC", "Música"],
    ["MUSICAL", "Musical"], ["MYSTERY", "Mistério"], ["ROMANCE", "Romance"], ["SCIFI", "Ficção Científica"],
    ["SPORT", "Esporte"], ["WAR", "Guerra"], ["WESTERN", "Faroeste"]
];

const NOME = {
    filme: { plural: "Filmes", um: "filme", todos: "filmes" },
    serie: { plural: "Séries", um: "série", todos: "séries" }
};

const estado = { lista: { filme: [], serie: [] }, externo: { filme: false, serie: false }, ultimoTipo: "filme" };

const $ = (s, el = document) => el.querySelector(s);
const esc = s => String(s ?? "").replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
const vazioVal = v => v == null || v === "" || v === "N/A";
const val = v => vazioVal(v) ? "—" : v;
const chip = (v, prefixo = "") => vazioVal(v) ? "" : `<span class="chip">${esc(prefixo + v)}</span>`;
const linha = (rotulo, v) => `<div><dt>${rotulo}</dt><dd>${esc(val(v))}</dd></div>`;

/* ---------- avisos e carregamento ---------- */
function toast(msg, erro = false) {
    const el = document.createElement("div");
    el.className = "toast" + (erro ? " toast--erro" : "");
    el.textContent = msg;
    $("#toasts").append(el);
    setTimeout(() => el.remove(), erro ? 6000 : 3500);
}

function carregando(on) {
    const b = $("#barra");
    b.classList.toggle("ativa", on);
}

async function executar(fn) {
    carregando(true);
    try { await fn(); } catch (err) { toast(err.message, true); } finally { carregando(false); }
}

/* ---------- posters ---------- */
function semPoster(titulo) {
    return `<div class="sem-poster"><span>${esc(titulo)}</span></div>`;
}

function posterHtml(url, titulo) {
    if (vazioVal(url)) return semPoster(titulo);
    return `<img src="${esc(url)}" alt="${esc(titulo)}" loading="lazy" onerror="this.outerHTML=semPoster(this.alt)">`;
}

/* ---------- listagem ---------- */
const tituloDe = it => it.titulo || it.title || it.Title || "Sem título";

function card(it, i, tipo, externo) {
    const t = tituloDe(it);
    const ano = it.ano || it.year || it.Year || "";
    const nota = !externo && !vazioVal(it.avaliacao) ? `<span class="nota">★ ${esc(it.avaliacao)}</span>` : "";
    return `<article class="card${externo ? " card--ext" : ""}" data-i="${i}" data-tipo="${tipo}" tabindex="0" role="button" aria-label="${esc(t)}">
        <div class="card__poster">${posterHtml(it.poster || it.Poster, t)}</div>
        <div class="card__info">
            <h3>${esc(t)}</h3>
            <p><span>${esc(ano)}</span>${nota}</p>
            ${externo ? `<button class="btn btn--mini" data-add>Adicionar ao catálogo</button>` : ""}
        </div>
    </article>`;
}

function mostrarSecao(tipo, itens, rotulo, { externo = false, vazio = "Nenhum resultado encontrado." } = {}) {
    estado.lista[tipo] = itens;
    estado.externo[tipo] = externo;
    const sec = $(`#sec-${tipo}`);
    sec.hidden = false;
    $(".secao__titulo", sec).textContent = rotulo;
    $(".grade", sec).innerHTML = itens.length
        ? itens.map((it, i) => card(it, i, tipo, externo)).join("")
        : `<p class="vazio">${vazio}</p>`;
}

const esconderSecao = tipo => { $(`#sec-${tipo}`).hidden = true; };
const outro = tipo => tipo === "filme" ? "serie" : "filme";

const vazioCatalogo = tipo =>
    `Seu catálogo de ${NOME[tipo].todos} está vazio. Abra “Gerenciar catálogo” e adicione ${tipo === "filme" ? "o primeiro filme" : "a primeira série"}.`;

async function carregarTudo() {
    const tipos = ["filme", "serie"];
    const res = await Promise.allSettled(tipos.map(t => Api.todos(t)));
    let falhou = false;
    res.forEach((r, i) => {
        const t = tipos[i];
        if (r.status === "fulfilled") {
            mostrarSecao(t, r.value || [], NOME[t].plural, { vazio: vazioCatalogo(t) });
        } else {
            mostrarSecao(t, [], NOME[t].plural, { vazio: "Não foi possível carregar o catálogo." });
            if (!falhou) { toast(r.reason.message, true); falhou = true; }
        }
    });
}

async function verTodos(tipo) {
    const dados = await Api.todos(tipo);
    mostrarSecao(tipo, dados || [], `${NOME[tipo].plural} do catálogo`, { vazio: vazioCatalogo(tipo) });
    esconderSecao(outro(tipo));
}

/* ---------- busca ---------- */
function listaDe(d) {
    if (Array.isArray(d)) return d;
    if (!d) return [];
    const a = d.search || d.Search || d.resultados || d.conteudos;
    return Array.isArray(a) ? a : [d];
}

function mostrarEpisodios(eps, rotulo) {
    estado.lista.serie = [];
    estado.externo.serie = false;
    const sec = $("#sec-serie");
    sec.hidden = false;
    $(".secao__titulo", sec).textContent = rotulo;
    const lista = Array.isArray(eps) ? eps : [];
    $(".grade", sec).innerHTML = lista.length
        ? `<ol class="top-eps">${lista.map((e, i) => {
            const num = e.episodio ?? e.numero;
            return `<li>
                <span class="top-eps__pos">${i + 1}</span>
                <div class="top-eps__info">
                    <strong>${esc(e.titulo || e.title || "Sem título")}</strong>
                    <div class="chips">${chip(e.temporada, "Temporada ")}${chip(num, "Episódio ")}${chip(e.dataLancamento || e.data)}</div>
                </div>
                ${vazioVal(e.avaliacao) ? "" : `<span class="nota">★ ${esc(e.avaliacao)}</span>`}
            </li>`;
        }).join("")}</ol>`
        : `<p class="vazio">Nenhum episódio encontrado.</p>`;
}

async function buscar(tipo) {
    estado.ultimoTipo = tipo;
    marcarNav(null);
    const n = NOME[tipo];
    const texto = $("#texto").value.trim();
    const modo = $("#modo").value;
    const genero = $("#genero").value;
    const filtro = $("#filtro").value;
    const valor = $("#valor").value;

    let promessa, rotulo, externo = false, episodios = false;

    if (filtro) {
        if (filtro.startsWith("temp") && tipo === "filme") return toast("Filtro de temporadas só vale para séries.", true);
        if (filtro === "top5Eps") {
            if (tipo === "filme") return toast("O top 5 de episódios só vale para séries.", true);
            const nomeSerie = $("#nome-serie").value.trim();
            if (!nomeSerie) return toast("Informe o nome da série.", true);
            promessa = Api.top5Episodios(nomeSerie);
            rotulo = `Top 5 episódios de ${nomeSerie}`;
            episodios = true;
        } else if (filtro === "top5") {
            promessa = Api.top5(tipo);
            rotulo = `${n.plural}: top 5 melhores avaliados`;
        } else {
            if (valor === "") return toast("Informe um valor para o filtro.", true);
            promessa = Api.filtro(tipo, filtro, valor);
            rotulo = `${n.plural}: ${$("#filtro").selectedOptions[0].text.toLowerCase().replace(" (séries)", "")} ${valor}`;
        }
    } else if (texto) {
        if (modo === "tituloIgual") {
            promessa = Api.tituloIgual(tipo, texto);
            rotulo = `${n.plural} com o título “${texto}”`;
            externo = true;
        } else if (modo === "ator") {
            promessa = Api.ator(tipo, texto);
            rotulo = `${n.plural} com ${texto}`;
        } else {
            promessa = Api.buscar(tipo, texto);
            rotulo = `${n.plural}: resultado para “${texto}”`;
        }
    } else if (genero) {
        promessa = Api.genero(tipo, genero);
        rotulo = `${n.plural} de ${$("#genero").selectedOptions[0].text}`;
    } else {
        promessa = Api.todos(tipo);
        rotulo = `${n.plural} do catálogo`;
    }

    await executar(async () => {
        const dados = await promessa;
        if (episodios) { mostrarEpisodios(dados, rotulo); esconderSecao("filme"); return; }
        const itens = externo ? listaDe(dados) : (Array.isArray(dados) ? dados : dados ? [dados] : []);
        mostrarSecao(tipo, itens, rotulo, { externo, vazio: externo ? "Nenhum título encontrado." : "Nada no catálogo com essa busca." });
        esconderSecao(outro(tipo));
    });
}

/* ---------- gerenciar catálogo ---------- */
function pedirNome(titulo) {
    return new Promise(resolve => {
        const d = $("#dlg-nome"), input = $("#dlg-input");
        $("#dlg-titulo").textContent = titulo;
        input.value = "";
        d.returnValue = "";
        d.showModal();
        input.focus();
        d.addEventListener("close", () => resolve(d.returnValue === "ok" ? input.value.trim() : null), { once: true });
    });
}

async function adicionar(tipo, nome) {
    toast(`Adicionando “${nome}”. Isso pode levar alguns segundos.`);
    const novo = await Api.adicionar(tipo, nome);
    toast(`“${(novo && novo.titulo) || nome}” foi adicionado ao catálogo.`);
    await carregarTudo();
}

$(".gerenciar__painel").addEventListener("click", async e => {
    const b = e.target.closest("button[data-acao]");
    if (!b) return;
    $(".gerenciar").open = false;
    const { acao, tipo } = b.dataset;
    const n = NOME[tipo];

    if (acao === "todos") return executar(() => verTodos(tipo));

    if (acao === "deletarTodos") {
        if (!confirm(`Deletar todos os ${n.todos} do catálogo? Essa ação não pode ser desfeita.`)) return;
        return executar(async () => {
            await Api.deletarTodos(tipo);
            toast(`Todos os ${n.todos} foram deletados.`);
            await carregarTudo();
        });
    }

    const nome = await pedirNome(acao === "adicionar" ? `Adicionar ${n.um}` : `Deletar ${n.um}`);
    if (!nome) return;

    executar(async () => {
        if (acao === "adicionar") return adicionar(tipo, nome);
        await Api.deletar(tipo, nome);
        toast(`“${nome}” foi deletado.`);
        await carregarTudo();
    });
});

/* ---------- modal ---------- */
const modal = $("#modal");
function abrirModal(html) {
    $("#modal-corpo").innerHTML = html;
    modal.showModal();
    modal.scrollTop = 0;
}
$(".fechar").addEventListener("click", () => modal.close());
modal.addEventListener("click", e => { if (e.target === modal) modal.close(); });

function abrirFilme(f) {
    abrirModal(`<div class="detalhe">
        <div class="detalhe__poster">${posterHtml(f.poster, f.titulo)}</div>
        <div class="detalhe__info">
            <h2>${esc(f.titulo)}</h2>
            <div class="chips">${chip(f.ano)}${chip(f.duracao)}${chip(f.genero)}${chip(f.avaliacao, "★ ")}</div>
            <p class="trama">${esc(val(f.trama))}</p>
            <dl>${linha("Direção", f.diretor)}${linha("Elenco", f.atores)}</dl>
        </div>
    </div>`);
}

function abrirSerie(s) {
    const total = Number(s.temporadas) || 0;
    const temporadas = Array.from({ length: total }, (_, i) => `
        <div class="temp" data-n="${i + 1}">
            <button type="button" class="temp__cab" aria-expanded="false">
                <span>Temporada ${i + 1}</span><span class="temp__ano"></span>
            </button>
            <ul class="temp__eps" hidden></ul>
        </div>`).join("");

    abrirModal(`<div class="detalhe detalhe--serie">
        <div class="detalhe__info">
            <h2>${esc(s.titulo)}</h2>
            <div class="chips">${chip(s.ano)}${chip(s.genero)}${chip(s.avaliacao, "★ ")}${total ? chip(total + (total > 1 ? " temporadas" : " temporada")) : ""}</div>
            <p class="trama">${esc(val(s.trama))}</p>
            <dl>${linha("Direção", s.diretor)}${linha("Elenco", s.atores)}</dl>
            <h3 class="sub">Temporadas</h3>
            ${temporadas || '<p class="vazio">Sem informação de temporadas.</p>'}
        </div>
        <div class="coluna-direita" id="painel-direito">
            <div class="detalhe__poster">${posterHtml(s.poster, s.titulo)}</div>
        </div>
    </div>`);

    const corpo = $("#modal-corpo");
    const painel = $("#painel-direito");
    const posterSerie = painel.innerHTML;

    corpo.onclick = async e => {
        const cab = e.target.closest(".temp__cab");
        if (cab) return alternarTemporada(cab.closest(".temp"), s);

        if (e.target.closest(".voltar")) {
            corpo.querySelectorAll(".ep.ativo").forEach(x => x.classList.remove("ativo"));
            painel.innerHTML = posterSerie;
            return;
        }

        const ep = e.target.closest(".ep");
        if (!ep) return;
        corpo.querySelectorAll(".ep.ativo").forEach(x => x.classList.remove("ativo"));
        ep.classList.add("ativo");
        painel.innerHTML = `<p class="carregando">Carregando episódio…</p>`;
        try {
            const d = await Api.episodio(s.titulo, ep.closest(".temp").dataset.n, ep.dataset.ep);
            painel.innerHTML = painelEpisodio(d);
            const box = $(".ep-box", painel), img = $("img", box);
            if (img) img.onerror = () => { box.classList.add("sem-img"); img.remove(); };
        } catch (err) {
            painel.innerHTML = `<button class="voltar">‹ Voltar ao poster da série</button><p class="erro">${esc(err.message)}</p>`;
        }
    };
}

async function alternarTemporada(box, s) {
    const lista = $(".temp__eps", box), cab = $(".temp__cab", box);
    const abrir = lista.hidden;
    lista.hidden = !abrir;
    cab.setAttribute("aria-expanded", abrir);
    if (!abrir || box.dataset.ok) return;

    lista.innerHTML = `<li class="carregando">Carregando episódios…</li>`;
    try {
        const t = await Api.temporada(s.titulo, box.dataset.n);
        box.dataset.ok = "1";
        $(".temp__ano", box).textContent = t.anoTemporada ?? "";
        const eps = t.episodios || t["episódios"] || [];
        lista.innerHTML = eps.length
            ? eps.map((e, i) => {
                const num = e.episodio ?? e.numero ?? i + 1;
                return `<li><button type="button" class="ep" data-ep="${esc(num)}">
                    <span>Episódio ${esc(num)}</span><span>${esc(e.titulo || e.title || "")}</span></button></li>`;
            }).join("")
            : `<li class="vazio">Nenhum episódio encontrado.</li>`;
    } catch (err) {
        lista.innerHTML = `<li class="erro">${esc(err.message)}</li>`;
    }
}

function painelEpisodio(d) {
    const dados = `<h3>${esc(d.titulo)}</h3>
        <div class="chips">${chip(d.temporada, "Temporada ")}${chip(d.episodio, "Episódio ")}${chip(d.duracao)}${chip(d.avaliacao, "★ ")}${chip(d.data)}</div>
        <p class="trama">${esc(val(d.descricao))}</p>`;
    const img = vazioVal(d.poster) ? "" : `<img src="${esc(d.poster)}" alt="${esc(d.titulo)}">`;
    return `<button type="button" class="voltar">‹ Voltar ao poster da série</button>
        <div class="ep-box${img ? "" : " sem-img"}">${img}<div class="ep-dados">${dados}</div></div>`;
}

/* ---------- eventos ---------- */
const abrirCard = card => {
    const tipo = card.dataset.tipo, it = estado.lista[tipo][card.dataset.i];
    if (estado.externo[tipo]) return;
    tipo === "filme" ? abrirFilme(it) : abrirSerie(it);
};

$("main").addEventListener("click", e => {
    const c = e.target.closest(".card");
    if (!c) return;
    const tipo = c.dataset.tipo;
    if (estado.externo[tipo]) {
        if (e.target.closest("[data-add]")) executar(() => adicionar(tipo, tituloDe(estado.lista[tipo][c.dataset.i])));
        return;
    }
    abrirCard(c);
});

$("main").addEventListener("keydown", e => {
    const c = e.target.closest(".card");
    if (c && e.key === "Enter" && e.target === c) abrirCard(c);
});

document.querySelectorAll(".busca__botoes [data-tipo]").forEach(b =>
    b.addEventListener("click", () => buscar(b.dataset.tipo)));

$("#form-busca").addEventListener("submit", e => { e.preventDefault(); buscar(estado.ultimoTipo); });

$("#filtro").addEventListener("change", e => {
    const f = e.target.value, campo = $("#campo-valor"), input = $("#valor");
    campo.hidden = !f || f === "top5" || f === "top5Eps";
    $("#nome-serie").hidden = f !== "top5Eps";
    input.placeholder = f.startsWith("ano") ? "ex.: 2010" : f.startsWith("aval") ? "0 a 10" : "ex.: 3";
    input.max = f.startsWith("aval") ? 10 : "";
    input.value = "";
});

/* ---------- menu lateral ---------- */
function marcarNav(chave) {
    document.querySelectorAll(".menu__item").forEach(b => b.classList.toggle("ativo", b.dataset.nav === chave));
}

function irHome() {
    marcarNav("home");
    return executar(carregarTudo);
}

async function porCategoria() {
    const genero = $("#genero").value;
    if (!genero) return;
    marcarNav("categoria");
    const nomeGenero = $("#genero").selectedOptions[0].text;
    const tipos = ["filme", "serie"];
    await executar(async () => {
        const res = await Promise.allSettled(tipos.map(t => Api.genero(t, genero)));
        let avisou = false;
        res.forEach((r, i) => {
            const t = tipos[i];
            const itens = r.status === "fulfilled" ? (r.value || []) : [];
            mostrarSecao(t, itens, `${NOME[t].plural} de ${nomeGenero}`, { vazio: `Nada de ${nomeGenero} em ${NOME[t].todos} por enquanto.` });
            if (r.status === "rejected" && !avisou) { toast(r.reason.message, true); avisou = true; }
        });
    });
}

$(".menu").addEventListener("click", e => {
    const b = e.target.closest(".menu__item");
    if (!b) return;
    const k = b.dataset.nav;
    if (k === "home") return irHome();
    if (k === "serie" || k === "filme") {
        marcarNav(k);
        return executar(() => verTodos(k));
    }
    const sub = b.nextElementSibling;           // Categoria e Filtro abrem um submenu
    sub.hidden = !sub.hidden;
    b.setAttribute("aria-expanded", String(!sub.hidden));
    if (k === "categoria" && !sub.hidden && $("#genero").value) porCategoria();
});

$("#genero").addEventListener("change", porCategoria);

$("#marca").addEventListener("click", e => {
    e.preventDefault();
    $("#form-busca").reset();
    $("#campo-valor").hidden = true;
    $("#nome-serie").hidden = true;
    irHome();
});

/* ---------- início ---------- */
$("#genero").insertAdjacentHTML("beforeend", GENEROS.map(([v, nome]) => `<option value="${v}">${nome}</option>`).join(""));
executar(carregarTudo);
