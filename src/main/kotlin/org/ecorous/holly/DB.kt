package org.ecorous.holly

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object DB {
	val url = "jdbc:postgresql://localhost:5432/holly"
	lateinit var db: Database

	fun setup() {
		db = Database.connect(url, driver = "org.postgresql.Driver", user = "postgres", password = "example")
		transaction(db) {
			// create tables if they don't exist
			// create reminders table
			// create server config table
			SchemaUtils.create(Tables.Reminders)
			SchemaUtils.create(Tables.ServerConfig)
		}
	}

	fun getConfig(serverId: Snowflake): ServerConfig? {
		// get the server config for the server id
		// if it doesn't exist, return null
		return transaction(db) {
			Tables.ServerConfig.selectAll().where {
				Tables.ServerConfig.serverId eq serverId.value.toLong()
			}.map {
				ServerConfig.fromRow(it)
			}.firstOrNull()
		}
	}

	fun setConfig(serverConfig: ServerConfig) {
		transaction(db) {
			// if the server config exists, update it
			// if it doesn't exist, insert it
			Tables.ServerConfig.deleteWhere {
				serverId eq serverConfig.serverId.value.toLong()
			}
			Tables.ServerConfig.insert {
				it[serverId] = serverConfig.serverId.value.toLong()
				it[remindersChannelId] = serverConfig.remindersChannelId.value.toLong()
			}
		}
	}

}
