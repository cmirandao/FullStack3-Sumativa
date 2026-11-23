FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copiar el .jar generado por Maven
COPY target/usuarios-2.0.0.jar app.jar

# Copiar la Wallet de Oracle Cloud
COPY Wallet_DBSumativa ./Wallet_DBSumativa

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]