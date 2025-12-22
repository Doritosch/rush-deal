#!/bin/bash

echo -e "\033[0;34m[INFO] Stopping RushDeal Services...\033[0m"

# 컨테이너 중지 및 제거
docker-compose -f docker-compose-app.yml down

# (선택사항) 볼륨까지 싹 지우고 싶다면 아래 명령어를 사용하세요 (데이터 초기화)
# docker-compose -f docker-compose-app.yml down -v

echo -e "\033[0;32m[INFO] All containers stopped and removed.\033[0m"
