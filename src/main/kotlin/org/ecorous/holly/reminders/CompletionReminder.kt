package org.ecorous.holly.reminders

import com.kotlindiscord.kord.extensions.DISCORD_RED
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.embed
import dev.kord.rest.builder.message.modify.UserMessageModifyBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.ecorous.holly.*
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

@OptIn(DelicateCoroutinesApi::class)
class CompletionReminder(
	dueTime: Instant,
	title: String,
	message: String,
	frequency: Frequency,
	lastCompleted: LocalDateTime,
	val onCompletion: UserMessageModifyBuilder.() -> Unit
) : DiscordRepeatingReminder(dueTime, title, message, frequency, lastCompleted) {
	var completed = false
	var completionReminder: Reminder? = null
	var buttonId: String = ""
	/*constructor(
		dueTime: Instant?,
		title: String?,
		message: String?,
		frequency: Frequency?,
		lastCompleted: LocalDateTime?
	) {
		super(dueTime, title, message, frequency, lastCompleted)
		schedule(Reminder({
			if (!completed) {
				// remind...
			}
		}, Instant.now().plusSeconds(5 * 60)).also { completionReminder = it })
	}*/

	fun refreshButtonId(): String {
		return "reminder_complete_${Random(324).nextInt(0, 200000)}"
	}

	init {

	}

	override fun format(): MessageCreateBuilder.() -> Unit {
		return {
			super.format()()
			formatComplete()()
		}
	}

	fun formatComplete(): MessageCreateBuilder.() -> Unit {
		return {
			actionRow {
				interactionButton(ButtonStyle.Success, buttonId) {
					label = "Complete"
				}
			}
		}
	}

	fun scheduleCompletionReminder() {
		println("Scheduling completion reminder!")
		val reminderReminderTime = Clock.System.now() + 1.minutes
		println(reminderReminderTime.toLocalDateTime(TimeZone.currentSystemDefault()).dateTimeFormat)
		Reminders.schedule(Reminder({
			println("Reminder might be completed!")
			if (!completed) {
				println("Reminder not completed!")
				DB.getConfig(TEST_SERVER_ID)?.remindersChannelId?.let {
					println(it.value)
					GlobalScope.launch {
						reminderMessage?.let { message ->
							message.reply {
								content = "<@$USER_ID>"
								embed {
									title = "Reminder not completed!"
									description =
										"Your reminder \"${this@CompletionReminder.title}\" was not completed. Please complete it as soon as possible."
									color = DISCORD_RED
								}
							}
							scheduleCompletionReminder()
						} ?: bot.kordRef.getChannelOf<TextChannel>(it)?.let { channel ->
							println(channel.name)
							channel.createMessage {
								content = "<@$USER_ID>"
								embed {
									this@embed.title = "Reminder not completed!"
									description =
										"Your reminder \"${title}\" was not completed. Please complete it as soon as possible."
									color = DISCORD_RED
									footer {
										text = "Notice: could not find the original message."
									}
								}
							}
						}


					}
				}
			}
		}, reminderReminderTime).also { completionReminder = it })
	}

	override suspend fun run() {
		buttonId = refreshButtonId()
		super.run()
		completionReminder?.let { Reminders.cancel(it) }
		scheduleCompletionReminder()
	}

	companion object {
		fun new(block: CompletionReminderBuilder.() -> Unit): CompletionReminder {
			val builder = CompletionReminderBuilder()
			builder.block()
			return builder.build()
		}
	}

}
