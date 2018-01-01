package io.methvin.standardmethods.macros

import org.scalatest.{Assertion, WordSpec}

class MacrosSpec extends WordSpec {
  "equalsAllVals and hashCodeAllVals macros" should {
    "work with simple class" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        def zed = z
        override def equals(obj: Any) = equalsAllVals(this, obj)
        override def hashCode = hashCodeAllVals(this)
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = false)
    }
    "work with simple inherited class" in {
      abstract class Bar(val z: Boolean) {
        override def equals(obj: Any) = equalsAllVals(this, obj)
        override def hashCode = hashCodeAllVals(this)
      }
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) extends Bar(z) {
        override def equals(obj: Any) = equalsAllVals(this, obj) && super.equals(obj)
        override def hashCode = hashCodeAllVals(this) ^ super.hashCode()
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = true)
    }
    "work with simple class with a non constructor val" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        val zed = z
        override def equals(obj: Any) = equalsAllVals(this, obj)
        override def hashCode = hashCodeAllVals(this)
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = true)
    }
    "work with simple class with canEqual" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        def zed = z
        override def equals(obj: Any) = equalsAllVals(this, obj)
        override def hashCode = hashCodeAllVals(this)
        def canEqual(other: Any): Boolean = other.isInstanceOf[Foo]
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = false)
    }
    "work with simple class with more more strict canEqual" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        def zed = z
        override def equals(obj: Any) = equalsAllVals(this, obj)
        override def hashCode = hashCodeAllVals(this)
        def canEqual(other: Any): Boolean = this eq other.asInstanceOf[AnyRef]
      }
      new Foo() mustNotEqual new Foo()
      val foo = new Foo()
      foo mustEqualWithHashCode foo
    }
  }
  "equalsConstructorVals and hashCodeConstructorVals" should {
    "work with simple class" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        val zed = z
        override def equals(obj: Any) = equalsConstructorVals(this, obj)
        override def hashCode = hashCodeConstructorVals(this)
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = false)
    }
    "work with simple inherited class" in {
      abstract class Bar(val z: Boolean)
      class Foo(val x: Int = 1, val y: String = "y", override val z: Boolean = false) extends Bar(z) {
        override def equals(obj: Any) = equalsConstructorVals(this, obj)
        override def hashCode = hashCodeConstructorVals(this)
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustNotEqual new Foo(z = false)
      new Foo(z = true) mustEqualWithHashCode new Foo(z = true)
    }
    "work with simple class with canEqual" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        val zed = z
        override def equals(obj: Any) = equalsConstructorVals(this, obj)
        override def hashCode = hashCodeConstructorVals(this)
        def canEqual(other: Any): Boolean = other.isInstanceOf[Foo]
      }
      new Foo() mustEqualWithHashCode new Foo()
      new Foo(y = "5") mustNotEqual new Foo(y = "4")
      new Foo(z = true) mustEqualWithHashCode new Foo(z = false)
    }
    "work with simple class with more more strict canEqual" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        val zed = z
        override def equals(obj: Any) = equalsConstructorVals(this, obj)
        override def hashCode = hashCodeConstructorVals(this)
        def canEqual(other: Any): Boolean = this eq other.asInstanceOf[AnyRef]
      }
      new Foo() mustNotEqual new Foo()
      val foo = new Foo()
      foo mustEqualWithHashCode foo
    }
  }
  "toStringConstructorParams" should {
    "generate a toString from constructor params" in {
      class Foo(val x: Int = 1, val y: String = "y", z: Boolean = false) {
        def zed = z
        override def toString = toStringConstructorParams(this)
      }
      new Foo(5, "six", true).toString mustEqual "Foo(5,six,true)"
    }
  }

  private implicit class Equalable[T](self: T) {
    def mustEqual(other: T): Assertion = {
      assert(self == other)
    }
    def mustEqualWithHashCode(other: T): Assertion = {
      assert(self == other && self.hashCode == other.hashCode)
    }
    def mustNotEqual(other: T): Assertion = {
      assert(self != other)
    }
  }
}
