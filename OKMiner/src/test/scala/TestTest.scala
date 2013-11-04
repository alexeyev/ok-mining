import org.scalatest.FunSuite
import ru.stachek66.okminer.language.russian.Stemmer

/**
 * TODO: create nice descriptive tests
 * @author alexeyev
 */
class TestTest extends FunSuite {

  test("stemmer") {
    assert(Stemmer.stem("красивого") === "красив")
  }

}
