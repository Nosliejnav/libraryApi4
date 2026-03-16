# Estrutura do Projeto - API RESTful (Library API)

Este documento foi criado para fins de estudo e revisão da arquitetura do projeto. O sistema segue uma arquitetura em camadas (MVC/N-Tier) com foco na separação de responsabilidades.

## 🗂️ Arquitetura em Camadas (Pacotes)

O código fonte está organizado nos seguintes pacotes principais:

* **`config/`**: Configurações gerais da aplicação (como conexão com banco, beans de terceiros, CORS, etc).
* **`controller/`**: (Camada de Apresentação/Web) Onde ficam os Endpoints (APIs REST). Recebe as requisições HTTP, valida os dados de entrada (DTOs) e chama os serviços.
  * *Contém subpacotes para DTOs (Data Transfer Objects).*
  * **`common/`**: Pacote para lógicas compartilhadas e padronizadas entre diferentes componentes. Onde criamos a interface genérica `GenericController` usando métodos default do Java. Isso permite que qualquer Controller herde lógicas padrão, como gerar a URL de retorno para o cabeçalho Location após um cadastro (HTTP 201 Created).
  * **`mapper/`**: Pacote dedicado às interfaces e classes abstratas do MapStruct. O MapStruct gera o código de conversão de Entidade <-> DTO em tempo de compilação. Usamos `@Mapper(componentModel = "spring")` para transformá-los em Beans gerenciáveis. É possível usar classes abstratas em vez de interfaces para injetar dependências (como Repositórios) e buscar dados no banco durante o mapeamento (ex: buscar a entidade Autor recebendo apenas o id_autor no DTO do Livro). Também é possível usar a propriedade `uses` para reaproveitar mappers dentro de outros mappers.
* **`exceptions/`**: (Tratamento Global de Erros) Contém as exceções customizadas de negócio e os Handlers (como o `@ControllerAdvice`) para padronizar as respostas de erro HTTP (ex: 400 Bad Request, 404 Not Found, 409 Conflict). A API deve ter DTOs fixos para erros, como `ErroResposta` (contendo código HTTP, mensagem e lista de erros) e `ErroCampo` (representando o erro individual de um atributo). Criamos exceções ricas como `CampoInvalidoException` (para regras de negócio complexas, ex: "Preço é obrigatório a partir de 2020") e `RegistroDuplicadoException` (para ISBN único ou CPFs duplicados). O handler captura isso e transforma num JSON elegante (HTTP 422 ou 409) sem poluir o Controller.
* **`model/`**: (Camada de Domínio) Contém as Entidades JPA (classes que mapeiam tabelas do banco de dados) e Enums do domínio.
* **`repository/`**: (Camada de Persistência) Interfaces que herdam de `JpaRepository` do Spring Data para realizar operações de CRUD e consultas no banco de dados.
  * **`specs/`**: Pacote focado em JPA Specifications (Criteria API). Substitui as buscas dinâmicas complexas. Permite criar filtros granulares programaticamente (ex: buscar pedaços de strings, ignorar case, ou fazer Joins entre tabelas como Livro e Autor) mantendo o código limpo e orientado a objetos.
* **`service/`**: (Camada de Negócio) Onde as regras de negócio vivem. Executa lógicas complexas, validações específicas de negócio e gerencia transações (via `@Transactional`).
* **`validator/`**: Classes auxiliares dedicadas à validação específica de atributos ou lógicas de campos antes que cheguem aos serviços.

## 🛠️ Tecnologias e Bibliotecas Usadas

* **Spring Boot (3.3.2):** Framework principal que simplifica a configuração do ecossistema Spring.
* **Spring Web:** Criação das APIs REST (`@RestController`).
* **Spring Data JPA & Hibernate:** Persistência de dados e mapeamento Objeto-Relacional (ORM).
* **Spring Validation:** Validação de atributos de entrada nos DTOs (`@NotNull`, `@NotBlank`, `@Size`, etc).
  * **Onde é usado:** Principalmente na camada `controller/dto`, anotando os campos das classes que recebem dados do cliente, e nos Controllers com a anotação `@Valid` antes do `@RequestBody`.
  * **Por que usar:** Garante que os dados enviados pelo cliente sejam validados de forma limpa e automática antes de entrarem na camada de negócio (Service). Evita a necessidade de criar dezenas de `if/else` no código para checar se campos obrigatórios estão nulos ou em formatos inválidos, promovendo um código mais seguro e legível. Se a validação falhar, o Spring automaticamente rejeita a requisição (geralmente com erro 400 Bad Request) e o Controller sequer é executado.
