package org.ecorous.holly.extensions

import com.kotlindiscord.kord.extensions.DISCORD_RED
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.channel
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingDefaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.components.*
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import dev.kord.common.Color
import dev.kord.rest.builder.message.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.ecorous.holly.*
import org.ecorous.holly.reminders.Reminders
import java.time.Instant

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
	}

	inner class ServerConfigArgs : Arguments() {
		val remindersChannel by channel {
			name = "reminders_channel"
			description = "The channel to send reminders in"
		}
	}
}
