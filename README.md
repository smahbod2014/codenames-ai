# Codenames

Coded entirely using gemini cli.

Play it at https://codenames-ai-dusky.vercel.app

## How to run it

### Backend
```
cd backend
./mvnw -f pom.xml spring-boot:run
```

### Frontend

```
cd frontend
npm run dev
```

## Build Backend with Docker

```
cd backend
docker build -t codenames-ai:latest .
docker run --rm -p 8080:8080 codenames-ai:latest
```