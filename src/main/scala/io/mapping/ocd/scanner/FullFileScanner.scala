package io.mapping.ocd.scanner

import java.io.{FileInputStream, File}

import io.mapping.ocd.fingerprint.Fingerprint

trait FullFileScanner {
	this: DuplicateScanner =>
	override protected def getFingerprint(file: File): Fingerprint = {
		val fis = new FileInputStream(file)

		val fp = getFingerprintStream(fis)

		fis.close()

		fp
	}
}
