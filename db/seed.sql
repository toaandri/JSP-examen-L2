-- Seed data for Tinder-like JSP app
-- Must be run AFTER db/schema.sql

-- Interests
INSERT INTO interest(label) VALUES
('Musique'),
('Voyage'),
('Sport'),
('Cuisine'),
('Cinéma'),
('Lecture'),
('Jeux vidéo'),
('Tech'),
('Art'),
('Nature')
ON CONFLICT (label) DO NOTHING;

-- Users (password_hash will be replaced with real bcrypt by the app on first login if needed)
-- For MVP seeding, we use a clearly marked placeholder. The application will detect it.
INSERT INTO app_user(email, password_hash) VALUES
('alice@example.com', '{SEED}password'),
('bob@example.com', '{SEED}password'),
('chris@example.com', '{SEED}password'),
('dina@example.com', '{SEED}password'),
('enzo@example.com', '{SEED}password')
ON CONFLICT (email) DO NOTHING;

-- Profiles
INSERT INTO profile(user_id, first_name, last_name, birthdate, gender_identity, sexual_orientation, looking_for, bio, city, photo_url)
SELECT u.id, p.first_name, p.last_name, p.birthdate, p.gender_identity, p.sexual_orientation, p.looking_for, p.bio, p.city, p.photo_url
FROM (
    VALUES
      ('alice@example.com','Alice','R.','2003-06-12','Femme','Hétéro','Homme','Café, musées, playlists et couchers de soleil.','Antananarivo','https://picsum.photos/seed/alice/600/800'),
      ('bob@example.com','Bob','K.','2002-02-28','Homme','Hétéro','Femme','Team sport + ramen. Swipe si tu aimes voyager.','Antananarivo','https://picsum.photos/seed/bob/600/800'),
      ('chris@example.com','Chris','M.','2001-11-03','Non-binaire','Bi','Tous','Je cherche des vibes chill et des discussions deep.','Fianarantsoa','https://picsum.photos/seed/chris/600/800'),
      ('dina@example.com','Dina','S.','2003-09-21','Femme','Bi','Tous','Concerts, films, et randos le weekend.','Toamasina','https://picsum.photos/seed/dina/600/800'),
      ('enzo@example.com','Enzo','T.','2000-04-08','Homme','Gay','Homme','Tech, jeux, et bonne bouffe.','Antsirabe','https://picsum.photos/seed/enzo/600/800')
) AS p(email, first_name, last_name, birthdate, gender_identity, sexual_orientation, looking_for, bio, city, photo_url)
JOIN app_user u ON u.email = p.email
ON CONFLICT (user_id) DO NOTHING;

-- User interests
-- helper: map interest label to id
WITH i AS (SELECT id, label FROM interest),
u AS (SELECT id, email FROM app_user)
INSERT INTO user_interest(user_id, interest_id)
SELECT u.id, i.id
FROM (
  VALUES
    ('alice@example.com','Musique'),
    ('alice@example.com','Art'),
    ('alice@example.com','Voyage'),
    ('bob@example.com','Sport'),
    ('bob@example.com','Cuisine'),
    ('bob@example.com','Voyage'),
    ('chris@example.com','Lecture'),
    ('chris@example.com','Nature'),
    ('chris@example.com','Art'),
    ('dina@example.com','Cinéma'),
    ('dina@example.com','Musique'),
    ('dina@example.com','Nature'),
    ('enzo@example.com','Tech'),
    ('enzo@example.com','Jeux vidéo'),
    ('enzo@example.com','Cuisine')
) AS x(email, label)
JOIN u ON u.email = x.email
JOIN i ON i.label = x.label
ON CONFLICT DO NOTHING;

-- Some swipes (to demonstrate matches)
WITH u AS (SELECT id, email FROM app_user)
INSERT INTO swipe(from_user_id, to_user_id, decision)
SELECT a.id, b.id, s.decision
FROM (
  VALUES
    ('alice@example.com','bob@example.com','LIKE'),
    ('bob@example.com','alice@example.com','LIKE'), -- match Alice <-> Bob
    ('alice@example.com','chris@example.com','NOPE'),
    ('dina@example.com','bob@example.com','LIKE'),
    ('bob@example.com','dina@example.com','NOPE'),
    ('enzo@example.com','chris@example.com','LIKE'),
    ('chris@example.com','enzo@example.com','LIKE') -- match Enzo <-> Chris
) AS s(from_email, to_email, decision)
JOIN u a ON a.email = s.from_email
JOIN u b ON b.email = s.to_email
ON CONFLICT (from_user_id, to_user_id) DO NOTHING;

-- Create matches for mutual likes
-- Ensure ordering user_a_id < user_b_id
INSERT INTO app_match(user_a_id, user_b_id)
SELECT LEAST(s1.from_user_id, s1.to_user_id) AS user_a_id,
       GREATEST(s1.from_user_id, s1.to_user_id) AS user_b_id
FROM swipe s1
JOIN swipe s2
  ON s2.from_user_id = s1.to_user_id
 AND s2.to_user_id   = s1.from_user_id
WHERE s1.decision='LIKE' AND s2.decision='LIKE'
ON CONFLICT (user_a_id, user_b_id) DO NOTHING;

-- Notifications for matches
INSERT INTO notification(user_id, type, payload_json)
SELECT m.user_a_id, 'NEW_MATCH', json_build_object('matchId', m.id, 'withUserId', m.user_b_id)::text
FROM app_match m
ON CONFLICT DO NOTHING;

INSERT INTO notification(user_id, type, payload_json)
SELECT m.user_b_id, 'NEW_MATCH', json_build_object('matchId', m.id, 'withUserId', m.user_a_id)::text
FROM app_match m
ON CONFLICT DO NOTHING;

-- Messages for demo chats
WITH u AS (SELECT id, email FROM app_user)
INSERT INTO message(match_id, from_user_id, body)
SELECT m.id, sender.id, msg.body
FROM (
  VALUES
    ('alice@example.com','bob@example.com','Salut ! Tu recommandes quoi comme spot ramen ?'),
    ('bob@example.com','alice@example.com','Hey ! Celui près du centre, top. Et toi tu écoutes quoi en ce moment ?'),
    ('chris@example.com','enzo@example.com','Hello, ton setup gaming c’est quoi ?'),
    ('enzo@example.com','chris@example.com','PC + un peu de console. Et toi, plutôt story ou multi ?')
) AS msg(from_email, to_email, body)
JOIN u sender ON sender.email = msg.from_email
JOIN u receiver ON receiver.email = msg.to_email
JOIN app_match m
  ON (m.user_a_id = LEAST(sender.id, receiver.id) AND m.user_b_id = GREATEST(sender.id, receiver.id))
ON CONFLICT DO NOTHING;

