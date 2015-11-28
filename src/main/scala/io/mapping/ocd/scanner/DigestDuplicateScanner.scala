package io.mapping.ocd.scanner

import java.io.FileInputStream

import com.roundeights.hasher.Digest
import io.mapping.ocd.fingerprint.Fingerprint

import scala.language.postfixOps

trait DigestDuplicateScanner extends DuplicateScanner {
	override protected def getFingerprintStream[T <: FileInputStream](fis: T): Fingerprint = {
		val x = getDigestStream(fis).hex

		yieldFingerprint(x)
	}

	override protected def getFingerprintArray[T <: Array[Byte]](arr: T): Fingerprint = {
		val x = getDigestArray(arr).hex

		yieldFingerprint(x)
	}

	protected def getDigestStream[T <: FileInputStream](fis: T): Digest
	protected def getDigestArray[T <: Array[Byte]](arr: T): Digest

	private def yieldFingerprint(hash: String): Fingerprint = new Fingerprint { override var value: String = hash }
}
