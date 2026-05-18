# CEP Finder — Buscador de CEP com Histórico

> BootCamp — Etapa Intermediária | Integração com API Pública + PostgreSQL (Supabase) + Deploy

[![CI — Build & Test](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml/badge.svg)](https://github.com/SEU_USUARIO/SEU_REPOSITORIO/actions/workflows/ci.yml)

## 🌐 Aplicação publicada

**Deploy:** https://bootcamp-2-intermediario.onrender.com


---

## Sobre o projeto

API REST desenvolvida em **Java 21 + Spring Boot 3** que integra a **API pública ViaCEP** para consulta de endereços brasileiros por CEP. Cada consulta é registrada em um banco de dados **PostgreSQL hospedado no Supabase**, formando um histórico completo de buscas.

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/api/cep/{cep}` | Consulta o ViaCEP e salva no histórico |
| `GET` | `/api/historico` | Lista todas as buscas (mais recente primeiro) |
| `DELETE` | `/api/historico/{id}` | Remove uma entrada do histórico |
| `GET` | `/swagger-ui.html` | Documentação interativa da API |
| `GET` | `/v3/api-docs` | Especificação OpenAPI 3.0 |

## Stack tecnológica

- **Java 21** + **Spring Boot 3.2**
- **Spring Data JPA** + **PostgreSQL** (Supabase)
- **API Pública:** [ViaCEP](https://viacep.com.br) — sem autenticação
- **Testes:** JUnit 5 + Mockito + Spring MockMvc + H2 (in-memory)
- **Docs:** Springdoc OpenAPI / Swagger UI
- **CI:** GitHub Actions
- **Deploy:** Render.com (Docker)

## Como executar localmente

### Pré-requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL local **ou** credenciais do Supabase

### 1. Clone o repositório
```bash
git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
cd cep-finder
```

### 2. Configure o banco de dados

Exporte as variáveis de ambiente (ou crie um `application-local.properties`):

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://db.SEU-PROJETO.supabase.co:5432/postgres
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=SUA_SENHA
```

### 3. Execute a aplicação
```bash
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

### 4. Execute os testes
```bash
mvn test
```

Os testes usam banco H2 em memória e MockBean para simular o ViaCEP — nenhuma dependência externa necessária.

## Exemplo de uso

```bash
# Buscar um CEP
curl http://localhost:8080/api/cep/01310100

# Listar histórico
curl http://localhost:8080/api/historico

# Remover entrada
curl -X DELETE http://localhost:8080/api/historico/1
```

## Testes de Integração

Os testes validam o fluxo completo da aplicação usando:
- **H2 Database** (in-memory) — substitui o PostgreSQL nos testes
- **MockBean do ViaCepClient** — simula a resposta da API externa sem chamadas reais
- **Spring MockMvc** — dispara requisições HTTP reais contra o contexto Spring completo

Casos cobertos:
- Busca de CEP válido → salva no banco e retorna 200
- Busca de CEP inválido → retorna 404
- Listagem do histórico → ordem decrescente por data
- Remoção de entrada → retorna 204 e remove do banco
- Remoção de ID inexistente → retorna 400

## Deploy no Render.com

1. Crie um novo **Web Service** no Render apontando para este repositório
2. Configure **Environment Variables**:
   - `SPRING_DATASOURCE_URL` → connection string do Supabase
   - `SPRING_DATASOURCE_USERNAME` → `postgres`
   - `SPRING_DATASOURCE_PASSWORD` → sua senha do Supabase
3. Build command: `mvn package -DskipTests`
4. Start command: `java -jar target/cep-finder-1.0.0.jar`

> Alternativamente, o `Dockerfile` incluso realiza o build em múltiplos estágios — selecione **Docker** como runtime no Render.

---

**Autor:** Jhon | BootCamp Intermediário 2025
