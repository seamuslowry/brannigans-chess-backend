package com.seamuslowry.branniganschess.backend.branniganschess.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Player {
    @Id
    @GeneratedValue
    private var id: Long? = null

    private var username: String = ""

    private var password: String = ""

    private var email: String = ""
}