package io.mapping.ocd.output

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint

import scala.collection.mutable

trait ProcessesDuplicates {
	def processDuplicates(dupes: mutable.HashMap[Fingerprint, List[File]])
}
