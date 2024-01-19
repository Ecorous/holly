package org.ecorous.holly.reminders

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.ecorous.holly.Frequency

class DiscordReminderBuilder {
	var dueTime: Instant? = null
	var title: String? = null
	var message: String? = null
	var frequency: Frequency? = null
	var lastCompleted: LocalDateTime? = null



	fun build(): DiscordReminder {
		return DiscordReminder(dueTime!!, title!!, message!!, frequency!!, lastCompleted!!)
	}
	fun buildRepeating(): DiscordRepeatingReminder {
		return DiscordRepeatingReminder(dueTime!!, title!!, message!!, frequency!!, lastCompleted!!)
	}


}
