package io.mapping.ocd

import java.io.File

import io.mapping.ocd.component.DuplicateScannerComponent
import io.mapping.ocd.output.{JsonOutputter, SqlOutputter, ConsoleOutputter}
import io.mapping.ocd.provider.DuplicateScannerProvider
import io.mapping.ocd.scanner._

object OCD extends App {

	case class Config(
		                 directory: File = new File("/"),
		                 md5: Boolean = false,
		                 crc32: Boolean = false,
		                 bytes: Int = 0,
		                 parallel: Boolean = false,
		                 outputStdout: Boolean = false,
		                 outputSql: File = null,
		                 outputJson: File = null
	                 )

	val argParser = new scopt.OptionParser[Config]("ocd") {
		head("ocd", "0.0.1")

		opt[Unit]('5', "md5") optional() action { (_, c) => c.copy(md5 = true) } text "Use MD5 hashing to determine sameness (more exact, slower)"

		opt[Unit]('c', "crc32") optional() action { (_, c) => c.copy(md5 = true) } text "Use CRC32 hashing to determine sameness (faster, less exact)"

		opt[Int]('b', "bytes") optional() valueName "<x>" action { (x, c) => c.copy(bytes = x) } text "Read <x> bytes from the start of the files (default is read all)"

		opt[Unit]('p', "parallel") optional() action ((_, c) => c.copy(parallel = true)) text "Process in parallel (faster, may hang)"

		opt[File]('s', "sql") optional() valueName "<x file>" action ((x, c) => c.copy(outputSql = x)) text "Output SQL (sqlite format) to <x file>"

		opt[Unit]('o', "stdout") optional() action ((_, c) => c.copy(outputStdout = true)) text "Output dupes to stdout"

		opt[File]('j', "json") optional() valueName "<x file>" action ((x, c) => c.copy(outputJson = x)) text "Output dupes to <x file> in JSON format"

		arg[File]("<directory>") required() action { (x, c) => c.copy(directory = x) } text "The directory in which to scan files (scans are recursive)"

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
			new ConsoleOutputter().generateOutput(dupes)
		}

		if (config.outputSql != null) {
			new SqlOutputter(config.outputSql).generateOutput(dupes)
		}

		if (config.outputJson != null) {
			new JsonOutputter(config.outputJson).generateOutput(dupes)
		}
	}
}
