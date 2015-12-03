package io.mapping.ocd.scanner

import java.io.{File, FileInputStream}

import io.mapping.ocd.OCD.Config
import io.mapping.ocd.fingerprint.Fingerprint
import io.mapping.ocd.output.ConsoleMessage

import scala.collection.mutable

trait DuplicateScanner {
	private def getFilesMulti(config: Config): List[File] = config.directories.toList.flatMap(getFiles(_, config))

	private def getFiles(directory: File, config: Config): List[File] = {
		var files = List[File]()

		if (directory.exists && directory.isDirectory) {
			files = directory.listFiles.filter(f => {
				if (config.skipEmpty) {
					if (f.length > 0) true
					else false
				} else {
					true
				}
			}).toList

			files ++= files.filter(_.isDirectory).flatMap(getFiles(_, config))
		}

		files
	}

	private def getFingeredFiles(parallel: Boolean, files: List[File]) = {
		if (parallel) {
			files.par.filter(_.isFile).map(f => getFingerprint(f) -> f)
		} else {
			files.filter(_.isFile).map(f => getFingerprint(f) -> f)
		}
	}

	protected def internalFindDuplicates(config: Config, files: List[File]): mutable.HashMap[Fingerprint, List[File]] = {
		val fingeredFiles = getFingeredFiles(config.parallel, files)

		val dupHash = new mutable.HashMap[Fingerprint, List[File]]

		for {fpFileMap <- fingeredFiles.filter {
			case (fpFileMap) => {
				if (config.parallel) {
					dupHash.values.par.count(_.contains(fpFileMap._2)) == 0
				} else {
					dupHash.values.count(_.contains(fpFileMap._2)) == 0
				}
			}
		}} {
					if (!dupHash.keySet.contains(fpFileMap._1)) {
						val otherFilePairs =
							if (config.parallel) fingeredFiles.par.filter(_._1.equals(fpFileMap._1))
							else fingeredFiles.filter(_._1.equals(fpFileMap._1))

						val otherFiles = otherFilePairs.map(_._2).toList

						dupHash += fpFileMap._1 -> otherFiles

						if (config.verbose) {
							ConsoleMessage.printMessage("Indexed " + dupHash.size + " files...")
						}
					}
				}

		dupHash.retain((x, y) => y.size > 1)

		if (config.verbose) {
			ConsoleMessage.printMessage("Narrowed to duplicates")
		}

		dupHash
	}

	protected def getFingerprint(file: File): Fingerprint

	protected def getFingerprintStream[T <: FileInputStream](fis: T): Fingerprint
	protected def getFingerprintArray[T <: Array[Byte]](arr: T): Fingerprint

	def findDuplicates(config: Config): mutable.HashMap[Fingerprint, List[File]] = internalFindDuplicates(config, getFilesMulti(config))
}
