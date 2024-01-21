package org.ecorous.holly

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Tables {
	// we need a reminders table
	// we need a server config table

	object Reminders: Table() {

	}

	object ServerConfig: Table() {
		val serverId = long("server_id")
		val remindersChannelId = long("reminders_channel_id")

		override val primaryKey = PrimaryKey(serverId)
	}
}

