/*
 * This file is generated by jOOQ.
 */
package com.github.ball.ballbot.domain.generated.indexes


import com.github.ball.ballbot.domain.generated.tables.Guild
import com.github.ball.ballbot.domain.generated.tables.Picture
import com.github.ball.ballbot.domain.generated.tables.TwitterScheduleTask

import org.jooq.Index
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// INDEX definitions
// -------------------------------------------------------------------------

val CHANNEL_ID_URL_NAME_IDX: Index = Internal.createIndex(DSL.name("channel_id_url_name_idx"), TwitterScheduleTask.TWITTER_SCHEDULE_TASK, arrayOf(TwitterScheduleTask.TWITTER_SCHEDULE_TASK.CHANNEL_ID, TwitterScheduleTask.TWITTER_SCHEDULE_TASK.URL_NAME), true)
val GUILD_ID_IDX: Index = Internal.createIndex(DSL.name("guild_id_idx"), Guild.GUILD, arrayOf(Guild.GUILD.ID), true)
val PICTURE_NAME_IDX: Index = Internal.createIndex(DSL.name("picture_name_idx"), Picture.PICTURE, arrayOf(Picture.PICTURE.GUILD_ID, Picture.PICTURE.NAME), true)
val PICTURE_TAGS_IDX: Index = Internal.createIndex(DSL.name("picture_tags_idx"), Picture.PICTURE, arrayOf(Picture.PICTURE.TAGS), false)
