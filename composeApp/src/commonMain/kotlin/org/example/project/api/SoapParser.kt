package org.example.project.api

object SoapParser {

    fun extractTagValue(xml: String, tagName: String): String? {
        val pattern = "<(?:\\w+:)?$tagName.*?>(.*?)</(?:\\w+:)?$tagName>"
            .toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        return pattern.find(xml)?.groupValues?.get(1)
    }

    fun unescapeXml(content: String): String {
        return content.replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
    }
}