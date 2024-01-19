package org.ecorous.holly

enum class FrequencyType {
	MINUTE,
	HOUR,
	DAY,
	WEEK,
	MONTH,
}

data class Frequency(val type: FrequencyType, val amount: Int) {
	fun toSeconds(): Int {
		return when (type) {
			FrequencyType.MINUTE -> amount * 60
			FrequencyType.HOUR -> amount * 60 * 60
			FrequencyType.DAY -> amount * 60 * 60 * 24
			FrequencyType.WEEK -> amount * 60 * 60 * 24 * 7
			FrequencyType.MONTH -> amount * 60 * 60 * 24 * 30
		}
	}
}
