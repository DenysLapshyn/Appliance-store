-- Insert Users
INSERT INTO users (id, name, surname, email, password, telephone_number, country, address, role_names) VALUES
(1, 'name1', 'surname1', 'email1@email.com', '$2a$10$X5wFKVqJ6e5VN6K7Zm1jXe6RGJKzJ4pZQT5yYxPqmYvN4iFqO6r2i', '11111111111', 'country1', 'address111', 'ROLE_USER'),
(2, 'name2', 'surname2', 'email2@email.com', '$2a$10$5J3h0ZQJ8rLKZ5JhTCKZR.4yJ5xW0WqZ5xJ5xJ5xJ5xJ5xJ5xJ5xa', '22222222222', 'country2', 'address222', 'ROLE_ADMIN'),
(3, 'name3', 'surname3', 'email3@email.com', '$2a$10$N3wJ5xJ5xJ5xJ5xJ5xJ5xuJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xJ5xm', '33333333333', 'country3', 'address333', 'ROLE_USER');

-- Reset user sequence
SELECT setval('users_id_seq', 3, true);

-- Insert Items
INSERT INTO items (id, name, price, amount, description, logo_name) VALUES
(1, 'Realme GT Master Edition 6/128GB Grey', 9999.0, 18, 'Realme mobile phone', 'logo1'),
(2, 'Lenovo IdeaPad 5 15ALC05 Graphite Grey', 24999.0, 8, 'Lenovo laptop', 'logo2'),
(3, 'Huawei FreeBuds 4i Silver Frost', 1749.0, 12, 'Huawei earphones', 'logo3');

-- Reset item sequence
SELECT setval('items_id_seq', 3, true);

-- Insert Item Images for Item 1
INSERT INTO item_image_names (item_id, image_names) VALUES
(1, 'image1.1'),
(1, 'image1.2'),
(1, 'image1.3'),
(1, 'image1.4');

-- Insert Item Specs for Item 1 (Realme GT Master Edition)
INSERT INTO item_specs (item_id, specs_key, specs) VALUES
(1, 'Release date', '18th August 2021'),
(1, 'RAM', '6GB'),
(1, 'Screen type', 'Super AMOLED'),
(1, 'Refresh rate', '120Hz'),
(1, 'Screen size (inches)', '6.43'),
(1, 'Brightness', '1000 nits'),
(1, 'Screen resolution', '1080x2400 pixels'),
(1, 'Aspect ratio', '20:9'),
(1, 'Processor', 'Qualcomm Snapdragon 778G'),
(1, 'Internal storage', '128GB'),
(1, 'Battery capacity (mAh)', '4300'),
(1, 'Charging', 'Fast charging 65W, 100% in 33 min'),
(1, 'Bluetooth', '5.2'),
(1, 'NFC', 'Yes'),
(1, 'USB Type-C', 'Yes'),
(1, 'Headphones', '3.5mm'),
(1, 'No. of Rear Cameras', '3'),
(1, 'Rear autofocus', 'Yes'),
(1, 'Rear flash', 'Yes'),
(1, 'Rear camera', '64-megapixel (f/1.8, 0.7-micron) + 8-megapixel (f/2.3, 1.12-micron) + 2-megapixel (f/2.4, 88.8-micron)'),
(1, 'Front camera', '32-megapixel (f/0.8)'),
(1, 'No. of Front Cameras', '1'),
(1, 'Operating system', 'Android 11'),
(1, 'Skin', 'Realme UI 2.0'),
(1, 'Dimensions (mm)', '159.20 x 73.50 x 8.00'),
(1, 'Weight (g)', '186.00'),
(1, 'Number of SIMs', '2'),
(1, 'SIM Type', 'Nano-SIM'),
(1, '5G', 'Yes'),
(1, 'In-Display Fingerprint Sensor', 'Yes'),
(1, 'Wi-Fi standards supported', '802.11 a/b/g/n/ac/ax'),
(1, 'GPS', 'Yes'),
(1, 'Stereo Sound', 'No'),
(1, 'IP Protection', 'No'),
(1, 'Kit items', 'smartphone, travel adapter, charging cable, SIM slot pin, protection case');

