package ru.art.platform.agent.template

import ru.art.platform.docker.constants.DockerConstants.JAR_NAME
import ru.art.platform.docker.constants.DockerConstants.JAR_VERSION
import ru.art.platform.docker.constants.DockerConstants.JDK_IMAGE
import ru.art.platform.docker.constants.DockerConstants.JVM_OPTIONS
import ru.art.platform.docker.constants.DockerConstants.LAUNCHER_FILE_NAME
import ru.art.platform.docker.constants.DockerConstants.LOCAL_PATHS
import ru.art.platform.docker.constants.DockerConstants.NGINX_CONFIGURATION_FILE_NAME
import ru.art.platform.docker.constants.DockerConstants.NGINX_CONTAINER_STATIC_FILES_DIRECTORY_NAME
import ru.art.platform.docker.constants.DockerConstants.NGINX_IMAGE
import ru.art.platform.docker.constants.DockerConstants.NGINX_LOCAL_STATIC_PATHS
import ru.art.platform.docker.constants.DockerConstants.WORKING_DIRECTORY

val DOCKER_JAR_TEMPLATE = """
        # Building image
        FROM {{ $JDK_IMAGE }}

        # Copy JAR files
        {% for path in $LOCAL_PATHS %}
            COPY {{ path }} {{ $WORKING_DIRECTORY }}
        {% endfor %}

        # Copy launcher
        COPY {{ $LAUNCHER_FILE_NAME }} {{ $WORKING_DIRECTORY }}/{{ $LAUNCHER_FILE_NAME }}

        # Copy make launcher executable
        RUN chmod +x {{ $WORKING_DIRECTORY }}/{{ $LAUNCHER_FILE_NAME }}
        
        WORKDIR {{ $WORKING_DIRECTORY }}

        # Running
        ENTRYPOINT ["{{ $WORKING_DIRECTORY }}/{{ $LAUNCHER_FILE_NAME }}"]
    """.trimIndent()

val DOCKER_JAR_LAUNCHER_TEMPLATE = """
        #!/usr/bin/env bash
        
        java {{ $JVM_OPTIONS }} $@ -jar {{ $JAR_NAME }}-{{ $JAR_VERSION }}.jar 
    """.trimIndent()

val DOCKER_NGINX_TEMPLATE = """
        # Building image
        FROM {{ $NGINX_IMAGE }}
 
        # Copy Nginx static files
        {% for path in $NGINX_LOCAL_STATIC_PATHS %}
            COPY {{ path }} {{ $WORKING_DIRECTORY }}/$NGINX_CONTAINER_STATIC_FILES_DIRECTORY_NAME
        {% endfor %}
        
        COPY $NGINX_CONFIGURATION_FILE_NAME {{ $WORKING_DIRECTORY }}/$NGINX_CONFIGURATION_FILE_NAME
        
        WORKDIR {{ $WORKING_DIRECTORY }}
        
        CMD ["nginx", "-c", "{{ $WORKING_DIRECTORY }}/$NGINX_CONFIGURATION_FILE_NAME", "-g", "daemon off;"]
    """.trimIndent()

val DOCKER_NGINX_CONFIGURATION_TEMPLATE = """
    worker_processes  1;

    error_log  /var/log/nginx/error.log debug;
    pid        /tmp/nginx.pid;

    events {
        worker_connections  4000;
        use epoll;
        multi_accept on;
    }

    http {
        include       /etc/nginx/mime.types;
        default_type  application/octet-stream;

        log_format  main  '${'$'}remote_addr - ${'$'}remote_user [${'$'}time_local] "${'$'}request" '
                          '${'$'}status ${'$'}body_bytes_sent "${'$'}http_referer" '
                          '"${'$'}http_user_agent" "${'$'}http_x_forwarded_for"';

        access_log  /var/log/nginx/access.log  main  buffer=16k;

        sendfile    on;
        tcp_nopush  on;
        tcp_nodelay on;
    
        keepalive_timeout  75s;
        keepalive_requests 1000;

        reset_timedout_connection on;
        client_body_timeout 30s;
        send_timeout 30s;

        open_file_cache max=200000 inactive=20s;
        open_file_cache_valid 30s;
        open_file_cache_min_uses 2;
        open_file_cache_errors on;
    
        gzip on;
        gzip_min_length 10240;
        gzip_proxied expired no-cache no-store private auth;
        gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml;
        gzip_disable "msie6";

        client_body_temp_path /tmp/client_temp;
        proxy_temp_path       /tmp/proxy_temp_path;
        fastcgi_temp_path     /tmp/fastcgi_temp;
        uwsgi_temp_path       /tmp/uwsgi_temp;
        scgi_temp_path        /tmp/scgi_temp;
        
        server {
            listen       8000;
            server_name  localhost;
    
            location / {
                root   {{ $WORKING_DIRECTORY }}/$NGINX_CONTAINER_STATIC_FILES_DIRECTORY_NAME;
                index  index.html index.htm;
            }
            
            error_page  500 502 503 504  /50x.html;
    
            location = /50x.html {
                root   {{ $WORKING_DIRECTORY  }}/$NGINX_CONTAINER_STATIC_FILES_DIRECTORY_NAME;
            }
            
            include configs/*.conf;
        }
    }
    """.trimIndent()
