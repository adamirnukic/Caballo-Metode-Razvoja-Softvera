INSERT INTO tables (broj_stola, broj_sjedista)
VALUES
    (1, 2),
    (2, 2),
    (3, 4),
    (4, 4),
    (5, 4),
    (6, 6),
    (7, 6),
    (8, 8),
    (9, 8),
    (10, 10)
    AS new_vals(broj_stola, broj_sjedista)
ON DUPLICATE KEY UPDATE
                     broj_sjedista = new_vals.broj_sjedista;


INSERT INTO users (id, username, password, salt, role)
VALUES
    (1,
     'Admin',
     '9c731443f43c6fccc94027eb38fa7e64db1665b8553f0f936abf15572c7c23c8',
     '91d20a12b3c44038c6b21e711d729ef0',
     'admin'),
    (2,
     'Adamir',
     '611ce4f749f25a1a398d36996dea7a26c9b0085c80809840a0a9ac729cdb2c5f',
     '485f2d7ccf9adafe2481ffab119fc3c4',
     'user'),
    (3,
     'Abida',
     '743ddb2c59e625a3d1fc96ce2d22b43c150987e96b46efe85cebc9858433b83d',
     'a35d2aa96b5b145b7655449aeb8c06be',
     'user'),
    (4,
     'Asja',
     '409759858858514722fc76299944a2be06cbbd8787282394aa1a6d0cd218c134',
     '168dbf380c3e931437d3bc143fbbf4e7',
     'admin')
    AS new_users(id, username, password, salt, role)
ON DUPLICATE KEY UPDATE
                     username = new_users.username,
                     password = new_users.password,
                     salt     = new_users.salt,
                     role     = new_users.role;


INSERT INTO shifts (user_id, datum, tip_smjene, napomena)
VALUES
    (1, '2025-12-22', 'slobodan', 'Seed (ponedjeljak)'),
    (2, '2025-12-22', 'slobodan', 'Seed (ponedjeljak)'),
    (3, '2025-12-22', 'slobodan', 'Seed (ponedjeljak)'),
    (4, '2025-12-22', 'slobodan', 'Seed (ponedjeljak)')
    AS new_shifts(user_id, datum, tip_smjene, napomena)
ON DUPLICATE KEY UPDATE
                     tip_smjene = new_shifts.tip_smjene,
                     napomena   = new_shifts.napomena;


INSERT INTO menu_items (id, naziv, opis, cijena, kategorija, item_type, is_active)
VALUES
-- PASTA
(1, 'Spaghetti Arabiatta', 'Ljuta pasta', 8.00, 'Pasta', 'FOOD', 1),
(2, 'Spaghetti Aglio, Olio e Peperoncino', 'Klasična pasta', 8.00, 'Pasta', 'FOOD', 1),
(3, 'Spaghetti Alla Carbonara', 'Kremasta pasta', 9.00, 'Pasta', 'FOOD', 1),
(4, 'Spaghetti Quattro Formaggi', 'Pasta sa sirom', 8.00, 'Pasta', 'FOOD', 1),
(5, 'Spaghetti Al Frutti di Mare', 'Pasta sa morskim plodovima', 12.00, 'Pasta', 'FOOD', 1),
(6, 'Spaghetti Al Pomodoro', 'Paradajz pasta', 7.00, 'Pasta', 'FOOD', 1),

(7, 'Penne Al Tono', 'Pasta sa tunom', 9.00, 'Pasta', 'FOOD', 1),
(8, 'Penne Caballo', 'Pasta sa piletinom', 9.00, 'Pasta', 'FOOD', 1),
(9, 'Penne Dello Chef', 'Specijalitet kuće', 9.00, 'Pasta', 'FOOD', 1),
(10, 'Penne Bistecca', 'Pasta sa biftekom', 15.00, 'Pasta', 'FOOD', 1),

