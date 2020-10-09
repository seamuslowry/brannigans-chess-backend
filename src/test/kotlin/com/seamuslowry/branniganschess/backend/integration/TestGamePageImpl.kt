package com.seamuslowry.branniganschess.backend.integration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.seamuslowry.branniganschess.backend.models.Game
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class TestGamePageImpl (
        content: List<Game>,
        totalElements: Long?,
        pageable: JsonNode)
    : PageImpl<Game>(
        content,
        PageRequest.of(pageable.asInt(0), pageable.asInt(10)),
        totalElements!!
)