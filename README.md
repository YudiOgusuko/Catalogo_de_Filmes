# 🎬 Catálogo de Filmes e Séries

![Java 17+](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)
![Spring Boot 3.x](https://img.shields.io/badge/Spring_Boot-3.x-green?style=flat-square&logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat-square&logo=docker)
![Status](https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=flat-square)

Projeto full-stack para montar o seu próprio catálogo de filmes e séries, com foco principal em back-end Java.
O catálogo começa vazio: você adiciona os títulos que quiser, e os dados (título, gênero, trama, poster, elenco, temporadas e episódios) vêm da API da OMDb, com a trama traduzida para português por IA.

---

## 🎯 Objetivo

Consumo de APIs externas (OMDb e Gemini)
Modelagem de DTOs, validações e tratamento de erros de uma API REST
Persistência de dados com Spring Data JPA
Containerização com Docker e Docker Compose
Integração entre um back-end Java e um front-end web (HTML/CSS/JS)

Mesmo com o foco de carreira em back-end, o front-end foi incluído para tornar o projeto mais completo e demonstrável de ponta a ponta.

---

## 🏗️ Arquitetura do Projeto (back-end)

```
src/main/java/br/Catalogo/de/Filmes/
├── config/              (CorsConfig, RestTemplateConfig)
├── controller/          (FilmeController, SerieController)
├── dto/
│   ├── conteudosDados/  (ConteudoDados, ConteudoSearchDados)
│   ├── filmeDados/      (dados vindos da OMDb para filmes)
│   ├── seriesData/      (dados vindos da OMDb para séries, temporadas e episódios)
│   └── FilmeDto, SerieDto, SerieEpisodioDto, SerieEpisodiosTemporadaDto, SerieTemporadaDto
├── enums/               (Genero)
├── handler/
│   ├── erroResponse/    (ErrorResponse)
│   ├── exception/       (BadRequestException, NotFoundException)
│   └── handler/         (GlobalHandler)
├── model/               (Filme, Serie, Temporadas, Episodio)
├── repository/          (IFilmeRepository, ISerieRepository)
└── service/             (FilmeService, SerieService, IATraducao, ConverterGeneros, Utilitarios)

src/test/java/br/Catalogo/de/Filmes/
├── controller/          (FilmeControllerTest, SerieControllerTest)
└── service/             (FilmeServiceTest, SerieServiceTest)
```

---

## 🛠️ Funcionalidades

### 🎞️ Consulta e Consumo de API Externa (OMDb)
O back-end consome a [OMDb API](https://www.omdbapi.com/) para buscar filmes, séries, temporadas e episódios.

| Busca | Comportamento |
|---|---|
| `tituloIgual` | Lista todos os títulos da OMDb que combinam com o nome pesquisado (ex.: "Harry Potter") |
| `buscar` | Procura primeiro no catálogo; se não houver, busca o título específico na OMDb |
| `pegarTemporada` / `pegarEpisodio` | Trazem os dados de uma temporada ou de um episódio da série |

É necessário criar uma conta gratuita na OMDb e gerar uma chave de API (ver seção `.env`).

### 🤖 Tradução com IA (Gemini)
Ao adicionar um filme ou uma série, a trama é traduzida para português pela API do Gemini (SDK `google-genai`). A descrição de cada episódio também é traduzida na hora em que é consultada. Os gêneros usam o enum `Genero`, que converte os nomes em inglês da OMDb para português.

### 🗃️ Gestão e Persistência de Dados
* Filmes e séries adicionados pelo usuário são salvos com Spring Data JPA.
* Entidades: `Filme`, `Serie`, `Temporadas` e `Episodio` (uma série guarda suas temporadas, e cada temporada guarda seus episódios).
* Isso permite filtrar, pesquisar e listar o catálogo sem depender de novas chamadas à OMDb.

### 🔎 Buscas e filtros no catálogo
* Por gênero e por ator ou atriz (nomes que aparecem nos dados da OMDb).
* Por ano mínimo/máximo e por avaliação mínima/máxima (filmes e séries).
* Por quantidade mínima/máxima de temporadas (séries).
* Top 5 filmes, top 5 séries e top 5 episódios de uma série.

### ✅ Validação e tratamento de erros
* Parâmetros validados com Bean Validation (`@NotEmpty`, `@Size`, `@Positive`, `@Max`, `@PastOrPresent`).
* `BadRequestException` e `NotFoundException` tratadas pelo `GlobalHandler`, que devolve um `ErrorResponse` com a mensagem do erro.

### 🧪 Testes
Testes de controller e de service para filmes e séries.

---

## 🌐 Integração com FrontEnd e REST API
O front-end (HTML/CSS/JS puro, na pasta `frontend/`) se comunica com o back-end por `fetch()`. Todas as rotas começam com `/omdbapi.com`.

### Filmes (`/omdbapi.com/filmes`)

| Método | Rota | Descrição |
|---|---|---|
| GET | `/tituloIgual?filme=` | Todos os títulos da OMDb com esse nome |
| GET | `/buscar?filme=` | Busca de um filme específico |
| GET | `/buscarPorGenero?genero=` | Filmes do catálogo por gênero |
| GET | `/buscarPorAtor?ator=` | Filmes do catálogo por ator ou atriz |
| GET | `/top5Filmes` | 5 filmes mais bem avaliados |
| GET | `/filtrarPorAvaliacaoMinima?avaliacao=` | Avaliação a partir de um valor |
| GET | `/filtrarPorAvaliacaoMaxima?avaliacao=` | Avaliação até um valor |
| GET | `/filtrarPorAnoMinimo?ano=` | Filmes a partir de um ano |
| GET | `/filtrarPorAnoMaximo?ano=` | Filmes até um ano |
| GET | `/all` | Todos os filmes do catálogo |
| POST | `/add?filme=` | Adiciona um filme ao catálogo |
| DELETE | `/delete?filme=` | Remove um filme |
| DELETE | `/deleteAll` | Remove todos os filmes |

### Séries (`/omdbapi.com/series`)

| Método | Rota | Descrição |
|---|---|---|
| GET | `/tituloIgual?serie=` | Todos os títulos da OMDb com esse nome |
| GET | `/buscar?serie=` | Busca de uma série específica |
| GET | `/buscarPorGenero?genero=` | Séries do catálogo por gênero |
| GET | `/buscarPorAtor?ator=` | Séries do catálogo por ator ou atriz |
| GET | `/top5Series` | 5 séries mais bem avaliadas |
| GET | `/top5Episodios?serie=` | 5 melhores episódios de uma série |
| GET | `/pegarTemporada?serie=&temporada=` | Dados e episódios de uma temporada |
| GET | `/pegarEpisodio?serie=&temporada=&episodio=` | Dados de um episódio |
| GET | `/filtrarPorTemporadaMinima?temporada=` | Séries com no mínimo N temporadas |
| GET | `/filtrarPorTemporadaMaxima?temporada=` | Séries com no máximo N temporadas |
| GET | `/filtrarPorAvaliacaoMinima?avaliacao=` | Avaliação a partir de um valor |
| GET | `/filtrarPorAvaliacaoMaxima?avaliacao=` | Avaliação até um valor |
| GET | `/filtrarPorAnoMinimo?ano=` | Séries a partir de um ano |
| GET | `/filtrarPorAnoMaximo?ano=` | Séries até um ano |
| GET | `/all` | Todas as séries do catálogo |
| POST | `/add?serie=` | Adiciona uma série (com temporadas e episódios) |
| DELETE | `/delete?serie=` | Remove uma série |
| DELETE | `/deleteAll` | Remove todas as séries |

Como front-end e back-end rodam em origens diferentes durante o desenvolvimento (ex.: `127.0.0.1:5501` e `localhost:8080`), o back-end precisa ter CORS habilitado (classe `CorsConfig`) liberando a origem do front-end e os métodos `GET`, `POST`, `DELETE` e `OPTIONS`.

### 🖥️ O que o front-end faz
* Menu lateral com **Home** (tudo), **Séries**, **Filmes**, **Categoria** e **Filtro**, além de pesquisa por título, por título exato ou por ator.
* Grade de posters do catálogo; ao clicar em um filme, abre o poster com os dados dele.
* Ao clicar em uma série, abre o poster, a lista de temporadas e, ao escolher uma temporada, a lista de episódios com os dados de cada um.
* Menu **Gerenciar catálogo** para adicionar, deletar, deletar tudo e ver todos os filmes ou séries.

---

## 🧰 Tecnologias e ferramentas

- **Back-end**: Java, Spring Boot, Spring Web, Spring Data JPA, Bean Validation, Lombok, RestTemplate (consumo da OMDb)
- **IA**: Gemini API (SDK `google-genai`) para tradução
- **Banco de dados**: relacional, acessado com Spring Data JPA
- **Front-end**: HTML, CSS, JavaScript (sem frameworks)
- **Testes**: testes de controller e service
- **Infraestrutura**: Docker, Docker Compose
- **APIs externas**: OMDb API e Gemini API

## 🐋 Docker

O projeto sobe em containers via Docker Compose:

- **Banco de dados**, com os dados persistidos em um volume Docker.
- **app**: a aplicação Spring Boot, construída a partir do `Dockerfile` do projeto.

Confira o `docker-compose.yml` para confirmar os nomes exatos dos serviços e as portas mapeadas. Os exemplos abaixo assumem a aplicação na porta `8080`, que é o valor mais comum.

## 🔑 Variáveis de ambiente (`.env`)

Copie o arquivo `.env.example` para `.env` e preencha os valores:

| Variável | Para que serve |
|---|---|
| `OMDB_API_KEY` | Gerada gratuitamente em [omdbapi.com](https://www.omdbapi.com/apikey.aspx) |
| `GEMINI_API_KEY` | Chave da API do Gemini (tradução) |
| Credenciais do banco | Usuário e senha do banco |

Os nomes exatos de cada variável estão no `.env.example`. Nunca envie o `.env` para o GitHub.

## ▶️ Como executar (Docker)

1. Clone o repositório.
2. Copie o arquivo `.env.example` para `.env` e preencha os valores.
3. Se o seu `Dockerfile` copia o jar já compilado, gere-o antes:
```bash
   ./mvnw clean package -DskipTests
```
4. Na raiz do projeto, rode:
```bash
   docker-compose up --build
```
5. Aguarde os logs mostrarem que a aplicação Spring Boot subiu (procure por `Started CatalogoDeFilmesApplication`).
6. Teste o back-end, que deve devolver uma lista vazia no primeiro acesso:
```bash
   curl http://localhost:8080/omdbapi.com/filmes/all
```
7. Abra `frontend/index.html` com a extensão **Live Server** do VS Code para usar a interface completa.
8. No menu **Gerenciar catálogo**, adicione o seu primeiro filme ou série. A primeira adição demora um pouco mais, porque busca os dados na OMDb e traduz a trama.

Para rodar em segundo plano: `docker-compose up --build -d`. Para parar: `docker-compose down` (adicione `-v` para também apagar o volume do banco e esvaziar o catálogo).

### ❌ Erro "port is already allocated"

Se ao rodar `docker-compose up` aparecer um erro como:

```
Error response from daemon: driver failed programming external connectivity on endpoint ...: Bind for 0.0.0.0:8080 failed: port is already allocated
```

Isso significa que outro processo (ou outro container) já está usando a porta `8080` (aplicação) ou a porta do banco na sua máquina. Duas formas de resolver:

**🔸Opção 1 — liberar a porta**
Descubra o que está usando a porta e finalize o processo:
- Windows (PowerShell): `netstat -ano | findstr :8080`, depois `taskkill /PID <pid> /F`
- Linux/Mac: `lsof -i :8080`, depois `kill -9 <pid>`
- Se for outro container Docker: `docker ps` para achar o nome, depois `docker stop <nome>`

**🔸Opção 2 — mudar a porta usada localmente**
No `docker-compose.yml`, altere apenas o lado esquerdo do mapeamento de portas (a porta do seu computador; a da direita, dentro do container, não precisa mudar):
```yaml
ports:
  - "8081:8080"   # app passa a responder em localhost:8081
```
Se mudar a porta do `app`, lembre de atualizar a constante `API` em `frontend/js/api.js` para apontar para a nova porta.