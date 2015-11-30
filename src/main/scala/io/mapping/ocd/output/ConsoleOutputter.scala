package io.mapping.ocd.output

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint

import scala.collection.mutable

class ConsoleOutputter extends ProcessesDuplicates {
	override def processDuplicates(dupes: mutable.HashMap[Fingerprint, List[File]]): Unit = {
		for (pair <- dupes) {
			Console.println("Duplicate set " + pair._1 + ":")

			for (file <- pair._2) {
				Console.println("\t" + file.getAbsolutePath)
			}

			Console.println()
		}
	}
}
