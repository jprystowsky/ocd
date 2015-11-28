package io.mapping.ocd.scanner

import java.io.FileInputStream

import io.mapping.ocd.fingerprint.Fingerprint

import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

abstract class Md5DuplicateScanner extends DuplicateScanner {
	protected def getFingerprintStream[T <: FileInputStream](fis: T): Fingerprint = {
//		val md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis)
		val md5 = fis.md5.hex

		yieldFingerprint(md5)
	}

	protected def getFingerprintArray[T <: Array[Byte]](arr: T): Fingerprint = {
		//val md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(arr)
		val md5 = arr.md5.hex

		yieldFingerprint(md5)
	}

	private def yieldFingerprint(md5: String): Fingerprint = new Fingerprint { override var value: String = md5 }
}
