package org.ecorous.holly.reminders

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

open class Reminder(val task: Runnable?, var dueTime: Instant) {
	open suspend fun run() {
		task?.run()
	}

	open fun isDue(): Boolean {
		return dueTime <= (Clock.System.now())
	}
}
