package io.mapping.ocd.scanner

import java.io.FileInputStream

import com.roundeights.hasher.Digest
import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

trait Md5DuplicateScanner extends DigestDuplicateScanner {
	override protected def getDigestStream[T <: FileInputStream](fis: T): Digest = fis.md5

	override protected def getDigestArray[T <: Array[Byte]](arr: T): Digest = arr.md5
}
