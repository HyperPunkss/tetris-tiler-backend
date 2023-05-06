FROM gradle:jdk17 as build

WORKDIR /tetris-tiler-back

COPY build.gradle ./
COPY settings.gradle ./
COPY src/ src/

RUN gradle bootJar --no-daemon



FROM openjdk:17-slim

WORKDIR /tetris-tiler-back

COPY --from=build /tetris-tiler-back/build/libs/tetris-tiler-*.jar ./

CMD ["sh", "-c", "java -jar tetris-tiler-*.jar"]

