package com.seamuslowry.branniganschess.backend.branniganschess.repos

import com.seamuslowry.branniganschess.backend.branniganschess.models.Move
import org.springframework.data.jpa.repository.JpaRepository

interface MoveInterface: JpaRepository<Move, Long> {
}