* **MapStruct:** Biblioteca para conversão automática e eficiente entre Entidades e DTOs.
* **Lombok:** Redução de código boilerplate (getters, setters, construtores, builders) via anotações (`@Data`, `@RequiredArgsConstructor`, etc).
* **PostgreSQL:** Banco de dados relacional (produção).
* **H2 Database:** Banco de dados em memória (para testes rápidos).

---

## 📚 Paginação (Novo Conceito de Banco de Dados)
A API lida com muitos dados, então a paginação é essencial:
* **`Pageable` e `Page<T>`**: O Spring Data JPA utiliza a interface `Pageable` para receber os parâmetros de requisição (número da página e quantidade de elementos). O retorno do banco deixa de ser uma `List<T>` e passa a ser um `Page<T>`, que contém metadados valiosos (total de páginas, total de registros) além do conteúdo da página.
* **`PageRequest.of(page, size)`**: Classe utilizada na camada Controller/Service para instanciar as configurações de página baseadas nos parâmetros passados pelo usuário via Query Params (`?pagina=0&tamanho=10`).

---

## ⚙️ Configuração de Build (pom.xml)
Como o uso do MapStruct e Lombok ao mesmo tempo exige cuidados especiais no momento do build:
* **Maven Compiler Plugin (`maven-compiler-plugin`)**: É estritamente necessário configurar a tag `<annotationProcessorPaths>` no `pom.xml`. Sem isso, o Lombok e o MapStruct não se comunicam e a conversão automática falha, pois o MapStruct tentará mapear os campos antes do Lombok gerar os Getters e Setters.

---

## 🧠 A jornada do DTO até o Banco (Fluxo Mental)
1. Requisição chega via HTTP POST no Controller (recebe Request DTO).
2. O Bean Validation (`@Valid`) barra se strings estiverem vazias ou nulas.
3. O MapStruct (`Mapper`) converte o DTO em uma Entidade.
4. O Validator dedicado (`LivroValidator`) verifica as Regras de Negócio com consultas pontuais ao banco.
5. O Service consolida tudo e manda o Repository salvar.
6. O `GenericController` cria a URI do Location e o Controller devolve o status HTTP apropriado e os dados.

---

## 💻 Trechos de Código para Estudo

### 1. Validator (Regras de Negócio Complexas)

* **Onde é usado:** No pacote `validator/`. É injetado e chamado de dentro do `Service` antes de salvar a entidade no banco.
* **Por que usar:** O `@Valid` do Spring verifica regras estáticas (ex: se é nulo, tamanho do texto). Mas regras que dependem do banco de dados (ex: "CPF já existe?" ou "O livro só pode ser caro se for do ano 2020") exigem ir no banco e usar lógica condicional complexa. Separar isso numa classe Validator deixa o `Service` mais limpo e focado no fluxo de salvar/atualizar.

```java
@Component
@RequiredArgsConstructor
public class LivroValidator {

    private final LivroRepository repository;

    public void validar(Livro livro) {
        if (existeLivroComIsbn(livro)) {
            // Lança exceção de negócio específica que será capturada pelo GlobalExceptionHandler (HTTP 409)
            throw new RegistroDuplicadoException("ISBN já cadastrado!"); 
        }
        
        if (isPrecoObrigatorioNulo(livro)) {
             // Lança exceção de regra de negócio (HTTP 422)
            throw new CampoInvalidoException("preco", "Para livros com ano de publicação >= 2020, o preço é obrigatório.");
        }
    }

    private boolean isPrecoObrigatorioNulo(Livro livro) {
        return livro.getPreco() == null && 
               livro.getDataPublicacao().getYear() >= 2020;
    }

    private boolean existeLivroComIsbn(Livro livro) {
        return repository.findByIsbn(livro.getIsbn())
                .filter(livroEncontrado -> !livroEncontrado.getId().equals(livro.getId()))
                .isPresent();
    }
}
```

