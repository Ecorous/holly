package org.ecorous.holly.reminders
/*

import com.kotlindiscord.kord.extensions.DISCORD_PINK
import dev.kord.rest.builder.message.allowedMentions
import dev.kord.rest.builder.message.create.MessageCreateBuilder
import dev.kord.rest.builder.message.embed
import org.ecorous.holly.Frequency
import org.ecorous.holly.FrequencyType
import org.ecorous.holly.USER_ID
import java.time.LocalDateTime

object DailyReminder : Reminder {
	override val title = "Daily Reminder!"
	override val message = "This is Daily!"
	override val frequency = Frequency(FrequencyType.MINUTE, 2)
	override var lastCompleted: LocalDateTime? = null
	override var nextDue: LocalDateTime? = null

	override fun format(): MessageCreateBuilder.() -> Unit {
		return {
			content = "<@${USER_ID}>"
			allowedMentions {
				users.add(USER_ID)
			}
			embed {
				title = "Daily Reminder!"
				description = "This is shown daily!"
				color = DISCORD_PINK
			}
		}
	}
}*/
