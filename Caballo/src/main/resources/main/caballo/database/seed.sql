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
     '863d979d27e83b1fe3297e33314ace50662e940fe1df04bba121826ed5c02cfc',
     'aedee604cec0994240a8fa3f0c6e5562',
     'user'),
    (3,
     'Abida',
     'db6517eaad9eccd9752682067ea5959ea53fa15b2f30d47e8c7163ebad82f542',
     '4c953491b7629a01968942731d4f508e',
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


INSERT INTO menu_items (id, naziv, opis, cijena, kategorija, item_type, current_qty, is_active)
VALUES
    (1, 'Espresso', 'Kratka crna kafa', 2.00, 'Piće', 'DRINK', 50, 1),
    (2, 'Cappuccino', 'Espresso sa mlijekom i pjenom', 2.50, 'Piće', 'DRINK', 50, 1),
    (3, 'Juha dana', 'Svježe pripremljena juha', 3.50, 'Predjelo', 'FOOD', 0, 1),
    (4, 'Bruschetta', 'Prepečeni hljeb sa paradajzom i bosiljkom', 4.00, 'Predjelo', 'FOOD', 0, 1),
    (5, 'Margherita pizza', 'Pizza sa sirom i paradajz sosom', 7.50, 'Glavno jelo', 'FOOD', 0, 1),
    (6, 'Ćevapi', 'Porcija od 10 ćevapa sa prilogom', 8.00, 'Glavno jelo', 'FOOD', 0, 1),
    (7, 'Piletina na žaru', 'Pileći file na žaru sa prilogom', 9.00, 'Glavno jelo', 'FOOD', 0, 1),
    (8, 'Šopska salata', 'Svježa salata sa sirom', 3.50, 'Salata', 'FOOD', 0, 1),
    (9, 'Cheesecake', 'Kolač od sira sa preljevom', 4.50, 'Desert', 'FOOD', 0, 1),
    (10, 'Čokoladni kolač', 'Bogati čokoladni desert', 4.00, 'Desert', 'FOOD', 0, 1),

    (11, 'Bolonjez pasta', 'Tjestenina sa mesnim sosom od paradajza', 8.50, 'Glavno jelo', 'FOOD', 0, 1),
    (12, 'Carbonara pasta', 'Tjestenina sa slaninom, jajetom i parmezanom', 9.00, 'Glavno jelo', 'FOOD', 0, 1),
    (13, 'Risotto sa gljivama', 'Kremasti rižoto sa šumskim gljivama', 9.50, 'Glavno jelo', 'FOOD', 0, 1),
    (14, 'Burger classic', 'Goveđi burger sa sirom, salatom i sosom', 7.90, 'Glavno jelo', 'FOOD', 0, 1),
    (15, 'Burger bbq', 'Burger sa roštilj sosom, karameliziranim lukom i sirom', 8.50, 'Glavno jelo', 'FOOD', 0, 1),
    (16, 'Pileći burger', 'Pileći burger sa hrskavom piletinom i sosom', 7.50, 'Glavno jelo', 'FOOD', 0, 1),
    (17, 'Pileća salata', 'Miješana salata sa komadićima piletine', 6.50, 'Salata', 'FOOD', 0, 1),
    (18, 'Grčka salata', 'Salata sa fetom, maslinama, krastavcem i paradajzom', 5.50, 'Salata', 'FOOD', 0, 1),
    (19, 'Cezar salata', 'Salata sa piletinom, parmezanom i krutonima', 6.90, 'Salata', 'FOOD', 0, 1),
    (20, 'Pomfrit', 'Hrskavi pomfrit uz prilog sosa', 3.00, 'Prilog', 'FOOD', 0, 1),
    (21, 'Pire krompir', 'Kremasti pire od krompira', 3.20, 'Prilog', 'FOOD', 0, 1),
    (22, 'Grilovano povrće', 'Mješavina sezonskog povrća na žaru', 4.20, 'Prilog', 'FOOD', 0, 1),
    (23, 'Pohovana piletina', 'Hrskavi pileći file sa prilogom', 8.20, 'Glavno jelo', 'FOOD', 0, 1),
    (24, 'Gulaš', 'Goveđi gulaš sa domaćim hljebom', 8.80, 'Glavno jelo', 'FOOD', 0, 1),
    (25, 'Riblji file na žaru', 'File bijele ribe na žaru sa limunom', 10.50, 'Glavno jelo', 'FOOD', 0, 1),
    (26, 'Tuna salata', 'Salata sa tunom, kukuruzom i povrćem', 6.20, 'Salata', 'FOOD', 0, 1),
    (27, 'Lasagna', 'Slojevita tjestenina sa mesnim sosom i sirom', 9.20, 'Glavno jelo', 'FOOD', 0, 1),
    (28, 'Vegetarijanska pizza', 'Pizza sa povrćem i sirom', 8.00, 'Glavno jelo', 'FOOD', 0, 1),
    (29, 'Funghi pizza', 'Pizza sa gljivama i sirom', 8.20, 'Glavno jelo', 'FOOD', 0, 1),
    (30, 'Quattro Formaggi pizza', 'Pizza sa četiri vrste sira', 9.00, 'Glavno jelo', 'FOOD', 0, 1),

    (31, 'Tiramisu', 'Talijanski desert sa kavom i mascarpone sirom', 4.80, 'Desert', 'FOOD', 0, 1),
    (32, 'Palačinke sa nutellom', 'Palačinke punjene nutellom i orasima', 4.20, 'Desert', 'FOOD', 0, 1),
    (33, 'Voćna salata', 'Mješavina svježeg sezonskog voća', 3.80, 'Desert', 'FOOD', 0, 1),
    (34, 'Sladoled kugla', 'Jedna kugla sladoleda po izboru', 1.80, 'Desert', 'FOOD', 0, 1),
    (35, 'Palačinke sa džemom', 'Palačinke punjene domaćim džemom', 3.80, 'Desert', 'FOOD', 0, 1),
    (36, 'Čokoladni mousse', 'Lagani čokoladni krem desert', 4.30, 'Desert', 'FOOD', 0, 1),
    (37, 'Pita od jabuka', 'Topla pita od jabuka sa cimetom', 4.00, 'Desert', 'FOOD', 0, 1),

    (38, 'Mineralna voda', 'Gazirana mineralna voda 0.33l', 1.80, 'Piće', 'DRINK', 100, 1),
    (39, 'Voda negazirana', 'Negazirana voda 0.33l', 1.50, 'Piće', 'DRINK', 100, 1),
    (40, 'Coca-Cola 0.33l', 'Hladni gazirani napitak', 2.00, 'Piće', 'DRINK', 100, 1),
    (41, 'Coca-Cola Zero 0.33l', 'Gazirano piće bez šećera', 2.00, 'Piće', 'DRINK', 100, 1),
    (42, 'Fanta 0.33l', 'Gazirano piće od narandže', 2.00, 'Piće', 'DRINK', 100, 1),
    (43, 'Sprite 0.33l', 'Gazirano limun-nana piće', 2.00, 'Piće', 'DRINK', 100, 1),
    (44, 'Sok od narandže', 'Cijeđeni sok od narandže 0.25l', 2.80, 'Piće', 'DRINK', 80, 1),
    (45, 'Sok od jabuke', 'Prirodni sok od jabuke 0.25l', 2.50, 'Piće', 'DRINK', 80, 1),
    (46, 'Sok od breskve', 'Voćni sok od breskve 0.25l', 2.50, 'Piće', 'DRINK', 80, 1),
    (47, 'Ledeni čaj breskva', 'Hladni čaj sa aromom breskve', 2.50, 'Piće', 'DRINK', 80, 1),
    (48, 'Ledeni čaj limun', 'Hladni čaj sa aromom limuna', 2.50, 'Piće', 'DRINK', 80, 1),
    (49, 'Gazirana limunada', 'Domaća limunada sa mineralnom vodom', 2.20, 'Piće', 'DRINK', 80, 1),
    (50, 'Domaća limunada', 'Svježe cijeđena limunada', 2.20, 'Piće', 'DRINK', 80, 1),

    (51, 'Duge kafe', 'Velika crna kafa', 2.20, 'Piće', 'DRINK', 80, 1),
    (52, 'Macchiato', 'Espresso sa malo mlijeka', 2.30, 'Piće', 'DRINK', 80, 1),
    (53, 'Latte macchiato', 'Kafa sa puno mlijeka i pjenom', 2.80, 'Piće', 'DRINK', 80, 1),
    (54, 'Mokka', 'Kafa sa čokoladnim sirupom', 3.00, 'Piće', 'DRINK', 60, 1),
    (55, 'Vruća čokolada', 'Gusta topla čokolada', 3.00, 'Piće', 'DRINK', 60, 1),
    (56, 'Čaj kamilica', 'Topli biljni čaj od kamilice', 2.00, 'Piće', 'DRINK', 60, 1),
    (57, 'Čaj menta', 'Topli biljni čaj od mente', 2.00, 'Piće', 'DRINK', 60, 1),
    (58, 'Crni čaj', 'Topli crni čaj', 2.00, 'Piće', 'DRINK', 60, 1),

    (59, 'Pivo točeno 0.3l', 'Svijetlo točeno pivo', 2.80, 'Piće', 'DRINK', 80, 1),
    (60, 'Pivo točeno 0.5l', 'Svijetlo točeno pivo', 3.50, 'Piće', 'DRINK', 80, 1),
    (61, 'Pivo flaša 0.33l', 'Lager pivo u boci', 3.00, 'Piće', 'DRINK', 80, 1),
    (62, 'Bijelo vino čaša', 'Suho bijelo vino 0.15l', 3.50, 'Piće', 'DRINK', 60, 1),
    (63, 'Crno vino čaša', 'Suho crno vino 0.15l', 3.50, 'Piće', 'DRINK', 60, 1),
    (64, 'Rakija šljivovica', 'Domaća rakija 0.03l', 2.50, 'Piće', 'DRINK', 60, 1),
    (65, 'Rakija lozovača', 'Domaća lozova rakija 0.03l', 2.50, 'Piće', 'DRINK', 60, 1),
    (66, 'Aperol Spritz', 'Koktel sa Aperolom, pjenušcem i sokom', 5.50, 'Piće', 'DRINK', 40, 1),
    (67, 'Gin Tonic', 'Koktel od gina i tonika sa limetom', 5.50, 'Piće', 'DRINK', 40, 1),
    (68, 'Whiskey 0.03l', 'Premium whiskey', 4.50, 'Piće', 'DRINK', 40, 1)
    AS new_menu(id, naziv, opis, cijena, kategorija, item_type, current_qty, is_active)
ON DUPLICATE KEY UPDATE
                     naziv        = new_menu.naziv,
                     opis = new_menu.opis,
                     cijena       = new_menu.cijena,
                     kategorija    = new_menu.kategorija,
                     item_type     = new_menu.item_type,
                     current_qty  = new_menu.current_qty,
                     is_active    = new_menu.is_active;
