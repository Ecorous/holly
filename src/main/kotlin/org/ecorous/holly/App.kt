/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.ecorous.holly

import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.datetime.*
import org.ecorous.holly.extensions.TestExtension
import org.ecorous.holly.reminders.*
import java.util.Timer
import kotlin.time.Duration.Companion.minutes

val TEST_SERVER_ID = Snowflake(
	env("TEST_SERVER").toLong()  // Get the test server ID from the env vars or a .env file
)

val USER_ID = Snowflake(
	env("USER_ID").toLong()  // Get the user ID from the env vars or a .env file
)

private val TOKEN = env("TOKEN")   // Get the bot' token from the env vars or a .env file

lateinit var bot: ExtensibleBot

@OptIn(DelicateCoroutinesApi::class)
suspend fun main() {
	// use while (true) { ... } to run forever
	// use delay(1000) to wait 1 second between each iteration

	DB.setup()
	bot = ExtensibleBot(TOKEN) {

		extensions {
			add(::TestExtension)
		}
	}

	/*Reminders.schedule(CompletionReminder.new {
		title = "Test Reminder (completion)"
		message = "This is a test reminder!"
		lastCompleted = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
		dueTime = Clock.System.now() + 2.minutes
		completion {
			embed {
				title = "Reminder completed!"
				description = "Your reminder \"${this@new.title}\" was completed."
				color = DISCORD_GREEN
			}
			actionRow {
				interactionButton(ButtonStyle.Success, "disabled") {
					label = "Complete"
					disabled = true
				}
			}
		}
	})*/
	bot.start()
}

val LocalDateTime.timeFormat: String // should be HH:MM:SS
	get() = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}"

val LocalDateTime.dateFormat: String// should be DD/MM/YYYY
	get() = "${dayOfMonth.toString().padStart(2, '0')}/${monthNumber.toString().padStart(2, '0')}/${year}"

val LocalDateTime.dateTimeFormat: String
	get() = "$dateFormat $timeFormat"
