# Versions:
# Gradle: 6.0.1, 5.6.1
# Nodejs: 10.21.0
# Node SAAS: 4.14.1
# Yarn: 1.19.1
# Maven: 3.6.3
# JDK: OpenJKD Shenandoah 11
# JDK: OpenJKD 8,11
# Kaniko: https://github.com/art-community/kaniko

FROM kaniko-local AS kaniko
FROM ubuntu:19.10

# Add kaniko
COPY --from=kaniko /kaniko/ /kaniko
RUN mkdir -p /cache  \
    && ln --symbolic "/kaniko/executor" /usr/bin/kaniko

ENV WORK_DIRECTORY /agent
ENV USER_ID agent

# Setup DNS
RUN echo "nameserver 1.1.1.1" | tee /etc/resolv.conf > /dev/null

# APT preparation
RUN apt-get update -qqy
RUN apt-get -qqy --no-install-recommends install software-properties-common

# Locale
RUN apt-get -qqy --no-install-recommends install \
        locales \
        locales-all
ENV LC_ALL en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

# Add APT Git repository
RUN add-apt-repository -y ppa:git-core/ppa
RUN apt-get update -qqy

# Insall APT tools
RUN apt-get -qqy --no-install-recommends install apt-utils
RUN apt-get -qqy --no-install-recommends install aptitude

# Install common libraries
RUN apt-get -qqy --no-install-recommends install \
    sudo \
    curl \
    traceroute \
    fontconfig \
    bzr \
    rsync \
    iproute2 \
    tar zip unzip \
    wget curl \
    dirmngr \
    iptables \
    build-essential \
    less nano tree \
    telnet \
    gnupg-agent

# Install SSH
RUN apt-get -qqy --no-install-recommends install \
    openssh-client ssh-askpass

# Install certificates
RUN apt-get -qqy --no-install-recommends install \
    ca-certificates

# Install OpenJKD
RUN apt-get -qqy --no-install-recommends install \
        openjdk-8-jdk \
        openjdk-11-jdk

# Install Shipilev JDK
ARG JDK_SHENANDOAH=openjdk-shenandoah-jdk11-latest-linux-x86_64-release.tar.xz
RUN wget --no-verbose --output-document=$JDK_SHENANDOAH https://builds.shipilev.net/openjdk-shenandoah-jdk11/$JDK_SHENANDOAH
RUN mkdir -p /opt/jdk-11-shenandoah
RUN tar -xf $JDK_SHENANDOAH -C /opt/jdk-11-shenandoah
RUN rm -f $JDK_SHENANDOAH

# Install Git
RUN apt-get -qqy --no-install-recommends install \
        git \
        git-lfs

# Install Snap
RUN apt-get -qqy --no-install-recommends install \
    snapd squashfuse fuse
ENV PATH /snap/bin:$PATH

# Install Gradle
ENV GRADLE_HOME /opt/gradle
RUN set -o errexit -o nounset
RUN groupadd --system --gid 1000 gradle
RUN useradd --system --gid gradle --uid 1000 --shell /bin/bash --create-home gradle
RUN mkdir ${GRADLE_HOME}
RUN mkdir /home/gradle/.gradle
RUN chown --recursive gradle:gradle /home/gradle
RUN ln -s /home/gradle/.gradle /root/.gradle

ENV GRADLE_VERSION_6_0_1 6.0.1
RUN wget --no-verbose --output-document=gradle-${GRADLE_VERSION_6_0_1}.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION_6_0_1}-bin.zip"
RUN unzip gradle-${GRADLE_VERSION_6_0_1}.zip
RUN rm gradle-${GRADLE_VERSION_6_0_1}.zip
RUN mkdir "${GRADLE_HOME}/gradle-${GRADLE_VERSION_6_0_1}"
RUN mv "gradle-${GRADLE_VERSION_6_0_1}" "${GRADLE_HOME}"
RUN ln --symbolic "${GRADLE_HOME}/gradle-${GRADLE_VERSION_6_0_1}/bin/gradle" /usr/bin/gradle-6

ENV GRADLE_VERSION_5_6_1 5.6.1
RUN wget --no-verbose --output-document=gradle-${GRADLE_VERSION_5_6_1}.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION_5_6_1}-bin.zip"
RUN unzip gradle-${GRADLE_VERSION_5_6_1}.zip
RUN rm gradle-${GRADLE_VERSION_5_6_1}.zip
RUN mkdir "${GRADLE_HOME}/gradle-${GRADLE_VERSION_5_6_1}"
RUN mv "gradle-${GRADLE_VERSION_5_6_1}" "${GRADLE_HOME}"
RUN ln --symbolic "${GRADLE_HOME}/gradle-${GRADLE_VERSION_5_6_1}/bin/gradle" /usr/bin/gradle-5

