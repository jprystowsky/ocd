package io.mapping.ocd.scanner

import java.io.FileInputStream

import com.roundeights.hasher.Digest
import com.roundeights.hasher.Implicits._
import scala.language.postfixOps


trait Crc32DuplicateScanner extends DigestDuplicateScanner {
	override protected def getDigestStream[T <: FileInputStream](fis: T): Digest = fis.crc32

	override protected def getDigestArray[T <: Array[Byte]](arr: T): Digest = arr.crc32
}
