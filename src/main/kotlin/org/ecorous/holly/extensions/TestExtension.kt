package org.ecorous.holly.extensions

import com.kotlindiscord.kord.extensions.DISCORD_FUCHSIA
import com.kotlindiscord.kord.extensions.DISCORD_GREEN
import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.DISCORD_YELLOW
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.optionalStringChoice
import com.kotlindiscord.kord.extensions.commands.application.slash.converters.impl.stringChoice
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.*
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralSlashCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import com.kotlindiscord.kord.extensions.utils.toDuration
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.actionRow
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ecorous.holly.*
import org.ecorous.holly.reminders.*

val SERVER_CONFIG_ERROR: EmbedBuilder.() -> Unit = {
	title = "Error"
	description = "Server config not found"
	color = DISCORD_RED
}

class TestExtension : Extension() {
	override val name = "test"

	override suspend fun setup() {

		publicSlashCommand {
			name = "check"
			description = "Check all reminders"
			guild(TEST_SERVER_ID)
			action {
				Reminders.checkAll()
				respond {
					content = "Checked all reminders"
				}
			}
		}
		publicSlashCommand {
			name = "config"
			description = "Server config commands"
			guild(TEST_SERVER_ID)
			publicSubCommand(::ServerConfigArgs) {
				name = "set"
				description = "Set server config"
				guild(TEST_SERVER_ID)
				action {
					DB.setConfig(ServerConfig(guild!!.id, arguments.remindersChannel.id))
					respond {
						embed {
							title = "Server config"
							description = "Successfully set server config"
							field {
								name = "Reminders channel"
								value = arguments.remindersChannel.mention
							}
						}
					}
				}
			}
		}
		ephemeralSlashCommand {
			name = "reminder"
			description = "Configure reminders"
			ephemeralSubCommand(::AddReminderArgs) {
				name = "add"
				description = "Add a new reminder"
				action {
					val dueTime = Clock.System.now().plus(arguments.time.toDuration(TimeZone.currentSystemDefault()))
					if (arguments.mode == "completion") {
						if (arguments.repeatingInterval == null) {
							respond {
								embed {
									title = "Failed to register reminder!"
									description = "Completion reminders require a repeating interval to be set!"
								}
							}
							return@action
						}
						Reminders.schedule(CompletionReminder(
							dueTime,
							arguments.title,
							arguments.message.replace("@USER@", user.mention),
							Frequency.ofDateTimePeriod(arguments.repeatingInterval!!),
							Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
							event.interaction.channel.asChannel(),
							onCompletion = {
								embed {
									title = "Reminder completed!"
									description = "Your reminder \"${arguments.title}\" was completed."
									color = DISCORD_GREEN
								}
								actionRow {
									interactionButton(ButtonStyle.Success, "disabled") {
										label = "Complete"
										disabled = true
									}
								}
							}
					} else {
						if (arguments.mode == "repeating") {
							if (arguments.repeatingInterval == null) {
								respond {
									embed {
										title = "Failed to register reminder!"
										description = "Repeating reminders require a repeating interval to be set!"
									}
								}
								return@action
							}
							Reminders.schedule(
								DiscordRepeatingReminder(
									dueTime,
									arguments.title,
									arguments.message.replace("@USER@", user.mention),
									Frequency.ofDateTimePeriod(arguments.repeatingInterval!!),
									Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
									event.interaction.channel.asChannel()
								)
							)
						} else {
							Reminders.schedule(
								DiscordReminder(
									dueTime,
									arguments.title,
									arguments.message.replace("@USER@", user.mention),
									Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
									event.interaction.channel.asChannel()
								)
							)
						}
					}
					respond {
						embed {
							title = "Reminder Scheduled!"
							field {
								name = "Title"
								value = arguments.title
							}
							field {
								name = "Message"
								value = arguments.message.replace("@USER@", user.mention)
							}
							field {
								name = "Due Time"
								value = dueTime.toDiscord(TimestampType.Default) + " (${dueTime.toDiscord(TimestampType.RelativeTime)})"
							}
							if (arguments.repeatingInterval != null) {
								field {
									name = "Frequency"
									value = Frequency.ofDateTimePeriod(arguments.repeatingInterval!!).toString()
								}
							}
							color = DISCORD_YELLOW
						}
					}
				}
			}
			ephemeralSubCommand {
				name = "list"
				description = "List Reminders"
				action {
					respond {
						embed {
							title = "Registered Reminders"
							fields.addAll(Reminders.getRemindersList())
							color = DISCORD_FUCHSIA
						}
					}
				}
			}
			ephemeralSubCommand(::RemoveReminderArgs){
				name = "remove"
				description = "remove a reminder"
				action {
					Reminders.remove(arguments.reminderId.toInt())
					respond {
						content = "Reminder removed!"
					}
				}
			}

		}
		event<ButtonInteractionCreateEvent> {
			action {
				println("hewwo! ${event.interaction.componentId}, ${event.interaction.componentType}")
				Reminders.reminders.filterIsInstance<CompletionReminder>().filter {
					it.buttonId == event.interaction.componentId
				}.forEach { reminder ->
					println("r1")
					println(reminder.buttonId)
					if (reminder.completed) {
						event.interaction.respondEphemeral { content = "Reminder already completed!" }
						return@action
					}
					Reminders.complete(reminder)
					event.interaction.respondEphemeral { content = "Completed reminder!" }
					return@action

				}
			}
		}

	}

	inner class ServerConfigArgs : Arguments() {
		val remindersChannel by channel {
			name = "reminders_channel"
			description = "The channel to send reminders in"
		}
	}

	inner class AddReminderArgs : Arguments() {
		val time by coalescingDuration {
			name = "time"
			description = "Time of reminding"
		}
		val title by defaultingString {
			name = "title"
			description = "Reminder Title"
			defaultValue = "Reminder"
		}
		val message by defaultingString {
			name = "message"
			description = "Reminder Message"
			defaultValue = "Reminder for @USER@"
		}
		val repeatingInterval by coalescingOptionalDuration {
			name = "repeat_interval"
			description = "Interval to remind you again, to use set the mode to repeating"
		}
		val mode by optionalStringChoice {
			name = "mode"
			description = "Which reminder mode this reminder should have"
			choices = mutableMapOf(
				"default" to "default",
				"repeating" to "repeating",
				"completion" to "completion"
			)
		}
	}

	inner class RemoveReminderArgs : Arguments() {
		val reminderId by long {
			name = "reminder_id"
			description = "The id of the reminder to remove"
		}
	}
}