-- Insert Item Images for Item 2
INSERT INTO item_image_names (item_id, image_names) VALUES
(2, 'image2.1'),
(2, 'image2.2'),
(2, 'image2.3'),
(2, 'image2.4'),
(2, 'image2.5');

-- Insert Item Specs for Item 2 (Lenovo IdeaPad)
INSERT INTO item_specs (item_id, specs_key, specs) VALUES
(2, 'Processor', 'AMD Ryzen 5 5500U'),
(2, 'GPU', 'AMD Radeon Vega 7'),
(2, 'RAM', '16GB'),
(2, 'RAM frequency', '3200MGz'),
(2, 'RAM type', 'DDR4'),
(2, 'Screen size (inches)', '15.6'),
(2, 'Screen resolution', '1920x1080 (Full HD)'),
(2, 'Screen coverage', 'anti glare'),
(2, 'Pixels density', '141ppi'),
(2, 'Screen type', 'IPS'),
(2, 'Refresh rate', '60Hz'),
(2, 'Empty RAM slots', '0'),
(2, 'Data storage', 'SSD disc'),
(2, 'SSD size', '512GB'),
(2, 'SSD type', 'M.2 2242 PCIe 3.0x4 NVMe'),
(2, 'Bluetooth', '5.1'),
(2, 'Battery', '70Wh'),
(2, 'Dimensions (mm)', '17.9-19.9 х 356.67 x 233.13 '),
(2, 'Color', 'gray'),
(2, 'Material', 'plastic/aluminium'),
(2, 'Weight (kg) ', '1.66'),
(2, 'Kensington lock', 'No'),
(2, 'Video RAM', 'uses RAM'),
(2, 'HDMI', '1'),
(2, 'LAN', 'No'),
(2, 'USB', '2 х 3.2 Gen 1'),
(2, 'USB Type-C', '1 х 3.2 Gen 1'),
(2, 'Headphones', '3.5mm'),
(2, 'Card reader', 'Yes'),
(2, 'Card reader type', 'SD'),
(2, 'Web camera', '720p'),
(2, 'Microphone', 'Yes'),
(2, 'Audio system', 'Dolby Audio'),
(2, 'Wi-Fi standards supported', '802.11 a/b/g/n/ac/ax'),
(2, 'Kit items', 'laptop, travel adapter');

-- Insert Item Images for Item 3
INSERT INTO item_image_names (item_id, image_names) VALUES
(3, 'image3.1'),
(3, 'image3.2'),
(3, 'image3.3'),
(3, 'image3.4'),
(3, 'image3.5'),
(3, 'image3.6'),
(3, 'image3.7'),
(3, 'image3.8');

-- Insert Item Specs for Item 3 (Huawei FreeBuds)
INSERT INTO item_specs (item_id, specs_key, specs) VALUES
(3, 'Color', 'gray'),
(3, 'Case weight (g)', '36.5'),
(3, 'Earphone battery', '55mAh'),
(3, 'Case battery', '215mAh'),
(3, 'Bluetooth', '5.2'),
(3, 'Earphones type', 'TWS'),
(3, 'Microphones', '4'),
(3, 'Earphone dimensions (mm) height x width x depth', '37.5 x 21 x 23.9'),
(3, 'Earphone weight (g)', '5.5 g'),
(3, 'Case dimensions (mm) height x width x depth', '48 x 61.8 x 27.5'),
(3, 'Active noise cancellation', 'Yes'),
(3, 'Call noise cancellation', 'Yes'),
(3, 'Kit items', 'earbuds x2, charging case, silicone ear tips x3, USB-C charging cable');