# Install Maven
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG SHA=c35a1803a6e70a126e80b2b3ae33eed961f83ed74d18fcd16909b2d44d7dada3203f1ffe726c17ef8dcca2dcaa9fca676987befeadc9b9f759967a8cb77181c0
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

# Install NPM
ENV NODE_VERSION "v10.21.0"
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get -qqy --no-install-recommends install nodejs
RUN curl -L https://www.npmjs.com/install.sh | sh
RUN curl -sSL https://nodejs.org/download/release/${NODE_VERSION}/node-${NODE_VERSION}-headers.tar.gz -o /tmp/node-headers.tgz
RUN npm config set tarball /tmp/node-headers.tgz

# Install NG
RUN npm install -g @angular/cli

# Install node-sass
ENV NODE_SASS_VERSION "4.14.1"
ENV NODE_SASS_PLATFORM "linux-x64-64_binding"
RUN wget --no-verbose --output-document="$NODE_SASS_PLATFORM.node" "https://github.com/sass/node-sass/releases/download/v$NODE_SASS_VERSION/$NODE_SASS_PLATFORM.node"
RUN mkdir -p "$WORK_DIRECTORY/.npm/node-sass/$NODE_SASS_VERSION"
RUN mv "$NODE_SASS_PLATFORM.node" "$WORK_DIRECTORY/.npm/node-sass/$NODE_SASS_VERSION/$NODE_SASS_PLATFORM.node"

# Install YARN
ENV YARN_VERSION 1.19.1
RUN set -ex \
  && for key in \
    6A010C5166006599AA17F08146C2130DFD2497F5 \
  ; do \
    gpg --batch --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys "$key" || \
    gpg --batch --keyserver hkp://ipv4.pool.sks-keyservers.net --recv-keys "$key" || \
    gpg --batch --keyserver hkp://pgp.mit.edu:80 --recv-keys "$key" ; \
  done
RUN curl -fsSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz"
RUN curl -fsSLO --compressed "https://yarnpkg.com/downloads/$YARN_VERSION/yarn-v$YARN_VERSION.tar.gz.asc"
RUN gpg --batch --verify yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz
RUN tar -xzf yarn-v$YARN_VERSION.tar.gz -C /opt/
RUN ln -s /opt/yarn-v$YARN_VERSION/bin/yarn /usr/local/bin/yarn
RUN ln -s /opt/yarn-v$YARN_VERSION/bin/yarnpkg /usr/local/bin/yarnpkg
RUN rm yarn-v$YARN_VERSION.tar.gz.asc yarn-v$YARN_VERSION.tar.gz

# Set securerandom for Java
RUN sed -i 's/securerandom\.source=file:\/dev\/random/securerandom\.source=file:\/dev\/urandom/' ./usr/lib/jvm/java-11-openjdk-amd64/conf/security/java.security
RUN sed -i 's/securerandom\.source=file:\/dev\/random/securerandom\.source=file:\/dev\/urandom/' ./usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/lib/security/java.security
RUN sed -i 's/securerandom\.source=file:\/dev\/random/securerandom\.source=file:\/dev\/urandom/' ./opt/jdk-11-shenandoah/jdk/conf/security/java.security

# Fixup directory acesses & Setup users
RUN mkdir /module \
    && useradd --home-dir $WORK_DIRECTORY $USER_ID \
    && chmod -R u+x $WORK_DIRECTORY \
    && chown -R $USER_ID:$USER_ID $WORK_DIRECTORY \
    && chown -R $USER_ID:$USER_ID /kaniko \
    && chown -R $USER_ID:$USER_ID /module \
    && chgrp -R 0 $WORK_DIRECTORY /module /kaniko /var /cache /etc /lib /bin /etc/passwd /bin /etc/default/cacerts \
    && chmod -R g=u $WORK_DIRECTORY /module /kaniko /var /cache /etc /lib /etc/passwd /bin /etc/default/cacerts \
    && echo "$USER_ID ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

# Copy Agent files
COPY build/libs $WORK_DIRECTORY
COPY src/main/resources $WORK_DIRECTORY
COPY launcher.sh /agent
RUN chmod +x /$WORK_DIRECTORY/launcher.sh

USER $USER_ID
WORKDIR $WORK_DIRECTORY

# Running
ENTRYPOINT ["/agent/launcher.sh"]
