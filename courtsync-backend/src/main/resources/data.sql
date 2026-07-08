-- Seed data for CourtSync
-- Run after spring.jpa.hibernate.ddl-auto=update creates the schema

-- Sports
INSERT IGNORE INTO sports (id, name, icon_name) VALUES
(1, 'Basketball', 'ic_basketball'),
(2, 'Football', 'ic_football'),
(3, 'Tennis', 'ic_tennis'),
(4, 'Padel', 'ic_padel'),
(5, 'Volleyball', 'ic_volleyball'),
(6, 'Badminton', 'ic_badminton');

-- Sport Halls (real venues in Sofia, Bulgaria)
-- Remove any previously-seeded halls that are not part of the current 4
DELETE FROM sport_halls WHERE id NOT IN (1, 2, 3, 4);

INSERT INTO sport_halls
  (id, name, description, address, city, district, latitude, longitude,
   price_per_hour, open_time, close_time, rating, review_count,
   image_url, hall_type, active, sport_id)
VALUES
(1,
 'Арена Исаев',
 'Арена Исаев е модерна баскетболна и волейболна арена с професионална дървена настилка, силно LED осветление и климатизирано пространство. Идеална за тренировки, турнири или приятелски мачове.',
 'ул. Жечо Гюмюшев 239, ж.к. Младост 3',
 'Sofia', 'Младост 3',
 42.639654, 23.386809,
 40.00, '07:00', '21:00', 4.5, 142,
 'https://images.unsplash.com/photo-1546519638405-a9f95a3b74c6?w=800',
 'INDOOR', true, 5),

(2,
 'Спортен комплекс "Мир и дружба"',
 'Спортен комплекс "Мир и дружба" разполага с изкуствени тревни игрища за футбол и хокей на трева, разположени в Студентски град. Подходящ за тренировки, приятелски мачове и турнири на открито.',
 'Студентски комплекс, ж.к. Студентски град 2',
 'Sofia', 'Студентски град',
 42.657485, 23.351058,
 50.00, '08:00', '23:00', 4.1, 607,
 'https://images.unsplash.com/photo-1529900748604-07564a03e7a6?w=800',
 'OUTDOOR', true, 2),

(3,
 'Тенис клуб "Про Спорт"',
 'Тенис клуб "Про Спорт" се намира на територията на Национална спортна академия "Васил Левски" в Студентски град. Разполага с покрити тенис кортове с професионална настилка, подходящи за тренировки през цялата година.',
 'Студентски комплекс, бул. "Климент Охридски" 22',
 'Sofia', 'Студентски град',
 42.646797, 23.352416,
 35.00, '08:00', '22:00', 4.5, 173,
 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=800',
 'INDOOR', true, 3),

(4,
 'Овергаз Арена',
 'Овергаз Арена е модерна спортна зала в близост до Американски колеж, ж.к. Младост. Разполага с професионален баскетболен паркет, трибуни и табло, подходяща за мачове, турнири и тренировки.',
 'в.з. Американски колеж, ул. "генерал-майор Васил Делов"',
 'Sofia', 'Младост',
 42.636460, 23.367829,
 45.00, '08:00', '22:00', 4.4, 246,
 'https://images.unsplash.com/photo-1546519638405-a9f95a3b74c6?w=800',
 'INDOOR', true, 1)
ON DUPLICATE KEY UPDATE
 name = VALUES(name),
 description = VALUES(description),
 address = VALUES(address),
 city = VALUES(city),
 district = VALUES(district),
 latitude = VALUES(latitude),
 longitude = VALUES(longitude),
 price_per_hour = VALUES(price_per_hour),
 open_time = VALUES(open_time),
 close_time = VALUES(close_time),
 rating = VALUES(rating),
 review_count = VALUES(review_count),
 image_url = VALUES(image_url),
 hall_type = VALUES(hall_type),
 active = VALUES(active),
 sport_id = VALUES(sport_id);
