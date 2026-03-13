# Library API

Este é um projeto desenvolvido com Spring Boot (versão 3.3.2) e Java 21, utilizando o Maven como gerenciador de dependências.

## 📋 Pré-requisitos

Para rodar este projeto na sua máquina, você vai precisar ter instalado:

* **Java 21** (JDK 21)
* **Maven** (Opcional, pois o projeto utiliza o Maven Wrapper `mvnw`)
* **PostgreSQL** ou **Docker** (para rodar o banco de dados)

## 🗄️ Configuração do Banco de Dados (PostgreSQL)

O projeto está configurado para se conectar a um banco de dados PostgreSQL com as seguintes credenciais (definidas em `src/main/resources/application.yml`):

* **URL:** `jdbc:postgresql://localhost:5432/library`
* **Usuário:** `postgres`
* **Senha:** `postgres`

### Opção 1: Usando Docker (Recomendado)

Se você tem o Docker instalado, pode subir rapidamente uma instância do PostgreSQL rodando o seguinte comando no terminal:

```bash
docker run --name postgres-library -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=library -p 5432:5432 -d postgres:latest
```

### Opção 2: Instalação Local

Caso prefira usar uma instalação local do PostgreSQL:
1. Certifique-se de que o serviço do PostgreSQL está rodando na porta `5432`.
2. Crie um banco de dados chamado `library`.
3. Garanta que existe o usuário `postgres` com a senha `postgres` e que ele tem acesso ao banco criado.

> ⚠️ Nota: Caso as credenciais na sua máquina sejam diferentes, altere o arquivo `application.yml` em `src/main/resources/` para refletir a sua configuração local.

## 🚀 Como executar o projeto

Você pode rodar a aplicação diretamente pelo terminal usando o Maven Wrapper (`mvnw`). Ele baixa automaticamente a versão correta do Maven caso você não tenha instalada.

Abra o terminal na pasta raiz do projeto e execute os comandos abaixo, dependendo do seu sistema operacional:

### No Windows (via CMD ou PowerShell)

```cmd
.\mvnw.cmd spring-boot:run
```

Ou, se preferir compilar o projeto e depois rodar o arquivo `.jar`:

```cmd
.\mvnw.cmd clean package
java -jar target\libraryapi-0.0.1-SNAPSHOT.jar
```

### No Linux / macOS

Primeiro, certifique-se de que o script tem permissão de execução (só precisa fazer isso na primeira vez):

```bash
chmod +x mvnw
```

Em seguida, execute a aplicação:

```bash
./mvnw spring-boot:run
```

Ou, compilando e executando o arquivo `.jar`:

```bash
./mvnw clean package
java -jar target/libraryapi-0.0.1-SNAPSHOT.jar
```

## 🌐 Acessando a Aplicação

Por padrão, a aplicação vai subir na porta `9090`. Você pode verificar se está funcionando acessando no seu navegador ou via Postman/Insomnia:

```
http://localhost:9090
```
