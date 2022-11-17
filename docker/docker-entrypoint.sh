#!/usr/bin/env bash
if [ "$1" = 'fishing-bot' ]; then
  mkdir -p /var/log/fishingbot/
  if [ ! -f /usr/lib/fishingbot/data/config.json ]; then
    echo "No config file was found! Creating a new config file".
    rm 2>/dev/null 1>&2 -rf /tmp/fishingbot-init/logs
    mkdir -p /tmp/fishingbot-init/logs
    /usr/bin/fishing-bot -nogui -logsdir /tmp/fishingbot-init/logs -config /tmp/fishingbot-init/config.json -onlyCreateConfig
    jq ".server.ip = \"${MC_SERVER}\"" /tmp/fishingbot-init/config.json |
      jq ".server.port = ${MC_PORT}" |
      jq ".server.\"default-protocol\" = \"${MC_PROTOCOL}\"" |
      jq ".server.\"online-mode\" = ${MC_ONLINE_MODE}" >/usr/lib/fishingbot/data/config.json
  fi
fi

exec "$@"
