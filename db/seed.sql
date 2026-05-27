-- Donnees initiales

-- Interets
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
('Nature'),
('Fitness'),
('Running'),
('Yoga'),
('Danse'),
('Basket'),
('Football'),
('Natation'),
('Surf'),
('Escalade'),
('Randonnée'),
('Cyclisme'),
('Photographie'),
('Peinture'),
('Dessin'),
('Théâtre'),
('Podcasts'),
('Anime'),
('Séries TV'),
('Documentaires'),
('Science-fiction'),
('Horreur'),
('Comédie'),
('Romance'),
('Manga'),
('Echecs'),
('Cartes'),
('Jeux de société'),
('Esport'),
('Streaming'),
('Programmation'),
('IA'),
('Startups'),
('Entrepreneuriat'),
('UX Design'),
('Marketing'),
('Finance'),
('Crypto'),
('Bricolage'),
('Jardinage'),
('Cuisine asiatique'),
('Pâtisserie'),
('Café'),
('Vin'),
('Street food'),
('BBQ'),
('Végétarien'),
('Mode'),
('Maquillage'),
('Skincare'),
('Tatouage'),
('Moto'),
('Voiture'),
('Minimalisme'),
('Méditation'),
('Spiritualité'),
('Développement personnel'),
('Langues'),
('Histoire'),
('Politique'),
('Bénévolat'),
('Animaux'),
('Chiens'),
('Chats'),
('Astronomie'),
('Physique'),
('Biologie'),
('Géographie'),
('K-pop'),
('Rap'),
('Rock'),
('Afrobeat'),
('Jazz'),
('Piano'),
('Guitare'),
('DJing'),
('Voyage sac à dos'),
('Road trip'),
('Camping'),
('Plage'),
('Montagne'),
('Architecture'),
('Calligraphie'),
('Memes'),
('TikTok'),
('YouTube'),
('Slow life'),
('Business'),
('Data science'),
('DevOps'),
('Cloud'),
('Cybersécurité'),
('Productivité')
ON CONFLICT (label) DO NOTHING;

-- Comptes de test
INSERT INTO app_user(email, password_hash) VALUES
('alice@example.com', '{SEED}password'),
('bob@example.com', '{SEED}password'),
('chris@example.com', '{SEED}password'),
('dina@example.com', '{SEED}password'),
('enzo@example.com', '{SEED}password')
ON CONFLICT (email) DO NOTHING;

-- Profils
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

-- Questions onboarding
INSERT INTO onboarding_question(question_key, label, question_order) VALUES
('fav_movie_genre','Ton genre de film prefere ?',1),
('fav_music_style','Ton style de musique prefere ?',2),
('weekend_vibe','Ton weekend ideal ?',3),
('dating_style','Ton style de relation ?',4),
('social_energy','Tu es plutot ?',5)
ON CONFLICT (question_key) DO NOTHING;

WITH q AS (SELECT id, question_key FROM onboarding_question)
INSERT INTO onboarding_option(question_id, label, score_tag, option_order)
SELECT q.id, x.label, x.score_tag, x.option_order
FROM (
  VALUES
    ('fav_movie_genre','Science-fiction','movie_scifi',1),
    ('fav_movie_genre','Comedie','movie_comedy',2),
    ('fav_movie_genre','Romance','movie_romance',3),
    ('fav_movie_genre','Thriller','movie_thriller',4),
    ('fav_movie_genre','Anime','movie_anime',5),
    ('fav_music_style','Afrobeat','music_afrobeat',1),
    ('fav_music_style','Rap','music_rap',2),
    ('fav_music_style','Rock','music_rock',3),
    ('fav_music_style','Pop','music_pop',4),
    ('fav_music_style','Jazz','music_jazz',5),
    ('weekend_vibe','Sortir et bouger','weekend_outdoor',1),
    ('weekend_vibe','Cinema/series chill','weekend_chill',2),
    ('weekend_vibe','Sport intensif','weekend_sport',3),
    ('weekend_vibe','Cuisine et maison','weekend_home',4),
    ('weekend_vibe','Road trip improvise','weekend_trip',5),
    ('dating_style','Serieux','dating_serious',1),
    ('dating_style','Flexible','dating_flexible',2),
    ('dating_style','Decouverte','dating_explore',3),
    ('dating_style','Amitie + affinite','dating_friendship',4),
    ('dating_style','Fun et leger','dating_fun',5),
    ('social_energy','Introverti','energy_introvert',1),
    ('social_energy','Ambiverti','energy_ambivert',2),
    ('social_energy','Extraverti','energy_extravert',3)
) AS x(question_key, label, score_tag, option_order)
JOIN q ON q.question_key = x.question_key
ON CONFLICT (question_id, label) DO NOTHING;

