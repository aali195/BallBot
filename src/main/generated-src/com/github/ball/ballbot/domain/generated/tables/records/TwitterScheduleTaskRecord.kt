/*
 * This file is generated by jOOQ.
 */
package com.github.ball.ballbot.domain.generated.tables.records


import com.github.ball.ballbot.domain.generated.tables.TwitterScheduleTask

import java.time.OffsetDateTime

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record9
import org.jooq.Row9
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class TwitterScheduleTaskRecord() : UpdatableRecordImpl<TwitterScheduleTaskRecord>(TwitterScheduleTask.TWITTER_SCHEDULE_TASK), Record9<Long?, OffsetDateTime?, String?, String?, String?, String?, Long?, Boolean?, String?> {

    var id: Long?
        set(value): Unit = set(0, value)
        get(): Long? = get(0) as Long?

    var added: OffsetDateTime?
        set(value): Unit = set(1, value)
        get(): OffsetDateTime? = get(1) as OffsetDateTime?

    var guildId: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    var channelId: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    var uploaderId: String?
        set(value): Unit = set(4, value)
        get(): String? = get(4) as String?

    var urlName: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    var updateInterval: Long?
        set(value): Unit = set(6, value)
        get(): Long? = get(6) as Long?

    var mediaOnly: Boolean?
        set(value): Unit = set(7, value)
        get(): Boolean? = get(7) as Boolean?

    var description: String?
        set(value): Unit = set(8, value)
        get(): String? = get(8) as String?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Long?> = super.key() as Record1<Long?>

    // -------------------------------------------------------------------------
    // Record9 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row9<Long?, OffsetDateTime?, String?, String?, String?, String?, Long?, Boolean?, String?> = super.fieldsRow() as Row9<Long?, OffsetDateTime?, String?, String?, String?, String?, Long?, Boolean?, String?>
    override fun valuesRow(): Row9<Long?, OffsetDateTime?, String?, String?, String?, String?, Long?, Boolean?, String?> = super.valuesRow() as Row9<Long?, OffsetDateTime?, String?, String?, String?, String?, Long?, Boolean?, String?>
    override fun field1(): Field<Long?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.ID
    override fun field2(): Field<OffsetDateTime?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.ADDED
    override fun field3(): Field<String?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.GUILD_ID
    override fun field4(): Field<String?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.CHANNEL_ID
    override fun field5(): Field<String?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.UPLOADER_ID
    override fun field6(): Field<String?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.URL_NAME
    override fun field7(): Field<Long?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.UPDATE_INTERVAL
    override fun field8(): Field<Boolean?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.MEDIA_ONLY
    override fun field9(): Field<String?> = TwitterScheduleTask.TWITTER_SCHEDULE_TASK.DESCRIPTION
    override fun component1(): Long? = id
    override fun component2(): OffsetDateTime? = added
    override fun component3(): String? = guildId
    override fun component4(): String? = channelId
    override fun component5(): String? = uploaderId
    override fun component6(): String? = urlName
    override fun component7(): Long? = updateInterval
    override fun component8(): Boolean? = mediaOnly
    override fun component9(): String? = description
    override fun value1(): Long? = id
    override fun value2(): OffsetDateTime? = added
    override fun value3(): String? = guildId
    override fun value4(): String? = channelId
    override fun value5(): String? = uploaderId
    override fun value6(): String? = urlName
    override fun value7(): Long? = updateInterval
    override fun value8(): Boolean? = mediaOnly
    override fun value9(): String? = description

    override fun value1(value: Long?): TwitterScheduleTaskRecord {
        this.id = value
        return this
    }

    override fun value2(value: OffsetDateTime?): TwitterScheduleTaskRecord {
        this.added = value
        return this
    }

    override fun value3(value: String?): TwitterScheduleTaskRecord {
        this.guildId = value
        return this
    }

    override fun value4(value: String?): TwitterScheduleTaskRecord {
        this.channelId = value
        return this
    }

    override fun value5(value: String?): TwitterScheduleTaskRecord {
        this.uploaderId = value
        return this
    }

    override fun value6(value: String?): TwitterScheduleTaskRecord {
        this.urlName = value
        return this
    }

    override fun value7(value: Long?): TwitterScheduleTaskRecord {
        this.updateInterval = value
        return this
    }

    override fun value8(value: Boolean?): TwitterScheduleTaskRecord {
        this.mediaOnly = value
        return this
    }

    override fun value9(value: String?): TwitterScheduleTaskRecord {
        this.description = value
        return this
    }

    override fun values(value1: Long?, value2: OffsetDateTime?, value3: String?, value4: String?, value5: String?, value6: String?, value7: Long?, value8: Boolean?, value9: String?): TwitterScheduleTaskRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        this.value5(value5)
        this.value6(value6)
        this.value7(value7)
        this.value8(value8)
        this.value9(value9)
        return this
    }

    /**
     * Create a detached, initialised TwitterScheduleTaskRecord
     */
    constructor(id: Long? = null, added: OffsetDateTime? = null, guildId: String? = null, channelId: String? = null, uploaderId: String? = null, urlName: String? = null, updateInterval: Long? = null, mediaOnly: Boolean? = null, description: String? = null): this() {
        this.id = id
        this.added = added
        this.guildId = guildId
        this.channelId = channelId
        this.uploaderId = uploaderId
        this.urlName = urlName
        this.updateInterval = updateInterval
        this.mediaOnly = mediaOnly
        this.description = description
    }
}
