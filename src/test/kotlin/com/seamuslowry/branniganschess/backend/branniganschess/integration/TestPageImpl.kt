package com.seamuslowry.branniganschess.backend.branniganschess.integration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class TestPageImpl<T> @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) constructor(
        @JsonProperty("content") content: List<T>,
        @JsonProperty("totalElements") totalElements: Long?,
        @JsonProperty("pageable") pageable: JsonNode)
    : PageImpl<T>(content,
        PageRequest.of(pageable.asInt(0), pageable.asInt(10)),
        totalElements!!
) {}