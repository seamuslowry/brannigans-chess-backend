package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Player (
        var username: String = "",
        var password: String = "",
        var email: String = "",
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private var id: Long = -1
) {}