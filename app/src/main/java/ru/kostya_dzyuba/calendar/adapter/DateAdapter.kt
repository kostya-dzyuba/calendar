package ru.kostya_dzyuba.calendar.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate

class DateAdapter : TypeAdapter<LocalDate>() {
    override fun read(`in`: JsonReader) = LocalDate.parse(`in`.nextString())

    override fun write(out: JsonWriter, value: LocalDate) {
        out.value(value.toString())
    }
}