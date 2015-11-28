package io.mapping.ocd.scanner

import java.io.FileInputStream

import io.mapping.ocd.fingerprint.Fingerprint

abstract class Md5DuplicateScanner extends DuplicateScanner {
	protected def getFingerprintFromBytes(bytes: Array[Byte]): Fingerprint = {
		val md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(bytes)

		yieldFingerprint(md5)
	}

	protected def getFingerprintFromStream(fis: FileInputStream): Fingerprint = {
		val md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis)

		yieldFingerprint(md5)
	}

	private def yieldFingerprint(md5: String): Fingerprint = new Fingerprint { override var value: String = md5 }
}
