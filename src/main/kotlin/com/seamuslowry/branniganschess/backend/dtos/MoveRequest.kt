package com.seamuslowry.branniganschess.backend.dtos

data class MoveRequest (
    val srcRow: Int,
    val srcCol: Int,
    val dstRow: Int,
    val dstCol: Int
)