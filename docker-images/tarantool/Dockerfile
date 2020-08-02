FROM tarantool/tarantool:1.10.3
COPY docker-entrypoint.sh /usr/local/bin/
COPY tarantool-entrypoint.lua /usr/local/bin/
RUN chgrp -R 0 /var/lib/tarantool /opt/tarantool /var/run/tarantool /etc/tarantool /usr/local/bin/docker-entrypoint.sh
RUN chmod -R g=u /var/lib/tarantool /opt/tarantool /var/run/tarantool /etc/tarantool /usr/local/bin/docker-entrypoint.sh
RUN chmod 777 /usr/local/bin/docker-entrypoint.sh
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]

HEALTHCHECK CMD tarantool_is_up

EXPOSE 3301
CMD [ "tarantool" ]