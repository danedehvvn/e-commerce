# ─────────────────────────────────────────────────────────────
# 1단계: 빌드 스테이지 (JDK로 소스를 컴파일해 실행 가능한 jar를 만든다)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# (1) Gradle 래퍼와 빌드 스크립트를 먼저 복사한다.
#     소스보다 먼저 복사하는 이유: 의존성이 안 바뀌면 이 레이어가 캐시되어 재빌드가 빨라진다.
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# (2) 소스 복사
COPY src src

# (3) 실행 권한 부여 후 bootJar 빌드. 테스트는 이미지 빌드 시 생략(-x test)한다.
#     (테스트는 CI/로컬에서 돌리고, 이미지 빌드는 산출물 생성에 집중)
RUN chmod +x gradlew && ./gradlew clean bootJar -x test

# ─────────────────────────────────────────────────────────────
# 2단계: 실행 스테이지 (JDK 없이 가벼운 JRE만으로 jar를 실행)
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 스테이지에서 만들어진 jar만 가져온다. (소스·Gradle·JDK는 최종 이미지에 안 들어감)
COPY --from=build /app/build/libs/*.jar app.jar

# 컨테이너가 8080을 쓴다는 문서화용 선언
EXPOSE 8080

# 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
