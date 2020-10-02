package com.seamuslowry.branniganschess.backend.repos

import com.seamuslowry.branniganschess.backend.models.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository: JpaRepository<Player, Long> {
}