(11, 'Tagliatelle Con Panna e Verdura', 'Krem pasta', 8.00, 'Pasta', 'FOOD', 1),
(12, 'Tagliatelle Al Funghi', 'Pasta sa gljivama', 8.00, 'Pasta', 'FOOD', 1),
(13, 'Tagliatelle Al Salmone', 'Pasta sa lososom', 12.00, 'Pasta', 'FOOD', 1),

(14, 'Lasagna Ragu', 'Lasagne sa mesom', 9.00, 'Pasta', 'FOOD', 1),

-- PIZZA MALA
(15, 'Pizza Margarita Mala', 'Klasična pizza', 6.00, 'Pizza', 'FOOD', 1),
(16, 'Pizza Salami Mala', 'Pizza sa šunkom', 7.00, 'Pizza', 'FOOD', 1),
(17, 'Pizza Funghi Mala', 'Pizza sa gljivama', 7.00, 'Pizza', 'FOOD', 1),
(18, 'Pizza Capricciosa Mala', 'Pizza specijal', 8.00, 'Pizza', 'FOOD', 1),
(19, 'Pizza Quattro Stagione Mala', 'Miješana pizza', 9.00, 'Pizza', 'FOOD', 1),
(20, 'Pizza Quattro Formaggi Mala', 'Pizza sa sirom', 9.00, 'Pizza', 'FOOD', 1),
(21, 'Pizza Mexico Mala', 'Ljuta pizza', 8.00, 'Pizza', 'FOOD', 1),
(22, 'Pizza Al Tonno Mala', 'Pizza sa tunom', 9.00, 'Pizza', 'FOOD', 1),
(23, 'Pizza Frutti di Mare Mala', 'Pizza sa plodovima mora', 10.00, 'Pizza', 'FOOD', 1),
(24, 'Pizza Specialita di Casa Mala', 'Specijal kuće', 10.00, 'Pizza', 'FOOD', 1),
(25, 'Pizza Vegetariana Mala', 'Vegetarijanska pizza', 7.00, 'Pizza', 'FOOD', 1),
(26, 'Pizza Al Salmone Mala', 'Pizza sa lososom', 10.00, 'Pizza', 'FOOD', 1),
(27, 'Pizza Napolitana Mala', 'Pizza sa inćunima', 10.00, 'Pizza', 'FOOD', 1),
(28, 'Pizza Diavolo Mala', 'Ljuta pizza', 7.00, 'Pizza', 'FOOD', 1),

-- PIZZA VELIKA
(29, 'Pizza Margarita Velika', 'Klasična pizza', 8.00, 'Pizza', 'FOOD', 1),
(30, 'Pizza Salami Velika', 'Pizza sa šunkom', 9.00, 'Pizza', 'FOOD', 1),
(31, 'Pizza Funghi Velika', 'Pizza sa gljivama', 9.00, 'Pizza', 'FOOD', 1),
(32, 'Pizza Capricciosa Velika', 'Pizza specijal', 10.00, 'Pizza', 'FOOD', 1),
(33, 'Pizza Quattro Stagione Velika', 'Miješana pizza', 11.00, 'Pizza', 'FOOD', 1),
(34, 'Pizza Quattro Formaggi Velika', 'Pizza sa sirom', 11.00, 'Pizza', 'FOOD', 1),
(35, 'Pizza Mexico Velika', 'Ljuta pizza', 10.00, 'Pizza', 'FOOD', 1),
(36, 'Pizza Al Tonno Velika', 'Pizza sa tunom', 11.00, 'Pizza', 'FOOD', 1),
(37, 'Pizza Frutti di Mare Velika', 'Pizza sa plodovima mora', 12.00, 'Pizza', 'FOOD', 1),
(38, 'Pizza Specialita di Casa Velika', 'Specijal kuće', 12.00, 'Pizza', 'FOOD', 1),
(39, 'Pizza Vegetariana Velika', 'Vegetarijanska pizza', 9.00, 'Pizza', 'FOOD', 1),
(40, 'Pizza Al Salmone Velika', 'Pizza sa lososom', 12.00, 'Pizza', 'FOOD', 1),
(41, 'Pizza Napolitana Velika', 'Pizza sa inćunima', 12.00, 'Pizza', 'FOOD', 1),
(42, 'Pizza Diavolo Velika', 'Ljuta pizza', 9.00, 'Pizza', 'FOOD', 1),

