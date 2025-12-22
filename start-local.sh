#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 RushDeal MSA 로컬 환경 통합 실행을 시작합니다...${NC}"

# 1. 권한 부여
chmod +x gradlew

# 2. Gradle 빌드 (테스트는 스킵하여 속도 향상, 병렬 빌드 사용)
echo -e "${GREEN}📦 Spring Boot 프로젝트 빌드 중... (시간이 좀 걸립니다)${NC}"
./gradlew clean build -x test --parallel

# 빌드 실패 시 중단
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ 빌드에 실패했습니다. 에러 로그를 확인해주세요.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ 빌드 완료!${NC}"

# 3. .env 파일 존재 확인
if [ -f .env ]; then
    echo -e "${GREEN}[INFO] .env file found. Loading variables...${NC}"
    # .env 파일을 export하여 쉘 환경변수로 등록 (Docker Compose가 확실히 인식하도록)
    export $(cat .env | grep -v '#' | awk '/=/ {print $1}')
else
    echo -e "\033[0;31m[ERROR] .env file not found! Please create one based on the template.\033[0m"
    exit 1
fi

# 4. init-schemas.sql 파일 체크 (DB 초기화용)
if [ ! -f ./scripts/init-schemas.sql ]; then
    echo -e "\033[0;33m[WARNING] ./scripts/init-schemas.sql not found. Creating an empty one to prevent errors...${NC}"
    mkdir -p ./scripts
    touch ./scripts/init-schemas.sql
fi



# 5. 기존 컨테이너 정리
echo -e "${GREEN} 기존 컨테이너 및 네트워크 정리 중...${NC}"
docker-compose -f docker-compose-app.yml down

# 6. 도커 컴포즈 실행
echo -e "${GREEN} Docker Compose로 서비스 실행 중...${NC}"
# --build: 이미지 새로 빌드, -d: 백그라운드 실행
docker-compose -f docker-compose-app.yml --env-file .env up -d --build

# 5. 결과 확인
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✨ 모든 서비스가 실행 요청되었습니다!${NC}"
    echo -e "${GREEN}📜 로그를 확인하려면: docker-compose -f docker-compose-app.yml logs -f${NC}"
    echo "🔍 Checking status..."
    docker-compose -f docker-compose-app.yml ps
    docker-compose -f docker-compose-app.yml logs -f

    # 5초 뒤에 실행 중인 컨테이너 목록 출력
    sleep 5
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
else
    echo -e "${RED}❌ 도커 실행 중 문제가 발생했습니다.${NC}"
    exit 1
fi
