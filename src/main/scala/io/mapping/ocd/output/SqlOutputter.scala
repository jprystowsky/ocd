package io.mapping.ocd.output

import java.io.{BufferedWriter, FileWriter, File}

import io.mapping.ocd.fingerprint.Fingerprint

import scala.collection.mutable

class SqlOutputter(outputFile: File) extends GeneratesOutput {
	private def getBaseSql =
		s"""
		   |drop table if exists fingerprint;
		   |create table fingerprint (
		   |id integer primary key autoincrement not null unique,
		   |fingerprint text not null collate nocase
		   |);
		   |
		   |drop table if exists fingerprint_file;
		   |create table fingerprint_file (
		   |id integer primary key autoincrement not null unique,
		   |fingerprintId integer not null,
		   |path text not null collate nocase,
		   |foreign key (fingerprintId) references fingerprint (id)
		   |);
		""".stripMargin

	private def getDupeKeySql(key: Fingerprint) =
		s"""
		   |insert into fingerprint (fingerprint) values ('${key.toString}');
		""".stripMargin

	private def getDupeKeyValueSql(key: Fingerprint, value: String) =
		s"""
		  |insert into fingerprint_file (fingerprintId, path) values (
		  |(select id from fingerprint where fingerprint = '${key.toString}'),
		  |'$value');
		""".stripMargin

	override def generateOutput(dupes: mutable.HashMap[Fingerprint, List[File]]): Unit = {
		val sb = new StringBuilder(getBaseSql)
		for (key <- dupes.keySet) {
			sb ++= getDupeKeySql(key)
		}

		for (pair <- dupes) {
			for (file <- pair._2) {
				sb ++= getDupeKeyValueSql(pair._1, file.getAbsolutePath)
			}
		}

		val bw = new BufferedWriter(new FileWriter(outputFile))
		bw.write(sb.toString)
		bw.close()
	}
}
