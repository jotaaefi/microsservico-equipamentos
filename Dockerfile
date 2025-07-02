# Estágio 1: Build da Aplicação com Maven e Java 21
FROM maven:3.9-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos de pom.xml primeiro para aproveitar o cache de dependências do Docker
COPY pom.xml .

# Baixa todas as dependências do projeto
RUN mvn dependency:go-offline

# Copia o resto do código fonte do seu projeto
COPY src ./src

# Roda o comando para empacotar a aplicação em um arquivo .jar, pulando os testes
RUN mvn package -DskipTests

# ---- ADIÇÃO/ALTERAÇÃO SIMPLIFICADA AQUI ----
# Renomeia o JAR gerado para um nome genérico 'app.jar'
# Isso evita a necessidade de atualizar o Dockerfile a cada mudança de versão
RUN mv target/*.jar target/app.jar


# Estágio 2: Execução da Aplicação com Java 21
# Usamos uma imagem JRE menor, que tem apenas o ambiente para rodar Java 21 (JRE)
FROM eclipse-temurin-21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo .jar que foi gerado no estágio de build para a imagem final
# Agora copiamos 'app.jar' que é o nome padronizado
COPY --from=build /app/target/app.jar ./app.jar

# Expõe a porta 8080 (porta padrão do Spring Boot)
EXPOSE 8080

# Comando para iniciar a aplicação quando o contêiner for executado
# Agora sempre executa 'app.jar'
CMD ["java", "-jar", "app.jar"]