import scala.quoted._
import scala.tasty.inspector._

object Bar {
  class Givens {
    given Int = 23
    given String = "the string"
  }
  class Foo {
    val g: Givens = Givens()
    import g.given
    import g.{given}
    import g.{given Int}
  }
}

// Case object implementation
sealed trait Flavor
case object Vanilla extends Flavor
case object Chocolate extends Flavor
case object Bourbon extends Flavor

object Test {
  def main(args: Array[String]): Unit = {
    // Artefact of the current test infrastructure
    // TODO improve infrastructure to avoid needing this code on each test
    val classpath = dotty.tools.dotc.util.ClasspathFromClassloader(this.getClass.getClassLoader).split(java.io.File.pathSeparator).find(_.contains("runWithCompiler")).get
    val allTastyFiles = dotty.tools.io.Path(classpath).walkFilter(_.extension == "tasty").map(_.toString).toList
    val tastyFiles = allTastyFiles.filter(_.contains("TraitParams"))

    val inspect = new TestInspector()
    inspect.inspectTastyFiles(allTastyFiles.filter(_.contains("Bar")))
  }
}

class TestInspector() extends TastyInspector:

  protected def processCompilationUnit(using Quotes)(root: quotes.reflect.Tree): Unit =
    import quotes.reflect._

    val code = root.show
    assert(code.contains("import Foo.this.g.{given}"), code)
    assert(code.contains("import Foo.this.g.{given scala.Int}"), code)

    val extractors = root.showExtractors
    assert(extractors.contains("GivenSelector"), extractors)
