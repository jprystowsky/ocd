package io.mapping.ocd.scanner

import java.io.{FileInputStream, File}

import io.mapping.ocd.fingerprint.Fingerprint

class FirstBytesMd5DuplicateScanner(val bytes: Int) extends Md5DuplicateScanner {
	override protected def getFingerprint(file: File): Fingerprint = {
		val fis = new FileInputStream(file)

		val firstBytes = Array.ofDim[Byte](bytes)

		fis.read(firstBytes, 0, Math.min(bytes, fis.available))

		fis.close

		getFingerprintArray(firstBytes)
	}
}
