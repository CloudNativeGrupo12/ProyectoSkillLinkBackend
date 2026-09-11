#!/bin/bash
set -uxo pipefail
dnf install -y java-21-amazon-corretto-headless >/tmp/dnf.log 2>&1
mkdir -p /opt/skilllink && touch /opt/skilllink/skilllink.env
cat > /etc/systemd/system/skilllink.service <<'UNIT'
[Unit]
Description=SkillLink API (Spring Boot Resource Server)
After=network-online.target
[Service]
WorkingDirectory=/opt/skilllink
EnvironmentFile=/opt/skilllink/skilllink.env
ExecStart=/usr/bin/java -Xmx700m -jar /opt/skilllink/app.jar
Restart=always
RestartSec=5
[Install]
WantedBy=multi-user.target
UNIT
cat > /opt/skilllink/sync.sh <<'SYNC'
#!/bin/bash
BUCKET="skilllink-artifacts-<cuenta>"
cd /opt/skilllink || exit 1
changed=0
for obj in app.jar skilllink.env; do
  etag=$(aws s3api head-object --bucket $BUCKET --key backend/$obj --query ETag --output text 2>/dev/null || echo none)
  if [ "$etag" != "none" ] && [ "$etag" != "$(cat .etag_$obj 2>/dev/null)" ]; then
    if aws s3 cp s3://$BUCKET/backend/$obj $obj.tmp --quiet; then mv $obj.tmp $obj; echo "$etag" > .etag_$obj; changed=1; fi
  fi
done
if [ $changed = 1 ] && [ -f app.jar ]; then systemctl daemon-reload; systemctl restart skilllink; fi
SYNC
chmod +x /opt/skilllink/sync.sh
cat > /etc/systemd/system/skilllink-sync.service <<'UNIT'
[Unit]
Description=Sincroniza artefactos de SkillLink desde S3
[Service]
Type=oneshot
ExecStart=/opt/skilllink/sync.sh
UNIT
cat > /etc/systemd/system/skilllink-sync.timer <<'UNIT'
[Unit]
Description=Ejecuta la sincronizacion cada 30s
[Timer]
OnBootSec=15
OnUnitActiveSec=30
AccuracySec=5
[Install]
WantedBy=timers.target
UNIT
systemctl daemon-reload
systemctl enable skilllink.service
systemctl enable --now skilllink-sync.timer
