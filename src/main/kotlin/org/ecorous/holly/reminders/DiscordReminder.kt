package org.ecorous.holly.reminders

import com.kotlindiscord.kord.extensions.DISCORD_PINK
import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.publicButton
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.MessageBehavior
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

	var reminderMessage: Message? = null
	@OptIn(DelicateCoroutinesApi::class)
	open fun format(): MessageCreateBuilder.() -> Unit {
		return {
			content = "<@$USER_ID>"
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
				color = DISCORD_PINK
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
				reminderMessage = Reminders.send(this, channel)
			}
		}
	}
}
