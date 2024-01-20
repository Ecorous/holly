package org.ecorous.holly.extensions

import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ButtonInteractionCreateEvent
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.embed
import org.ecorous.holly.DB
import org.ecorous.holly.ServerConfig
import org.ecorous.holly.TEST_SERVER_ID
import org.ecorous.holly.reminders.CompletionReminder
import org.ecorous.holly.reminders.Reminders

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
		event<ButtonInteractionCreateEvent> {
			action {
				println("hewwo! ${event.interaction.componentId}, ${event.interaction.componentType}")
				Reminders.reminders.filter { it is CompletionReminder  }.filter {
					val r = it as? CompletionReminder
					if (r != null) {
						r.buttonId == event.interaction.componentId
					} else {
						false
					}
				}.forEach { reminder ->
					val r = reminder as? CompletionReminder
					if (r != null) {
						println("r1")
						println(r.buttonId)
						if (reminder.completed) {
							event.interaction.respondEphemeral { content = "Reminder already completed!" }
							return@action
						}
						Reminders.complete(r)
						event.interaction.respondEphemeral { content = "Completed reminder!" }
					}
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
}
