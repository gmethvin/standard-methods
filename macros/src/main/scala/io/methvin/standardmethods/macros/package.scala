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

package io.methvin.standardmethods

package object macros {

  def equalsAllVals[T](self: T, other: Any): Boolean =
    macro MacrosImpl.equalsAllVals[T]
  def hashCodeAllVals[T](self: T): Int = macro MacrosImpl.hashCodeAllVals[T]
  def equalsConstructorVals[T](self: T, other: Any): Boolean =
    macro MacrosImpl.equalsConstructorVals[T]
  def hashCodeConstructorVals[T](self: T): Int =
    macro MacrosImpl.hashCodeConstructorVals[T]
  def toStringConstructorParams[T](self: T): String =
    macro MacrosImpl.toStringConstructorParams[T]

}
