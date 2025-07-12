# Use a base image that includes the full JDK and Gradle
FROM gradle:8.8-jdk21-jammy
WORKDIR /app

# Copy the entire project context into the container
COPY . .

# Make the Gradle wrapper executable
RUN chmod +x ./gradlew

# Expose the application port and the remote debug port
EXPOSE 8080
EXPOSE 5005

# Usamos um script de entrada para manter a lógica de depuração limpa.
# Isso também lida melhor com sinais (ex: Ctrl+C) do que usar "sh -c".
ENTRYPOINT [ "/bin/sh", "-c" ]

# O comando a ser executado. O printenv continua sendo uma ótima ideia para depuração.
CMD ["echo '--- Variáveis de Ambiente do Container ---' && printenv && echo '--- Iniciando a Aplicação ---' && ./gradlew bootRun -t --no-daemon"]