package com.partiful.invitemaker.data.service

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.partiful.invitemaker.domain.model.PartyEvent

class CalendarService(private val context: Context) {

    /**
     * Add event to Calendar using standard Android Calendar Intent
     * Safe, reliable, and does not require dangerous calendar permissions on all devices.
     */
    fun addEventViaIntent(event: PartyEvent): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, "${event.eventDescription}\n\n${event.notes}")
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTimestamp)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTimestamp)
                if (event.hasReminder) {
                    putExtra(CalendarContract.Reminders.MINUTES, event.reminderMinutesBefore)
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Direct Calendar Provider insertion if permission is granted
     */
    fun insertEventDirectly(calendarId: Long = 1, event: PartyEvent): Long? {
        return try {
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, event.startTimestamp)
                put(CalendarContract.Events.DTEND, event.endTimestamp)
                put(CalendarContract.Events.TITLE, event.title)
                put(CalendarContract.Events.DESCRIPTION, "${event.eventDescription}\n\n${event.notes}")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, java.util.TimeZone.getDefault().id)
                put(CalendarContract.Events.EVENT_LOCATION, event.location)
                put(CalendarContract.Events.HAS_ALARM, if (event.hasReminder) 1 else 0)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()

            if (eventId != null && event.hasReminder) {
                val reminderValues = ContentValues().apply {
                    put(CalendarContract.Reminders.EVENT_ID, eventId)
                    put(CalendarContract.Reminders.MINUTES, event.reminderMinutesBefore)
                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
                }
                context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
            }
            eventId
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