(43, 'Focaccia', 'Pecivo', 3.00, 'Pizza', 'FOOD', 1),

-- GLAVNA JELA
(44, 'Calamari Alla Griglia 200g', 'Grilovani lignji', 22.00, 'Glavno jelo', 'FOOD', 1),
(45, 'Filetto di Salmone 150g', 'Losos na žaru', 22.00, 'Glavno jelo', 'FOOD', 1),
(46, 'Filetto al Burro 200g', 'Ramstek na žaru', 20.00, 'Glavno jelo', 'FOOD', 1),
(47, 'Filetto al Pepe 200g', 'Ramstek u sosu', 22.00, 'Glavno jelo', 'FOOD', 1),
(48, 'Filetto di Tacchino 200g', 'Pureći file', 20.00, 'Glavno jelo', 'FOOD', 1),
(49, 'Filetto alla Stroganoff 200g', 'Ramstek stroganoff', 22.00, 'Glavno jelo', 'FOOD', 1),
(50, 'Filetto di Pollo 200g', 'Pileći file', 13.00, 'Glavno jelo', 'FOOD', 1),
(51, 'Filetto di Pollo u Susamu 200g', 'Pileći file', 14.00, 'Glavno jelo', 'FOOD', 1),
(52, 'Verdura Alla Griglia', 'Grilovano povrće', 9.00, 'Glavno jelo', 'FOOD', 1),
(53, 'Steak House 200g', 'Biftek', 35.00, 'Glavno jelo', 'FOOD', 1),
(54, 'Frittata', 'Omlet', 6.00, 'Glavno jelo', 'FOOD', 1),

-- SALATE
(55, 'Insalata Mista', 'Miješana salata', 6.00, 'Salata', 'FOOD', 1),
(56, 'Insalata con Mozzarella e Pomodoro', 'Salata sa sirom', 10.00, 'Salata', 'FOOD', 1),
(57, 'Insalata al Tonno', 'Salata sa tunom', 10.00, 'Salata', 'FOOD', 1),
(58, 'Insalata con Petto di Pollo', 'Salata sa piletinom', 10.00, 'Salata', 'FOOD', 1),
(59, 'Insalata Frutti di Mare', 'Salata sa morskim plodovima', 12.00, 'Salata', 'FOOD', 1),
(60, 'Insalata con Petto di Tacchino', 'Salata sa puretinom', 12.00, 'Salata', 'FOOD', 1),
(61, 'Insalata con Petto di Manzo', 'Salata sa ramstekom', 13.00, 'Salata', 'FOOD', 1),
(62, 'Insalata di Bistecca', 'Salata sa biftekom', 15.00, 'Salata', 'FOOD', 1),
(63, 'Insalata Caprese Mala', 'Caprese salata', 10.00, 'Salata', 'FOOD', 1),
(64, 'Insalata Caprese Velika', 'Caprese salata', 14.00, 'Salata', 'FOOD', 1),
(65, 'Afettati e Formaggi Mala', 'Sirna plata', 10.00, 'Salata', 'FOOD', 1),
(66, 'Afettati e Formaggi Velika', 'Sirna plata', 14.00, 'Salata', 'FOOD', 1)

    AS new_food(id, naziv, opis, cijena, kategorija, item_type, is_active)
ON DUPLICATE KEY UPDATE
                     naziv = new_food.naziv,
                     opis = new_food.opis,
                     cijena = new_food.cijena,
                     kategorija = new_food.kategorija,
                     item_type = new_food.item_type,
                     is_active = new_food.is_active;


