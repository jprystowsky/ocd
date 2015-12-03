package io.mapping.ocd

import java.io.File

import io.mapping.ocd.component.DuplicateScannerComponent
import io.mapping.ocd.output.{FileMover, JsonOutputter, SqlOutputter, ConsoleOutputter}
import io.mapping.ocd.provider.DuplicateScannerProvider
import io.mapping.ocd.scanner._

object OCD extends App {

	case class Config(
		                 directories: Seq[File] = null,
		                 md5: Boolean = false,
		                 crc32: Boolean = false,
		                 bytes: Int = 0,
		                 parallel: Boolean = false,
		                 outputStdout: Boolean = false,
		                 outputSql: File = null,
		                 outputJson: File = null,
		                 verbose: Boolean = false,
		                 skipEmpty: Boolean = false,
		                 moveFilesTo: File = null
	                 )

	val argParser = new scopt.OptionParser[Config]("ocd") {
		head("ocd", "0.0.1")

		opt[Unit]('5', "md5") optional() action { (_, c) => c.copy(md5 = true) } text "Use MD5 hashing to determine sameness (more exact)"

		opt[Unit]('c', "crc32") optional() action { (_, c) => c.copy(md5 = true) } text "Use CRC32 hashing to determine sameness (faster)"

		opt[Int]('b', "bytes") optional() valueName "<x>" action { (x, c) => c.copy(bytes = x) } text "Read upt tyo <x> bytes from the start of the files (default is read all)"

		opt[Unit]('p', "parallel") optional() action ((_, c) => c.copy(parallel = true)) text "Process some things in parallel (experimental)"

		opt[File]('s', "sql") optional() valueName "<sql-file>" action ((x, c) => c.copy(outputSql = x)) text "Output SQL (sqlite format) to <sql-file>"

		opt[Unit]('o', "stdout") optional() action ((_, c) => c.copy(outputStdout = true)) text "Output dupes to stdout"

		opt[File]('j', "json") optional() valueName "<json-file>" action ((x, c) => c.copy(outputJson = x)) text "Output dupes to <json-file> in JSON format"

		opt[Unit]('v', "verbose") optional() action ((_, c) => c.copy(verbose = true)) text "Enable verbose output"

		opt[Unit]('e', "skip-empty") optional() action ((_, c) => c.copy(skipEmpty = true)) text "Skip empty files"

		opt[File]('m', "move-to") optional() valueName "<move-to-dir>" action ((x, c) => c.copy(moveFilesTo = x)) text "Move duplicate files into subdirectories of <move-to-dir> (WARNING: flattens relative hierarchy)"

		arg[Seq[File]]("<scan-dir-1>[, <scan-dir-2>[, ...]]") required() action { (x, c) => c.copy(directories = x) } text "The directory in which to scan files (scans are recursive)"

		checkConfig(c =>
			if (c.md5 || c.crc32) success else failure("Please pick a hashing mode")
		)
	}

	argParser.parse(args, Config()) match {
		case Some(config) => {
			doScan(config)
		}


		case None =>
	}

	private def doScan(config: Config) = {
		val dupScanProvider = new DuplicateScannerProvider with DuplicateScannerComponent {
			override def getDuplicateScanner: DuplicateScanner = {
				var fullFileScanner: FullFileScanner with DuplicateScanner = new FullFileMd5DuplicateScanner
				var firstBytesScanner: FirstBytesScanner with DuplicateScanner = new FirstBytesMd5DuplicateScanner(config.bytes)

				if (config.md5) {
					// Default values
				} else if (config.crc32) {
					fullFileScanner = new FullFileCrc32DuplicateScanner
					firstBytesScanner = new FirstBytesCrc32DuplicateScanner(config.bytes)
				}

				if (config.bytes <= 0) {
					// Full file MD5
					fullFileScanner
				} else {
					// First-n bytes MD5
					firstBytesScanner
				}
			}
		}

		val dupes = dupScanProvider.getDuplicateScanner.findDuplicates(config)

		if (config.outputStdout) {
			new ConsoleOutputter().processDuplicates(dupes)
		}

		if (config.outputSql != null) {
			new SqlOutputter(config.outputSql).processDuplicates(dupes)
		}

		if (config.outputJson != null) {
			new JsonOutputter(config.outputJson).processDuplicates(dupes)
		}

		if (config.moveFilesTo != null) {
			new FileMover(config.moveFilesTo).processDuplicates(dupes)
		}
	}
}
