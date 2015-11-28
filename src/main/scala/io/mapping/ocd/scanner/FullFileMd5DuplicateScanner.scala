package io.mapping.ocd.scanner

import java.io.{File, FileInputStream}

import io.mapping.ocd.fingerprint.Fingerprint

class FullFileMd5DuplicateScanner extends Md5DuplicateScanner {
	override protected def getFingerprint(file: File): Fingerprint = {
		val fis = new FileInputStream(file)

		val fp = getFingerprintStream(fis)

		fis.close()

		fp
	}
}
