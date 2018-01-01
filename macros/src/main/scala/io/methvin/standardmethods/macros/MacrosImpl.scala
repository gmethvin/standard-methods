/*
 * Copyright 2017 Greg Methvin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.methvin.standardmethods.macros

import scala.reflect.macros.blackbox

private[macros] class MacrosImpl(val c: blackbox.Context) {
  import c.universe._

  def hashCodeAllVals[T: WeakTypeTag](self: c.Expr[T]): Tree = {
    hashCodeImpl(self, nonInheritedVals)
  }

  def hashCodeConstructorVals[T: WeakTypeTag](self: c.Expr[T]): Tree = {
    hashCodeImpl(self, nonInheritedConstructorVals)
  }

  def equalsAllVals[T: WeakTypeTag](self: c.Expr[T], other: c.Expr[Any]): Tree = {
    equalsImpl(self, other, nonInheritedVals)
  }

  def equalsConstructorVals[T: WeakTypeTag](self: c.Expr[T], other: c.Expr[Any]): Tree = {
    equalsImpl(self, other, nonInheritedConstructorVals)
  }

  def toStringConstructorParams[T: WeakTypeTag](self: c.Expr[T]): Tree = {
    toStringImpl(self, constructorParams)
  }

  // Helpers

  private def toStringImpl[T](self: c.Expr[T], symbols: Seq[Symbol])(implicit tag: WeakTypeTag[T]): Tree = {
    import tag.tpe
    assert(self != null) // we use this to infer the type without having to rely on context
    val params = symbols.map(_.name.toTermName)
    // Note that this assumes the toString is being called inside the class
    q"""${tpe.typeSymbol.name.toString} + Seq(..$params).mkString("(", ",", ")")"""
  }

  private def hashCodeImpl[T](self: c.Expr[T], symbols: Seq[Symbol]): Tree = {
    val params = symbols.map(_.name.toTermName).map(n => q"$self.$n")
    q"scala.util.hashing.MurmurHash3.seqHash(scala.collection.immutable.Seq(..$params))"
  }

  private def equalsImpl[T](self: c.Expr[T], other: c.Expr[Any], symbols: Seq[Symbol])
    (implicit tag: WeakTypeTag[T]): Tree = {
    import tag.tpe

    val otherCast = TermName(c.freshName("other"))

    val equalAllVals = symbols.map { sym =>
      val termName = sym.asTerm.name.toTermName
      q"$self.$termName == $otherCast.$termName"
    }.reduce((a, b) => q"$a && $b")

    val canEqual = canEqualOpt match {
      case Some(ce) => q"$otherCast.$ce($self)"
      case None => q"$other.getClass == $self.getClass"
    }

    q"""
       $other match {
         case $otherCast: $tpe => $canEqual && $equalAllVals
         case _ => false
       }
     """
  }

  private def canEqualOpt[T](implicit tag: WeakTypeTag[T]): Option[Symbol] = {
    val name = TermName("canEqual")
    val canEqual = typeOf[scala.Equals].member(name)
    tag.tpe.member(name) match {
      case member if member.name == name && canEqual.typeSignature =:= member.typeSignature => Some(member)
      case _ => None
    }
  }

  private def nonInheritedConstructorVals[T](implicit tag: WeakTypeTag[T]): Seq[Symbol] = {
    val argNames = constructorParams.map(_.name).toSet
    nonInheritedVals.filter(v => argNames.contains(v.name))
  }

  private def constructorParams[T](implicit tag: WeakTypeTag[T]): Seq[Symbol] = {
    tag.tpe.member(termNames.CONSTRUCTOR).asMethod.paramLists.flatten
  }

  private def nonInheritedVals[T](implicit tag: WeakTypeTag[T]): Seq[Symbol] = {
    import tag.tpe
    tpe.members.sorted.filter { t => t.isTerm && isVal(t) && t.owner == tpe.typeSymbol }
  }

  private def isVal(term: Symbol): Boolean = term.isMethod && term.asTerm.isStable
}
