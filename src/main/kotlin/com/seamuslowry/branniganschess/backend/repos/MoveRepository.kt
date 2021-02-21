package com.seamuslowry.branniganschess.backend.repos

import com.seamuslowry.branniganschess.backend.models.Move
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface MoveRepository: JpaRepository<Move, Long>, JpaSpecificationExecutor<Move>