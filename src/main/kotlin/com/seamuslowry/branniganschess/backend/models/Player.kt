package com.seamuslowry.branniganschess.backend.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Player (
        private val googleId: String,
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private var id: Long = -1
) {
        val authId: String
                get() = googleId
}