WITH u AS (SELECT id, email FROM app_user),
q AS (SELECT id, question_key FROM onboarding_question),
o AS (SELECT id, question_id, label FROM onboarding_option)
INSERT INTO user_onboarding_answer(user_id, question_id, option_id)
SELECT u.id, q.id, o.id
FROM (
  VALUES
    ('alice@example.com','fav_movie_genre','Romance'),
    ('alice@example.com','fav_music_style','Pop'),
    ('alice@example.com','weekend_vibe','Cinema/series chill'),
    ('alice@example.com','dating_style','Serieux'),
    ('alice@example.com','social_energy','Ambiverti'),
    ('bob@example.com','fav_movie_genre','Comedie'),
    ('bob@example.com','fav_music_style','Rap'),
    ('bob@example.com','weekend_vibe','Sport intensif'),
    ('bob@example.com','dating_style','Flexible'),
    ('bob@example.com','social_energy','Extraverti'),
    ('chris@example.com','fav_movie_genre','Science-fiction'),
    ('chris@example.com','fav_music_style','Jazz'),
    ('chris@example.com','weekend_vibe','Cinema/series chill'),
    ('chris@example.com','dating_style','Decouverte'),
    ('chris@example.com','social_energy','Introverti'),
    ('dina@example.com','fav_movie_genre','Anime'),
    ('dina@example.com','fav_music_style','Afrobeat'),
    ('dina@example.com','weekend_vibe','Road trip improvise'),
    ('dina@example.com','dating_style','Amitie + affinite'),
    ('dina@example.com','social_energy','Ambiverti'),
    ('enzo@example.com','fav_movie_genre','Thriller'),
    ('enzo@example.com','fav_music_style','Rock'),
    ('enzo@example.com','weekend_vibe','Cuisine et maison'),
    ('enzo@example.com','dating_style','Serieux'),
    ('enzo@example.com','social_energy','Introverti')
) AS x(email, question_key, option_label)
JOIN u ON u.email = x.email
JOIN q ON q.question_key = x.question_key
JOIN o ON o.question_id = q.id AND o.label = x.option_label
ON CONFLICT (user_id, question_id) DO NOTHING;

-- Interets utilisateur
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

-- Swipes de demonstration
WITH u AS (SELECT id, email FROM app_user)
INSERT INTO swipe(from_user_id, to_user_id, decision)
SELECT a.id, b.id, s.decision
FROM (
  VALUES
    ('alice@example.com','bob@example.com','LIKE'),
    ('bob@example.com','alice@example.com','LIKE'),
    ('alice@example.com','chris@example.com','NOPE'),
    ('dina@example.com','bob@example.com','LIKE'),
    ('bob@example.com','dina@example.com','NOPE'),
    ('enzo@example.com','chris@example.com','LIKE'),
    ('chris@example.com','enzo@example.com','LIKE')
) AS s(from_email, to_email, decision)
JOIN u a ON a.email = s.from_email
JOIN u b ON b.email = s.to_email
ON CONFLICT (from_user_id, to_user_id) DO NOTHING;

-- Matches mutuels
INSERT INTO app_match(user_a_id, user_b_id)
SELECT LEAST(s1.from_user_id, s1.to_user_id) AS user_a_id,
       GREATEST(s1.from_user_id, s1.to_user_id) AS user_b_id
FROM swipe s1
JOIN swipe s2
  ON s2.from_user_id = s1.to_user_id
 AND s2.to_user_id   = s1.from_user_id
WHERE s1.decision='LIKE' AND s2.decision='LIKE'
ON CONFLICT (user_a_id, user_b_id) DO NOTHING;

-- Notifications
INSERT INTO notification(user_id, type, payload_json)
SELECT m.user_a_id, 'NEW_MATCH', json_build_object('matchId', m.id, 'withUserId', m.user_b_id)::text
FROM app_match m
ON CONFLICT DO NOTHING;

INSERT INTO notification(user_id, type, payload_json)
SELECT m.user_b_id, 'NEW_MATCH', json_build_object('matchId', m.id, 'withUserId', m.user_a_id)::text
FROM app_match m
ON CONFLICT DO NOTHING;

-- Messages de demonstration
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

