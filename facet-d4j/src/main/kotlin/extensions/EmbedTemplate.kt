package io.facet.discord.extensions

import discord4j.core.spec.*
import discord4j.discordjson.json.*
import io.facet.discord.dsl.*

/*
 * facet - Created on 6/13/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/13/2021
 */

/**
 * Creates an [EmbedData] object from this template. Used with MultipartRequests
 */
fun EmbedTemplate.asRequest(): EmbedData = EmbedCreateSpec().also { spec -> accept(spec) }.asRequest()
