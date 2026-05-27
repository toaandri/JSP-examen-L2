# Tinderish JSP

Application web JSP/Servlet (Tomcat 10+, Jakarta EE) avec:
- inscription/connexion
- onboarding en etapes avec cartes swipe
- matching en pourcentage
- likes/dislikes
- matches, notifications, chat
- base PostgreSQL

## Stack

- Java 11
- Maven (WAR)
- Tomcat 10+
- JSP + JSTL Jakarta
- PostgreSQL
- JDBC

## Prerequis

- JDK 11+
- Maven 3.9+
- PostgreSQL 13+
- Eclipse IDE (Enterprise Java) + serveur Tomcat 10 configure

## Configuration base de donnees

L'application lit ces variables d'environnement:

- `APP_DB_URL` (defaut: `jdbc:postgresql://localhost:5432/tinder_jsp`)
- `APP_DB_USER` (defaut: `postgres`)
- `APP_DB_PASSWORD` (defaut: `postgres`)

Exemple (PowerShell):

```powershell
$env:APP_DB_URL="jdbc:postgresql://localhost:5432/tinder_jsp"
$env:APP_DB_USER="postgres"
$env:APP_DB_PASSWORD="postgres"
```

## Initialisation SQL

1. Creer la base:

```sql
CREATE DATABASE tinder_jsp;
```

2. Executer, dans cet ordre:
- `db/schema.sql`
- `db/seed.sql`

## Lancer en local

### Via Maven

```bash
mvn clean package
```

Le WAR est genere dans `target/jsp-examen-l2.war`.

### Via Eclipse

1. `File > Import > Maven > Existing Maven Projects`
2. Selectionner ce dossier
3. Associer le projet a Tomcat 10+
4. `Run As > Run on Server`

URL typique:

`http://localhost:8080/jsp-examen-l2/`

## Comptes seed

Utilisateurs:
- `alice@example.com`
- `bob@example.com`
- `chris@example.com`
- `dina@example.com`
- `enzo@example.com`

Mot de passe seed:

`password`

Le format seed est `'{SEED}password'`. Lors de la premiere connexion, l'application remplace cette valeur par un hash BCrypt.

## Parcours utilisateur

1. Creation de compte
2. Onboarding:
   - identite
   - genre/orientation
   - cartes swipe centres d'interet
   - cartes swipe preferences (films, musique, etc.)
   - bio + ville
3. Swipe profils (NOPE/LIKE)
4. Match si like mutuel
5. Ouverture chat depuis la page matches

## Routes principales

- `/auth/register`
- `/auth/login`
- `/app/swipe`
- `/app/matches`
- `/app/notifications`
- `/app/chat?matchId=...`
- `/app/settings`

## Notes

- Projet en `jakarta.*` (Tomcat 10+).
- Si le schema change, rejouer `db/schema.sql` puis `db/seed.sql`.
- Build de verification:

```bash
mvn clean package
```

