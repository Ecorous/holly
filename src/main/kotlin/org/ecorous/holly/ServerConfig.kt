package org.ecorous.holly

import dev.kord.common.entity.Snowflake

data class ServerConfig(val serverId: Snowflake, val remindersChannelId: Snowflake) {
	companion object {
		fun fromRow(row: org.jetbrains.exposed.sql.ResultRow): ServerConfig {
			return ServerConfig(
				Snowflake(row[Tables.ServerConfig.serverId]),
				Snowflake(row[Tables.ServerConfig.remindersChannelId])
			)
		}
	}
}
