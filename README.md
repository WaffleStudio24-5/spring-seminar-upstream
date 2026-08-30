# backend-seminar-upstream
백엔드 세미나 과제용 레포

## 자동 채점 설정

GitHub Actions variable에 다음 값을 설정하면 `main` push 시 채점 서버를 호출합니다.

- `JUDGE_URL`: 채점 서버 주소
- `ASSIGNMENT`: upstream 과제 tag (예: `assignment-1-v1`)
