package org.ecorous.holly.reminders

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
object Reminders {
	val reminders: MutableList<Reminder> = ArrayList()

	suspend fun send(reminder: DiscordReminder, channel: MessageChannel): Message {
		return channel.createMessage(reminder.format())
	}

	val Duration.milliseconds: Long
		get() = this.inWholeMilliseconds

	init {
		val timer = Timer()
		timer.schedule(0, 10.seconds.milliseconds) { checkAll() }
	}

	fun schedule(reminder: Reminder) {
		reminders.add(reminder)
	}

	fun schedule(task: Runnable?, time: Instant) {
		reminders.add(Reminder(task, time))
	}

	fun checkAll() {
		reminders.stream().filter { it.isDue() }
			.forEach { GlobalScope.launch {
				it.run()
				if (it !is DiscordRepeatingReminder) {
					reminders.remove(it)
				}
			} }
	}

	fun cancel(reminder: Reminder) {
		reminders.remove(reminder)
	}

	suspend fun complete(reminder: CompletionReminder) {
		reminder.completed = true
		reminder.completionReminder?.let { cancel(it) }
//		DB.getConfig(TEST_SERVER_ID)?.remindersChannelId?.let {
//			bot.kordRef.getChannelOf<MessageChannel>(it)?.createMessage(reminder.onCompletion)
//		}
		reminder.reminderMessage?.edit(reminder.onCompletion)
	}
}

