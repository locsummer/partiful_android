package com.partiful.invitemaker.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PartyEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Summer Rooftop Party",
    val eventDescription: String = "Join us for drinks, music, and great vibes under the city lights!",
    val startTimestamp: Long = System.currentTimeMillis() + 86400000L * 2,
    val endTimestamp: Long = System.currentTimeMillis() + 86400000L * 2 + 14400000L,
    val location: String = "123 Skyline Avenue, Rooftop Bar",
    val notes: String = "Dress code: Casual chic. BYOB welcome!",
    val hasReminder: Boolean = true,
    val reminderMinutesBefore: Int = 120,
    val isSavedToCalendar: Boolean = false
)
