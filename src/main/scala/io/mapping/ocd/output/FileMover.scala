package io.mapping.ocd.output

import java.io.File

import io.mapping.ocd.fingerprint.Fingerprint

import scala.collection.mutable

import java.nio.file.StandardCopyOption.ATOMIC_MOVE

class FileMover(destinationDir: File) extends ProcessesDuplicates {

	private def ensureExists(destinationDir: File): Unit = {
		if (destinationDir.exists) {
			if (!destinationDir.isDirectory) {
				throw new Throwable("Destination directory isn't a directory")
			} else if (!destinationDir.canWrite) {
				throw new Throwable("Can't write to destination directory")
			}
		} else {
			destinationDir.mkdirs
		}
	}

	override def processDuplicates(dupes: mutable.HashMap[Fingerprint, List[File]]): Unit = {
		ensureExists(destinationDir)

		for ((fp, f) <- dupes) {
			val groupDir = new File(destinationDir, fp.toString)
			groupDir.mkdirs

			for (file <- f) {
				java.nio.file.Files.move(file.toPath, new File(groupDir, file.getName).toPath, ATOMIC_MOVE)
			}
		}
	}
}
