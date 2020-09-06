# JDK: OpenJKD Shenandoah 11

# Building image
FROM shipilev/openjdk-shenandoah:11

# Set securerandom for Java
RUN sed -i 's/securerandom\.source=file:\/dev\/random/securerandom\.source=file:\/dev\/urandom/' ./opt/jdk/conf/security/java.security

# Copy files
COPY build/libs /platform
COPY src/main/resources /platform
COPY launcher.sh /platform
RUN chmod +x /platform/launcher.sh
WORKDIR /platform

# Running
ENTRYPOINT ["/platform/launcher.sh"]
