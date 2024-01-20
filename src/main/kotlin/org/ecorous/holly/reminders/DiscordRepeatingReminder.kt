package org.ecorous.holly.reminders

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.ecorous.holly.Frequency
import kotlin.time.Duration.Companion.seconds

open class DiscordRepeatingReminder(
	dueTime: Instant,
	title: String,
	message: String,
	frequency: Frequency,
	lastCompleted: LocalDateTime
) : DiscordReminder(dueTime, title, message, frequency, lastCompleted) {
	override suspend fun run() {
		super.run()
		dueTime += frequency.toSeconds().seconds
	}
	companion object {
		fun new(block: DiscordReminderBuilder.() -> Unit): DiscordRepeatingReminder {
			val builder = DiscordReminderBuilder()
			builder.block()
			return builder.buildRepeating()
		}
	}
}
