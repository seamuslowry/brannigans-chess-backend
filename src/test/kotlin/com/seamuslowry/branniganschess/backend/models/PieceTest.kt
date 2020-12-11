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
        assertDoesNotThrow { Bishop(PieceColor.BLACK, Game("bishop")) }
        assertDoesNotThrow { Pawn(PieceColor.BLACK, Game("pawn")) }
        assertDoesNotThrow { Queen(PieceColor.BLACK, Game("queen")) }
        assertDoesNotThrow { King(PieceColor.BLACK, Game("king")) }
        assertDoesNotThrow { Knight(PieceColor.BLACK, Game("knight")) }
        assertDoesNotThrow { Rook(PieceColor.BLACK, Game("rook")) }
    }
}