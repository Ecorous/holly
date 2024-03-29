package org.ecorous.holly.reminders

import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.modify.UserMessageModifyBuilder
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.ecorous.holly.Frequency

class CompletionReminderBuilder {
	var dueTime: Instant? = null
	var title: String? = null
	var message: String? = null
	var frequency: Frequency? = null
	var lastCompleted: LocalDateTime? = null
	var channel: MessageChannel? = null
	private var onCompletion: UserMessageModifyBuilder.() -> Unit = {}



	fun build(): CompletionReminder {
		return CompletionReminder(dueTime!!, title!!, message!!, frequency!!, lastCompleted!!, channel!!, onCompletion)
	}

	fun completion(c: UserMessageModifyBuilder.() -> Unit) {
		onCompletion = c
	}

}
