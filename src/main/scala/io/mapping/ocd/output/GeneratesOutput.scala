package io.mapping.ocd.output

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint

import scala.collection.mutable

trait GeneratesOutput {
	def generateOutput(dupes: mutable.HashMap[Fingerprint, List[File]])
}
