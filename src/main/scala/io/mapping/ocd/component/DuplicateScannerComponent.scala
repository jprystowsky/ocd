package io.mapping.ocd.component

import io.mapping.ocd.scanner.DuplicateScanner

trait DuplicateScannerComponent {
	def getDuplicateScanner: DuplicateScanner
}
