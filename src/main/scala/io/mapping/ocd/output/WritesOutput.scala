package io.mapping.ocd.output

import java.io.{FileWriter, BufferedWriter, File}

trait WritesOutput {
	def writeOutput(output: String, file: File) = {
		val bw = new BufferedWriter(new FileWriter(file))
		bw.write(output)
		bw.close()
	}
}