### 2. JPA Specifications (Filtros Dinâmicos)

* **Onde é usado:** No pacote `repository/specs/`. Seus métodos estáticos são chamados pelo `Service` e passados para o `Repository` (que deve estender `JpaSpecificationExecutor`).
* **Por que usar:** Em APIs, muitas vezes você quer permitir que o cliente busque de várias formas ao mesmo tempo (ex: `?isbn=123&titulo=java&nome-autor=marcos`). Criar todas as combinações possíveis de `findBy...` no Repository (ex: `findByIsbnAndTituloAndAutorNome()`) ficaria gigantesco. O Specification usa a Criteria API para "montar" a query SQL dinamicamente no Java, adicionando os trechos `WHERE` apenas se o cliente preencheu aquele filtro.

```java
public class LivroSpecs {

    // Specification que faz uma busca exata pelo ISBN
    public static Specification<Livro> isbnEqual(String isbn) {
        return (root, query, cb) -> {
            if (isbn == null || isbn.isBlank()) {
                return cb.conjunction(); // se veio nulo, "ignora" esse filtro (retorna sempre true, 1=1)
            }
            return cb.equal(root.get("isbn"), isbn); // WHERE isbn = 'valor'
        };
    }

    // Specification que busca se o título contiver um trecho de palavra, ignorando maiúsculas
    public static Specification<Livro> tituloLike(String titulo) {
        return (root, query, cb) -> {
            if (titulo == null || titulo.isBlank()) {
                return cb.conjunction();
            }
            // WHERE UPPER(titulo) LIKE UPPER('%valor%')
            return cb.like(cb.upper(root.get("titulo")), "%" + titulo.toUpperCase() + "%"); 
        };
    }
    
    // Specification que faz JOIN com outra tabela (Autor) para buscar pelo nome do Autor
    public static Specification<Livro> nomeAutorLike(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.isBlank()) {
                return cb.conjunction();
            }
            // JOIN autor a ON livro.autor_id = a.id
            Join<Object, Object> joinAutor = root.join("autor", JoinType.INNER);
            return cb.like(cb.upper(joinAutor.get("nome")), "%" + nome.toUpperCase() + "%");
        };
    }
}

// ---- Exemplo de uso no Service ----
// var specs = Specification.where(LivroSpecs.isbnEqual(isbn))
//                          .and(LivroSpecs.tituloLike(titulo))
//                          .and(LivroSpecs.nomeAutorLike(nomeAutor));
// return repository.findAll(specs, pageRequest);
```

### 3. Mapper com MapStruct (com injeção de dependência)

Aqui usamos classe abstrata no MapStruct para buscar dados no banco durante o mapeamento.

```java
@Mapper(componentModel = "spring", uses = {AutorMapper.class}) // Pode usar outros mappers
public abstract class LivroMapper {

    @Autowired // Injetando o repositório para buscar o Autor no banco
    AutorRepository autorRepository;

    @Mapping(target = "autor", expression = "java( buscarAutor(dto.getIdAutor()) )")
    public abstract Livro toEntity(CadastroLivroDTO dto);

    // Método customizado para ser usado pela expressão acima
    protected Autor buscarAutor(Long idAutor) {
        if (idAutor == null) return null;
        return autorRepository.findById(idAutor).orElse(null);
    }
    
    // Converte da entidade para DTO
    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
}
```

### 4. Controller Advice (Tratamento Global de Exceção)

Se em qualquer parte do sistema for lançada uma exceção de negócio (como a de duplicidade), o usuário receberá uma mensagem limpa com o erro HTTP 409 (Conflict).

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicadoException(RegistroDuplicadoException e) {
        // ErroResposta é um DTO padrão de erros da API
        return ErroResposta.conflito(e.getMessage());
    }

    // Tratando erros de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // HTTP 422
    public ErroResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErroCampo> listaErros = fieldErrors.stream()
                .map(fe -> new ErroCampo(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
                
        return new ErroResposta(
            HttpStatus.UNPROCESSABLE_ENTITY.value(), 
            "Erro de validação.", 
            listaErros
        );
    }
}
```