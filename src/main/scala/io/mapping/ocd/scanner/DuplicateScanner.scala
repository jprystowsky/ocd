package io.mapping.ocd.scanner

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint
import io.mapping.ocd.output.ConsoleMessage

import scala.collection.mutable

trait DuplicateScanner {
	private def getFiles(directory: File): List[File] = {
		var files = List[File]()

		if (directory.exists && directory.isDirectory) {
			files = directory.listFiles.toList

			files ++= files.filter(_.isDirectory).flatMap(getFiles)
		}

		files//.filter(_.isFile)
	}

	protected def internalFindDuplicates(files: List[File]): mutable.HashMap[Fingerprint, List[File]] = {
		val fingeredFiles = files.par.filter(_.isFile).map(f => getFingerprint(f) -> f)

		val dupHash = new mutable.HashMap[Fingerprint, List[File]]

		for (fpFileMap <- fingeredFiles if dupHash.values.par.count(_.contains(fpFileMap._2)) == 0) {
			if (!dupHash.keySet.contains(fpFileMap._1)) {
				val otherFilePairs = fingeredFiles.par.filter(_._1.equals(fpFileMap._1))
				val otherFiles = otherFilePairs.map(_._2).toList

				dupHash += fpFileMap._1 -> otherFiles

				ConsoleMessage.printMessage("Indexed " + dupHash.size + " files...")
			}
		}

		dupHash.retain((x, y) => y.size > 1)

		ConsoleMessage.printMessage("Narrowed to duplicates")

		dupHash
	}

	protected def getFingerprint(file: File): Fingerprint

	def findDuplicates(directory: File): mutable.HashMap[Fingerprint, List[File]] = internalFindDuplicates(getFiles(directory))
}
