package io.mapping.ocd.output

object ConsoleMessage {
	var lastMessage: String = ""

	def printMessage(s: String): Unit = {
		clearLastMessage()

		lastMessage = s
		Console.println(s)
		Console.flush()
	}

	private def clearLastMessage(): Unit = {
		val clearStr = "\b \b" * (lastMessage.length + 1)

		if (lastMessage.length > 0) {
			Console.println(clearStr)
			Console.flush()
		}
	}
}
