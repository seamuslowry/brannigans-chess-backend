package com.seamuslowry.branniganschess.backend.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Player (
        val authId: String,
        var name: String = "",
        val imageUrl: String = "",
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = -1
)