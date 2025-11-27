# Contributing Guide

본 문서는 RushDeal 프로젝트 개발 협업 규칙을 정의합니다.

---

## 📌 Git Branch 전략

RushDeal은 **Git Flow 전략**을 기반으로 진행합니다.

| 브랜치 | 용도 |
|--------|------|
| `main` | 서비스 **최신 배포 브랜치** |
| `develop` | feature 브랜치가 병합되는 **개발 통합 브랜치** |
| `feature/*` | 기능별 개발 브랜치 (`develop`에서 분기) |

---

## 📌 브랜치 네이밍 규칙


예시:

`feat/auth/42` <br>
`fix/order/108`


---

## 📌 Pull Request 규칙

- 리뷰어 **2명 이상 승인 필수**
- 머지 방식: **기본 Merge 방식 사용**
- **PR 단위는 작고 기능 단위로 분리**

PR 메시지 예시:

`[feat/auth] 로그인 기능 추가`


---

## 📌 Commit Message Convention

| 타입 | 의미 |
|------|------|
| `Feat` | 새로운 기능 추가 |
| `Fix` | 버그 수정 |
| `Docs` | 문서 변경 |
| `Style` | 코드 포맷 수정(기능 영향 없음) |
| `Refactor` | 코드 리팩토링 |
| `Test` | 테스트 코드 추가/수정 |
| `Chore` | 빌드 및 패키지 설정 변경 등 기타 작업 |
| `Design` | UI/CSS 관련 변경 |
| `Comment` | 주석 추가/수정 |
| `Rename` | 파일/폴더 명 변경 |
| `Remove` | 파일 삭제 |
| `!HOTFIX` | 긴급 배포용 심각 버그 수정 |
| `Config` | 프로젝트 공통 설정 추가 |
| `Init` | 프로젝트 초기 셋업 |
| `WIP` | 작업 중 (Work In Progress) |

### Commit Format

`<type>(<domain>): <message>`

예: `feat(auth): 회원가입 기능 구현`


---

## 🔚 규칙 변경

본 문서 규칙은 필요 시 변경될 수 있으며, 변경 시 팀 합의 후 반영합니다.
