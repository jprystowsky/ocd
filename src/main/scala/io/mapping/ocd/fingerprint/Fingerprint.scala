package io.mapping.ocd.fingerprint

trait Fingerprint {
	var value: String

	override def equals(obj: scala.Any): Boolean = {
		obj match {
			case f: Fingerprint => this.value.equals(f.value)
			case _ => false
		}
	}

	override def hashCode(): Int = value.hashCode

	override def toString: String = value.toString
}
