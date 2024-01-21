package org.ecorous.holly.reminders

import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.EmbedFieldRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ecorous.holly.dateTimeFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
object Reminders {
	val reminders = mutableListOf<Reminder>()

	suspend fun send(reminder: DiscordReminder, channel: MessageChannel): Message {
		return channel.createMessage(reminder.format())
	}

	val Duration.milliseconds: Long
		get() = this.inWholeMilliseconds

	init {
		val timer = Timer()
		timer.scheduleAtFixedRate(0, 10.seconds.milliseconds) { checkAll() }
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
		if (reminder is CompletionReminder) {
			reminder.completionReminder?.let { cancel(it) }
		}
	}

	suspend fun complete(reminder: CompletionReminder) {
		reminder.completed = true
		reminder.completionReminder?.let { cancel(it) }
//		DB.getConfig(TEST_SERVER_ID)?.remindersChannelId?.let {
//			bot.kordRef.getChannelOf<MessageChannel>(it)?.createMessage(reminder.onCompletion)
//		}
		reminder.reminderMessage?.edit(reminder.onCompletion)
	}

	fun getRemindersList(): MutableList<EmbedBuilder.Field> {
		val list = mutableListOf<EmbedBuilder.Field>()

		if (reminders.isEmpty()){
			val f: EmbedBuilder.Field = EmbedBuilder.Field()
			f.value = "No registered reminders!"
			list.add(f)
			return list
		}

		val header: EmbedBuilder.Field = EmbedBuilder.Field()
		header.value = "`id`: `time`: `title`"
		list.add(header)
		reminders.filterIsInstance<DiscordReminder>().forEachIndexed { index, reminder ->
			val f: EmbedBuilder.Field = EmbedBuilder.Field()
			f.value = "`$index`: ${reminder.dueTime.toDiscord(TimestampType.Default)} (${reminder.dueTime.toDiscord(TimestampType.RelativeTime)}): " +
				reminder.title
			list.add(f)
		}
		return list
	}

	fun remove(index: Int) {
		cancel(reminders[index])
	}
}

