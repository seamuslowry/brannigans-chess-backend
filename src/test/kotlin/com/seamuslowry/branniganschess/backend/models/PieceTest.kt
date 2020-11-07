import com.seamuslowry.branniganschess.backend.models.Game
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
        val game = Game("New Game")
        assertDoesNotThrow { Bishop(PieceColor.BLACK, game) }
        assertDoesNotThrow { Pawn(PieceColor.BLACK, game) }
        assertDoesNotThrow { Queen(PieceColor.BLACK, game) }
        assertDoesNotThrow { King(PieceColor.BLACK, game) }
        assertDoesNotThrow { Knight(PieceColor.BLACK, game) }
        assertDoesNotThrow { Rook(PieceColor.BLACK, game) }
    }
}