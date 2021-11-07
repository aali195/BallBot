/*
 * This file is generated by jOOQ.
 */
package com.github.ball.ballbot.domain.generated.tables.records


import com.github.ball.ballbot.domain.generated.tables.Picture

import java.time.OffsetDateTime

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record7
import org.jooq.Row7
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class PictureRecord() : UpdatableRecordImpl<PictureRecord>(Picture.PICTURE), Record7<Long?, OffsetDateTime?, String?, String?, String?, String?, Array<String?>?> {

    var id: Long?
        set(value): Unit = set(0, value)
        get(): Long? = get(0) as Long?

    var created: OffsetDateTime?
        set(value): Unit = set(1, value)
        get(): OffsetDateTime? = get(1) as OffsetDateTime?

    var guildId: String?
        set(value): Unit = set(2, value)
        get(): String? = get(2) as String?

    var uploaderId: String?
        set(value): Unit = set(3, value)
        get(): String? = get(3) as String?

    var name: String?
        set(value): Unit = set(4, value)
        get(): String? = get(4) as String?

    var url: String?
        set(value): Unit = set(5, value)
        get(): String? = get(5) as String?

    var tags: Array<String?>?
        set(value): Unit = set(6, value)
        get(): Array<String?>? = get(6) as Array<String?>?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Long?> = super.key() as Record1<Long?>

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row7<Long?, OffsetDateTime?, String?, String?, String?, String?, Array<String?>?> = super.fieldsRow() as Row7<Long?, OffsetDateTime?, String?, String?, String?, String?, Array<String?>?>
    override fun valuesRow(): Row7<Long?, OffsetDateTime?, String?, String?, String?, String?, Array<String?>?> = super.valuesRow() as Row7<Long?, OffsetDateTime?, String?, String?, String?, String?, Array<String?>?>
    override fun field1(): Field<Long?> = Picture.PICTURE.ID
    override fun field2(): Field<OffsetDateTime?> = Picture.PICTURE.CREATED
    override fun field3(): Field<String?> = Picture.PICTURE.GUILD_ID
    override fun field4(): Field<String?> = Picture.PICTURE.UPLOADER_ID
    override fun field5(): Field<String?> = Picture.PICTURE.NAME
    override fun field6(): Field<String?> = Picture.PICTURE.URL
    override fun field7(): Field<Array<String?>?> = Picture.PICTURE.TAGS
    override fun component1(): Long? = id
    override fun component2(): OffsetDateTime? = created
    override fun component3(): String? = guildId
    override fun component4(): String? = uploaderId
    override fun component5(): String? = name
    override fun component6(): String? = url
    override fun component7(): Array<String?>? = tags
    override fun value1(): Long? = id
    override fun value2(): OffsetDateTime? = created
    override fun value3(): String? = guildId
    override fun value4(): String? = uploaderId
    override fun value5(): String? = name
    override fun value6(): String? = url
    override fun value7(): Array<String?>? = tags

    override fun value1(value: Long?): PictureRecord {
        this.id = value
        return this
    }

    override fun value2(value: OffsetDateTime?): PictureRecord {
        this.created = value
        return this
    }

    override fun value3(value: String?): PictureRecord {
        this.guildId = value
        return this
    }

    override fun value4(value: String?): PictureRecord {
        this.uploaderId = value
        return this
    }

    override fun value5(value: String?): PictureRecord {
        this.name = value
        return this
    }

    override fun value6(value: String?): PictureRecord {
        this.url = value
        return this
    }

    override fun value7(value: Array<String?>?): PictureRecord {
        this.tags = value
        return this
    }

    override fun values(value1: Long?, value2: OffsetDateTime?, value3: String?, value4: String?, value5: String?, value6: String?, value7: Array<String?>?): PictureRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        this.value5(value5)
        this.value6(value6)
        this.value7(value7)
        return this
    }

    /**
     * Create a detached, initialised PictureRecord
     */
    constructor(id: Long? = null, created: OffsetDateTime? = null, guildId: String? = null, uploaderId: String? = null, name: String? = null, url: String? = null, tags: Array<String?>? = null): this() {
        this.id = id
        this.created = created
        this.guildId = guildId
        this.uploaderId = uploaderId
        this.name = name
        this.url = url
        this.tags = tags
    }
}