INSERT INTO menu_items (id, naziv, opis, cijena, kategorija, item_type, current_qty, is_active)
VALUES
-- TOPLI NAPICI
(67, 'Espresso kafa', 'Topla kafa', 2.50, 'Topli napici', 'DRINK', 100, 1),
(68, 'Espresso kafa sa šlagom', 'Topla kafa', 2.50, 'Topli napici', 'DRINK', 100, 1),
(69, 'Espresso kafa sa mlijekom', 'Topla kafa', 2.50, 'Topli napici', 'DRINK', 100, 1),
(70, 'Nescafe', 'Topla kafa', 3.00, 'Topli napici', 'DRINK', 80, 1),
(71, 'Macchiato', 'Topla kafa', 3.00, 'Topli napici', 'DRINK', 80, 1),
(72, 'Cappuccino', 'Topla kafa', 3.00, 'Topli napici', 'DRINK', 80, 1),
(73, 'Topla čokolada', 'Topli napitak', 5.00, 'Topli napici', 'DRINK', 60, 1),
(74, 'Čaj', 'Topli napitak', 2.50, 'Topli napici', 'DRINK', 80, 1),

-- BEZALKOHOLNA PIĆA
(75, 'Prirodni sok', 'Voćni sok', 4.00, 'Bezalkoholna pića', 'DRINK', 120, 1),
(76, 'Gazirani sok', 'Gazirano piće', 4.00, 'Bezalkoholna pića', 'DRINK', 120, 1),
(77, 'Mineralna voda 0.25', 'Gazirana voda', 3.00, 'Bezalkoholna pića', 'DRINK', 150, 1),
(78, 'Mineralna voda 1l', 'Gazirana voda', 8.00, 'Bezalkoholna pića', 'DRINK', 60, 1),
(79, 'Mineralna voda senzacija', 'Gazirana voda', 3.00, 'Bezalkoholna pića', 'DRINK', 100, 1),
(80, 'Flaširana voda 0.33', 'Negazirana voda', 3.00, 'Bezalkoholna pića', 'DRINK', 150, 1),
(81, 'Orangina', 'Gazirani sok', 5.00, 'Bezalkoholna pića', 'DRINK', 100, 1),
(82, 'Ledeni čaj', 'Hladni čaj', 4.00, 'Bezalkoholna pića', 'DRINK', 100, 1),
(83, 'Cedevita', 'Vitaminski napitak', 4.00, 'Bezalkoholna pića', 'DRINK', 120, 1),
(84, 'Limunada', 'Svježi napitak', 5.00, 'Bezalkoholna pića', 'DRINK', 80, 1),
(85, 'Cijeđena narandža', 'Svježi sok', 6.00, 'Bezalkoholna pića', 'DRINK', 60, 1),

-- ALKOHOLNA PIĆA
(86, 'Jack Daniels', 'Whiskey', 6.00, 'Alkohol', 'DRINK', 40, 1),
(87, 'Stock', 'Likér', 3.00, 'Alkohol', 'DRINK', 50, 1),
(88, 'Vodka', 'Žestoko piće', 4.00, 'Alkohol', 'DRINK', 60, 1),
(89, 'Rakija šljivovica', 'Domaća rakija', 4.00, 'Alkohol', 'DRINK', 60, 1),
(90, 'Rakija travarica', 'Biljna rakija', 4.00, 'Alkohol', 'DRINK', 60, 1),
(91, 'Rakija jabuka', 'Voćna rakija', 4.00, 'Alkohol', 'DRINK', 60, 1),
(92, 'Rakija viljamovka', 'Voćna rakija', 4.00, 'Alkohol', 'DRINK', 60, 1),
(93, 'Viljamovka Prior', 'Premium rakija', 6.00, 'Alkohol', 'DRINK', 40, 1),
(94, 'Rum', 'Žestoko piće', 3.00, 'Alkohol', 'DRINK', 60, 1),
(95, 'Gin', 'Žestoko piće', 4.00, 'Alkohol', 'DRINK', 60, 1),

