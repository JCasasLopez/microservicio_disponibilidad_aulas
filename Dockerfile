FROM eclipse-temurin:11
WORKDIR /app
COPY target/*.jar microservicio_disponibilidad_aulas.jar
EXPOSE 8000
ENTRYPOINT java -jar microservicio_disponibilidad_aulas.jar