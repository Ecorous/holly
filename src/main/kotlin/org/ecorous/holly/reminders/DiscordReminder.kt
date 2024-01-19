package org.ecorous.holly.reminders

import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ecorous.holly.*

open class DiscordReminder(
	dueTime: Instant,
	val title: String,
	val message: String,
	val frequency: Frequency,
	val lastCompleted: LocalDateTime
) : Reminder(null, dueTime) {
	fun format(): MessageCreateBuilder.() -> Unit {
		return {
			embed {
				title = this@DiscordReminder.title
				description = message
				field {
					name = "Frequency"
					value = frequency.toString()
				}
				field {
					name = "Last completed"
					value = lastCompleted.dateTimeFormat
				}
				field {
					name = "Due time"
					value = dueTime.toLocalDateTime(TimeZone.currentSystemDefault()).dateTimeFormat
				}
			}
		};
	}

	companion object {
		fun new(block: DiscordReminderBuilder.() -> Unit): DiscordReminder {
			val builder = DiscordReminderBuilder()
			builder.block()
			return builder.build()
		}
	}


	override suspend fun run() {
		DB.getConfig(TEST_SERVER_ID)?.remindersChannelId?.let {
			bot.kordRef.getChannelOf<MessageChannel>(it)?.let { channel ->
				Reminders.send(this, channel)
			}
		}
	}
}
