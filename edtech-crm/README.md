# EdTech CRM & Inquiry System

A full-stack inquiry management application for educational institutions. Public users can submit inquiries; administrators can sign in, search/filter inquiries, review details, and update inquiry status.

## Technology stack

- **Frontend:** React 18, TypeScript, Vite, React Router
- **Backend:** Java 21, Spring Boot 3.3, Spring Security, Spring Data JPA
- **Database:** PostgreSQL (Neon or another PostgreSQL-compatible provider)
- **Authentication:** Stateless JWT with BCrypt password hashing
- **Schema management:** Flyway migration on backend startup; standalone SQL at `database/schema.sql`

## Repository structure

```text
.
├── backend/                 # Spring Boot REST API
│   ├── pom.xml
│   └── src/main/resources/
│       ├── application.yml
│       └── db/migration/    # Flyway migrations
├── frontend/                # React + TypeScript app
├── database/schema.sql      # Standalone PostgreSQL schema
├── postman/                 # Postman request collection
├── .env.example             # Environment variable template
├── render.yaml              # Render backend service blueprint
└── README.md
```

## Prerequisites

- JDK 21 or newer
- Maven 3.8+
- Node.js 18+ and npm
- A PostgreSQL database (Neon is supported)

## 1. Configure the database

Create a PostgreSQL database in Neon (or another PostgreSQL provider). Copy its connection URI. For Neon, it normally resembles:

```text
postgresql://USER:PASSWORD@HOST/DBNAME?sslmode=require
```

At the project root, copy `.env.example` to `.env` and set your own values. The backend's environment loader reads the root `.env` during local development. Keep `.env` private and never commit it.

```bash
# Windows PowerShell
Copy-Item .env.example .env

# macOS/Linux
cp .env.example .env
```

The application supports `DATABASE_URL` as a standard PostgreSQL connection URI. It converts this URI into JDBC settings at startup. You can alternatively set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` separately.

### Database schema

The backend runs Flyway migrations from `backend/src/main/resources/db/migration` on startup. For manual setup or schema review, run `database/schema.sql` in your PostgreSQL provider's SQL editor. Do not run a conflicting manual schema against an already migrated database unless you understand the existing schema state.

Tables:

- `admins`: administrator account and BCrypt password hash
- `inquiries`: public inquiry submissions, categories, contact details, and status

## 2. Start the backend

From the repository root:

```bash
cd backend
mvn spring-boot:run
```

The backend defaults to `http://localhost:8080`.

Relevant backend environment variables:

| Variable | Purpose |
|---|---|
| `DATABASE_URL` | PostgreSQL URI, including SSL settings |
| `DB_URL` | Alternative JDBC URL |
| `DB_USERNAME` / `DB_PASSWORD` | Optional separate database credentials |
| `JWT_SECRET` | Secret used to sign JWTs; use a strong, private value at least 32 characters long |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds |
| `FRONTEND_ORIGINS` | Comma-separated allowed frontend origins |
| `SERVER_PORT` | Backend port; hosting providers may supply `PORT` instead |
| `ADMIN_NAME` / `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Bootstrap an initial administrator if that email is not already present |

The bootstrap service creates the admin only if the configured email does not already exist. It hashes the password with BCrypt and does not overwrite an existing account's password on subsequent starts.

## 3. Start the frontend

In a second terminal, from the repository root:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

Set `VITE_API_BASE_URL` in `frontend/.env.local` if the backend API is not at its default address. Example:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Only public frontend settings should use the `VITE_` prefix. Never put database credentials or JWT signing secrets in frontend environment variables.

## 4. Admin login and custom JWT authentication

1. Configure `ADMIN_EMAIL` and `ADMIN_PASSWORD` in the backend environment before first startup.
2. Open the admin login screen and sign in with those configured credentials.
3. The backend verifies the password against the BCrypt hash stored in `admins` and returns a signed JWT.
4. The frontend keeps the token in memory and sends it as `Authorization: Bearer <token>` for protected requests.
5. The API is stateless; there is no public administrator registration endpoint.

For the current local-development configuration, the defaults in `backend/src/main/resources/application.yml` may be used if you have not overridden them. Change these defaults before public deployment and set a unique production `JWT_SECRET`.

## 5. Build commands

Frontend production build:

```bash
cd frontend
npm run build
```

Backend package:

```bash
cd backend
mvn clean package -DskipTests
```

The backend JAR is created under `backend/target/`.

## 6. Deployment

### Backend on Render

A `render.yaml` blueprint is included. In Render, create a new Blueprint from the repository and configure the secret environment values when prompted. Ensure `DATABASE_URL` points to the PostgreSQL database and `FRONTEND_ORIGINS` contains the exact deployed frontend origin (for example, `https://your-app.vercel.app`). Do not use a placeholder URL in production.

If configuring the Web Service manually:

- **Root directory:** repository root
- **Build command:** `mvn -f backend/pom.xml clean package -DskipTests`
- **Start command:** `java -jar backend/target/edtech-crm-backend-1.0.0.jar`
- **Runtime:** Java 21

The backend uses the hosting platform's `PORT` when supplied; otherwise it defaults to 8080.

### Frontend on Vercel

1. Import the GitHub repository into Vercel.
2. Set **Root Directory** to `frontend`.
3. Set **Build Command** to `npm run build` and **Output Directory** to `dist`.
4. Add `VITE_API_BASE_URL=https://YOUR-BACKEND-HOST/api` in project environment variables.
5. Deploy, then add the exact deployed frontend URL to backend `FRONTEND_ORIGINS` and redeploy the backend.

The included `frontend/vercel.json` configures SPA route rewrites. A live URL only exists after the deployment succeeds; add the actual URLs here after deploying:

- **Frontend demo:** _Add deployed Vercel URL_
- **Backend base URL:** _Add deployed Render URL_
- **GitHub repository:** _Add repository URL_

## 7. Postman collection

Import `postman/EdTech-CRM.postman_collection.json` into Postman to view the included API requests. API verification is not part of this submission-finalization step, so the collection is provided as a testing aid rather than a claim that every request was recently run.

## 8. How AI was used

AI assistance was used to help plan the application structure, draft configuration/documentation, and review implementation patterns. The code and configuration should be reviewed by the developer before submission; AI-generated suggestions are not a substitute for understanding the implementation.

## Security notes

- Never commit `.env` files, production database URLs, passwords, or JWT secrets.
- Configure strong production values in the hosting provider's environment-variable settings.
- Do not expose database credentials through `VITE_` frontend variables.
- The default local admin credentials are for development only; replace them before deployment.
