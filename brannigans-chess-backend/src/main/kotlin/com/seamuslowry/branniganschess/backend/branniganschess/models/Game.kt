package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class Game {
    @Id
    @GeneratedValue
    private var id: Long? = null

    private var uuid: String? = null

    @ManyToOne
    private var whitePlayer: Player? = null;

    @ManyToOne
    private var blackPlayer: Player? = null;

    @ManyToOne
    private var winner: Player? = null;
}