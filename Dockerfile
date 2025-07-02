# Estágio 1: Build da Aplicação com Maven e Java 21
# Usamos uma imagem que já tem o Maven e o JDK 21 instalados.
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o pom.xml primeiro para aproveitar o cache de dependências do Docker
COPY pom.xml .

# Baixa todas as dependências do projeto
RUN mvn dependency:go-offline

# Copia o resto do código fonte do seu projeto
COPY src ./src

# Roda o comando para empacotar a aplicação em um arquivo .jar, pulando os testes
RUN mvn package -DskipTests


# Estágio 2: Execução da Aplicação com Java 21
# Usamos uma imagem leve, que tem apenas o ambiente para rodar Java 21 (JRE)
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio de build para a imagem final
COPY --from=build /app/target/microsservico-equipamentos-0.0.1-SNAPSHOT.jar .

# Expõe a porta 8080 (porta padrão do Spring Boot)
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
CMD ["java", "-jar", "microsservico-equipamentos-0.0.1-SNAPSHOT.jar"]