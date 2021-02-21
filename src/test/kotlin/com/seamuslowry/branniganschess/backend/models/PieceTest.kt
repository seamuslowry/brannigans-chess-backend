import com.seamuslowry.branniganschess.backend.models.PieceColor
import com.seamuslowry.branniganschess.backend.models.pieces.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
class PieceTest {
    @Test
    fun `can be instantiated without location`() {
        assertDoesNotThrow { Bishop(PieceColor.BLACK, 1L) }
        assertDoesNotThrow { Pawn(PieceColor.BLACK, 1L) }
        assertDoesNotThrow { Queen(PieceColor.BLACK, 1L) }
        assertDoesNotThrow { King(PieceColor.BLACK, 1L) }
        assertDoesNotThrow { Knight(PieceColor.BLACK, 1L) }
        assertDoesNotThrow { Rook(PieceColor.BLACK, 1L) }
    }
}