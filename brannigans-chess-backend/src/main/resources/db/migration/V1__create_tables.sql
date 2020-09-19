CREATE TABLE IF NOT EXISTS player(
    id integer PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS game(
    id integer PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL,
    white_player_id integer,
    black_player_id integer,
    winner_id integer,
    FOREIGN KEY (white_player_id) REFERENCES player(id),
    FOREIGN KEY (black_player_id) REFERENCES player(id),
    FOREIGN KEY (winner_id) REFERENCES player(id)
);

CREATE TABLE IF NOT EXISTS piece(
    id integer PRIMARY KEY,
    type integer NOT NULL,
    color VARCHAR(5) NOT NULL,
    taken boolean NOT NULL default false,
    pos_x integer,
    pos_y integer,
    game_id integer NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE IF NOT EXISTS move(
    id integer PRIMARY KEY,
    from_x integer NOT NULL,
    from_y integer NOT NULL,
    to_x integer NOT NULL,
    to_y integer NOT NULL,
    moving_piece_id integer NOT NULL,
    taken_piece_id integer NOT NULL,
    FOREIGN KEY (moving_piece_id) REFERENCES piece(id),
    FOREIGN KEY (taken_piece_id) REFERENCES piece(id)
);