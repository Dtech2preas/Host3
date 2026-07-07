package com.preasx24.dtechtv.network.parser

import com.preasx24.dtechtv.domain.model.Channel
import java.io.InputStream
import java.util.UUID

object M3uParser {
    fun parse(inputStream: InputStream, countryCode: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = inputStream.bufferedReader()

        var currentLine = reader.readLine()
        if (currentLine == null || !currentLine.startsWith("#EXTM3U")) {
            return emptyList() // Not a valid M3U file
        }

        var channelName = ""
        var logo = ""
        var group = ""
        var tvgId = ""

        currentLine = reader.readLine()
        while (currentLine != null) {
            if (currentLine.startsWith("#EXTINF:")) {
                // Parse attributes
                val attributesString = currentLine.substringAfter("#EXTINF:").substringBeforeLast(",")
                channelName = currentLine.substringAfterLast(",").trim()

                logo = extractAttribute(attributesString, "tvg-logo")
                group = extractAttribute(attributesString, "group-title")
                tvgId = extractAttribute(attributesString, "tvg-id")
            } else if (currentLine.isNotBlank() && !currentLine.startsWith("#")) {
                // It's a stream URL
                val streamUrl = currentLine.trim()
                val id = tvgId.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()

                channels.add(
                    Channel(
                        id = id,
                        name = channelName.takeIf { it.isNotBlank() } ?: "Unknown Channel",
                        logo = logo.takeIf { it.isNotBlank() },
                        group = group.takeIf { it.isNotBlank() },
                        streamUrl = streamUrl,
                        country = countryCode,
                        language = null // Complex to extract from basic #EXTINF reliably
                    )
                )

                // Reset for next
                channelName = ""
                logo = ""
                group = ""
                tvgId = ""
            }
            currentLine = reader.readLine()
        }

        return channels
    }

    private fun extractAttribute(line: String, attribute: String): String {
        val regex = Regex("$attribute=\"([^\"]*)\"")
        val matchResult = regex.find(line)
        return matchResult?.groups?.get(1)?.value ?: ""
    }
}
