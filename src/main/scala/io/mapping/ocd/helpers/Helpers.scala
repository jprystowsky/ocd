package io.mapping.ocd.helpers

object Helpers {
	// Thanks to https://stackoverflow.com/questions/10763730/hex-string-to-int-short-and-long-in-scala
	def hex2dec(hex: String): BigInt = {
		hex.toLowerCase().toList.map(
			"0123456789abcdef".indexOf(_)).map(
			BigInt(_)).reduceLeft( _ * 16 + _)
	}
}
