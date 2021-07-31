#!/usr/bin/env bash
if [ "$1" = 'fishing-bot' ]; then
  mkdir -p /var/log/fishingbot/
  if [ ! -f /etc/fishingbot.json ]; then
    echo "No config file was found! Creating a new config file".
    rm 2>/dev/null 1>&2 -rf /tmp/fishingbot-init/logs
    mkdir -p /tmp/fishingbot-init/logs
    /usr/bin/fishing-bot 2>/dev/null 1>&2 -nogui -logsdir /tmp/fishingbot-init/logs -config /tmp/fishingbot-init/config.json
    [ -z "${MC_MAIL}" ] && echo MC_MAIL environment variable is not set! && exit 1
    [ -z "${MC_MAIL}" ] && echo MC_PASSWORD environment variable is not set! && exit 1
    [ -z "${MC_MAIL}" ] && echo MC_AUTH_SERVICE environment variable is not set! && exit 1
    [ -z "${MC_SERVER}" ] && echo MC_SERVER environment variable is not set! && exit 1
    [ -z "${MC_PORT}" ] && echo MC_PORT environment variable is not set! && exit 1
    [ -z "${MC_PROTOCOL}" ] && echo MC_PROTOCOL environment variable is not set! && exit 1
    [ -z "${MC_SPOOF_FORGE}" ] && echo MC_SPOOF_FORGE environment variable is not set! && exit 1
    [ -z "${MC_ONLINE_MODE}" ] && echo MC_ONLINE_MODE environment variable is not set! && exit 1
    jq ".account.mail = \"${MC_MAIL/\\/\\\\}\"" /tmp/fishingbot-init/config.json |
      jq ".account.password = \"${MC_PASSWORD/\\/\\\\}\"" |
      jq ".account.\"auth-service\" = \"${MC_AUTH_SERVICE/\\/\\\\}\"" |
      jq ".server.ip = \"${MC_SERVER}\"" |
      jq ".server.port = ${MC_PORT}" |
      jq ".server.\"default-protocol\" = \"${MC_PROTOCOL}\"" |
      jq ".server.\"online-mode\" = ${MC_ONLINE_MODE}" >/etc/fishingbot.json
  fi
fi

exec "$@"