-- LIKERI
(96, 'Jagermeister', 'Biljni liker', 4.00, 'Liker', 'DRINK', 50, 1),
(97, 'Pelinkovac', 'Biljni liker', 4.00, 'Liker', 'DRINK', 50, 1),
(98, 'Višnjevac', 'Voćni liker', 4.00, 'Liker', 'DRINK', 50, 1),
(99, 'Orahovac', 'Orah liker', 4.00, 'Liker', 'DRINK', 50, 1),

-- PIVO
(100, 'Tuborg', 'Pivo', 4.00, 'Pivo', 'DRINK', 120, 1),
(101, 'Carlsberg', 'Pivo', 4.50, 'Pivo', 'DRINK', 120, 1),
(102, 'Krombacher', 'Pivo', 4.00, 'Pivo', 'DRINK', 120, 1),
(103, 'Pivo bezalkoholno', 'Bezalkoholno pivo', 4.00, 'Pivo', 'DRINK', 80, 1),
(104, 'Pivo razni okusi', 'Aromatizovano pivo', 4.00, 'Pivo', 'DRINK', 80, 1),
(105, 'Tamno pivo', 'Tamno pivo', 4.00, 'Pivo', 'DRINK', 80, 1),
(106, 'Sarajevsko pivo 0.33', 'Domaće pivo', 4.00, 'Pivo', 'DRINK', 120, 1),
(107, 'Sarajevsko pivo 0.5', 'Domaće pivo', 6.00, 'Pivo', 'DRINK', 80, 1),

-- VINO
(108, 'Vino bijelo 0.2', 'Bijelo vino', 6.00, 'Vino', 'DRINK', 80, 1),
(109, 'Vino bijelo 0.1', 'Bijelo vino', 3.00, 'Vino', 'DRINK', 80, 1),
(110, 'Vino crno 0.2', 'Crno vino', 6.00, 'Vino', 'DRINK', 80, 1),
(111, 'Vino crno 0.1', 'Crno vino', 3.00, 'Vino', 'DRINK', 80, 1),
(112, 'Vino bijelo Graševina 0.75', 'Buteljirano vino', 45.00, 'Vino', 'DRINK', 30, 1),
(113, 'Vino bijelo Vukoja 0.75', 'Buteljirano vino', 60.00, 'Vino', 'DRINK', 30, 1),
(114, 'Vino bijelo Kameno 0.75', 'Buteljirano vino', 45.00, 'Vino', 'DRINK', 30, 1),
(115, 'Vino bijelo Krauthaker 0.75', 'Buteljirano vino', 60.00, 'Vino', 'DRINK', 30, 1),
(116, 'Vino crno Vukoja 0.75', 'Buteljirano vino', 60.00, 'Vino', 'DRINK', 30, 1),
(117, 'Vino crno Vranac Pro Corde 0.75', 'Buteljirano vino', 45.00, 'Vino', 'DRINK', 30, 1),
(118, 'Vino crno Blatina 0.75', 'Buteljirano vino', 45.00, 'Vino', 'DRINK', 30, 1),
(119, 'Vino bijelo 0.187', 'Čaša vina', 9.00, 'Vino', 'DRINK', 60, 1),
(120, 'Vino crno 0.187', 'Čaša vina', 9.00, 'Vino', 'DRINK', 60, 1)

    AS new_drinks(id, naziv, opis, cijena, kategorija, item_type, current_qty, is_active)
ON DUPLICATE KEY UPDATE
                     naziv = new_drinks.naziv,
                     opis = new_drinks.opis,
                     cijena = new_drinks.cijena,
                     kategorija = new_drinks.kategorija,
                     item_type = new_drinks.item_type,
                     current_qty = new_drinks.current_qty,
                     is_active = new_drinks.is_active;
