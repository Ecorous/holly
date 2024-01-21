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
			FrequencyType.MONTH -> amount * 60 * 60 * 24 * Clock.System.now()
				.toLocalDateTime(TimeZone.currentSystemDefault()).month.length(isLeapYear(Clock.System.now().toLocalDateTime(
					TimeZone.currentSystemDefault()).year))
		}
	}

	private fun isLeapYear(year: Int): Boolean {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
	}

	override fun toString(): String {
		var int = amount
		var string = ""

		val mLength = (60*60*24*Clock.System.now()
			.toLocalDateTime(TimeZone.currentSystemDefault()).month.length(isLeapYear(Clock.System.now().toLocalDateTime(
				TimeZone.currentSystemDefault()).year)))
		val wLength = 60*60*24*7
		val dLength = 60*60*24
		val hLength = 3600
		if (int > mLength){
			string+="${int/mLength} month"
			if (int/mLength > 1){
				string+="s"
			}
			int %= mLength
		}
		if (int > wLength){
			if (string.isNotEmpty()){
				string+=", "
			}
			string+="${int/wLength} week"
			if (int/wLength > 1){
				string+="s"
			}
			int %= wLength
		}
		if (int > dLength){
			if (string.isNotEmpty()){
				string+=", "
			}
			string+="${int/dLength} day"
			if (int/dLength > 1){
				string+="s"
			}
			int %= dLength
		}
		if (int > hLength){
			if (string.isNotEmpty()){
				string+=", "
			}
			string+="${int/hLength} hour"
			if (int/hLength > 1){
				string+="s"
			}
			int %= wLength
		}
		if (int > 60){
			if (string.isNotEmpty()){
				string+=", "
			}
			string+="${int/60} minute"
			if (int/60 > 1){
				string+="s"
			}
			int %= 60
		}
		if (int > 0){
			if (string.isNotEmpty()){
				string+=", "
			}
			string += "$int second"
			if (int > 1){
				string+="s"
			}
		}

		return string
	}

	companion object {
		fun ofDateTimePeriod(period: DateTimePeriod): Frequency {
			return Frequency(FrequencyType.SECOND, period.toDuration(TimeZone.currentSystemDefault()).inWholeSeconds)
		}
	}
}
