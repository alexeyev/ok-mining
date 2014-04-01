import org.scalatest.FunSuite
import ru.stachek66.okminer.utils.Trie
import scala.collection.JavaConversions._

/**
 * Checking if Trie works correctly.
 * @author alexeyev
 */
class TrieTest extends FunSuite {

  test("trie") {

    val t: Trie = new Trie()
    t.add("APPLE")
    t.add("APPLESAUCE")
    t.add("APPLICATION")
    assert(t.foundPrefixes("APPLEJOUICE")(0) == 5)
    assert(t.foundPrefixes("APPLEJ")(0) == 5)
    assert(t.foundPrefixes("APPLE")(0) == 5)
    assert(t.foundPrefixes("AP").isEmpty)

    assert(!t.contains("FOO"))
    assert(!t.contains("APPL"))
    assert(!t.contains("APPLES"))
    assert(t.contains("APPLE"))

    assert(!t.containsAsPrefix("FOO"))
    assert(t.containsAsPrefix("APPL"))
    assert(t.containsAsPrefix("APPLES"))
    assert(t.containsAsPrefix("APPLE"))

  }
}
