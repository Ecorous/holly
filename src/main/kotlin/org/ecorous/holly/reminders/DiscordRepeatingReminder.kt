package org.ecorous.holly.reminders

import com.kotlindiscord.kord.extensions.DISCORD_PINK
import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.ecorous.holly.Frequency
import org.ecorous.holly.USER_ID
import org.ecorous.holly.dateTimeFormat
import kotlin.time.Duration.Companion.seconds

open class DiscordRepeatingReminder(
	dueTime: Instant,
	title: String,
	message: String,
	val frequency: Frequency,
	lastCompleted: LocalDateTime
) : DiscordReminder(dueTime, title, message, lastCompleted) {
	override suspend fun run() {
		super.run()
		dueTime += frequency.toSeconds().seconds
	}

	override fun format(): MessageCreateBuilder.() -> Unit {
		return {
			content = "<@$USER_ID>"
			embed {
				title = this@DiscordRepeatingReminder.title
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
					value = "${dueTime.toDiscord(TimestampType.Default)} (${dueTime.toDiscord(
						TimestampType.RelativeTime)})"
				}
				field {
					name = "Frequency"
					value = frequency.toString()
				}
				color = DISCORD_PINK
			}


		};
	}
	companion object {
		fun new(block: DiscordReminderBuilder.() -> Unit): DiscordRepeatingReminder {
			val builder = DiscordReminderBuilder()
			builder.block()
			return builder.buildRepeating()
		}
	}
}
