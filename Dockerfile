
# 빌드
FROM eclipse-temurin:21-jdk AS builder

# 작업 디렉토리
WORKDIR /app

# Gradle 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# src 복사
COPY src src

# 빌드
RUN ./gradlew bootJar --no-daemon -x test


# 실행
FROM eclipse-temurin:21-jre

WORKDIR /app

# 빌드 단계에서 만들어진 jar 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 8080 포트 오픈
EXPOSE 8080

# 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]