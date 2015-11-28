package io.mapping.ocd

import java.io.File

import io.mapping.ocd.component.DuplicateScannerComponent
import io.mapping.ocd.provider.DuplicateScannerProvider
import io.mapping.ocd.scanner.{FirstBytesMd5DuplicateScanner, FullFileMd5DuplicateScanner, DuplicateScanner}

object OCD extends App {
	case class Config(directory: File = new File("/"), md5: Boolean = false, bytes: Int = 0)

	val argParser = new scopt.OptionParser[Config]("ocd") {
		head("ocd", "0.0.1")

		opt[Unit]('5', "md5") optional() action { (_, c) => c.copy(md5 = true) }

		opt[Int]('b', "bytes") optional() action { (x, c) => c.copy(bytes = x) }

		arg[File]("<directory>") required() action { (x, c) => c.copy(directory = x) }
	}

	argParser.parse(args, Config()) match {
		case Some(config) => {
			if (config.md5 || config.bytes > 0) {
				 doScan(config)
			}
		}


		case None =>
	}

	private def doScan(config: Config) = {
		val dupScanProvider = new DuplicateScannerProvider with DuplicateScannerComponent {
			override def getDuplicateScanner: DuplicateScanner = {
				if (config.md5) {
					new FullFileMd5DuplicateScanner
				} else if (config.bytes > 0) {
					new FirstBytesMd5DuplicateScanner(config.bytes)
				} else {
					new FullFileMd5DuplicateScanner
				}
			}
		}

		for (pair <- dupScanProvider.getDuplicateScanner.findDuplicates(config.directory)) {
			Console.println("Duplicate set " + pair._1 + ":")

			for (file <- pair._2) {
				Console.println("\t" + file.getAbsolutePath)
			}

			Console.println()
		}
	}
}
