CREATE TABLE IF NOT EXISTS player(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS game(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL,
    white_player_id integer,
    black_player_id integer,
    winner_id integer,
    FOREIGN KEY (white_player_id) REFERENCES player(id),
    FOREIGN KEY (black_player_id) REFERENCES player(id),
    FOREIGN KEY (winner_id) REFERENCES player(id)
);

CREATE TABLE IF NOT EXISTS piece(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    color VARCHAR(5) NOT NULL,
    taken boolean NOT NULL default false,
    position_row integer,
    position_col integer,
    game_id integer NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE IF NOT EXISTS move(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    src_col integer NOT NULL,
    src_row integer NOT NULL,
    dst_col integer NOT NULL,
    dst_row integer NOT NULL,
    moving_piece_id integer NOT NULL,
    move_type VARCHAR(16) NOT NULL,
    taken_piece_id integer,
    FOREIGN KEY (moving_piece_id) REFERENCES piece(id),
    FOREIGN KEY (taken_piece_id) REFERENCES piece(id)
);