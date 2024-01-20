package org.ecorous.holly

import com.kotlindiscord.kord.extensions.utils.toDuration
import kotlinx.datetime.*

enum class FrequencyType {
	SECOND,
	MINUTE,
	HOUR,
	DAY,
	WEEK,
	MONTH,
}

data class Frequency(val type: FrequencyType, val amount: Long) {
	fun toSeconds(): Long {
		return when (type) {
			FrequencyType.SECOND -> amount
			FrequencyType.MINUTE -> amount * 60
			FrequencyType.HOUR -> amount * 60 * 60
			FrequencyType.DAY -> amount * 60 * 60 * 24
			FrequencyType.WEEK -> amount * 60 * 60 * 24 * 7
			FrequencyType.MONTH -> amount * 60 * 60 * 24 * 30
		}
	}

	companion object {
		fun ofDateTimePeriod(period: DateTimePeriod): Frequency {
			return Frequency(FrequencyType.SECOND, period.toDuration(TimeZone.currentSystemDefault()).inWholeSeconds)
		}
	}
}
