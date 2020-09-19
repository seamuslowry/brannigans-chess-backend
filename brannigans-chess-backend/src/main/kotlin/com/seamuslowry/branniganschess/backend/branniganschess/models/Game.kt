package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.*

@Entity
class Game (
        var uuid: String = "",
        @ManyToOne
        var whitePlayer: Player? = null,
        @ManyToOne
        var blackPlayer: Player? = null,
        @ManyToOne
        var winner: Player? = null,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = -1
) {}