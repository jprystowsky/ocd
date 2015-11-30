package io.mapping.ocd.output

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint
import org.json4s.jackson.Serialization

import scala.collection.mutable

class JsonOutputter(file: File) extends ProcessesDuplicates with WritesOutput {
	implicit val formats = org.json4s.DefaultFormats

	override def processDuplicates(dupes: mutable.HashMap[Fingerprint, List[File]]): Unit = {
		val string = Serialization.write(dupes.map(x => (x._1.toString, x._2.map(_.getAbsolutePath))))

		writeOutput(string, file)
	}
}
