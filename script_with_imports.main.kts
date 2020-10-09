#!/usr/bin/env kotlin

@file:DependsOn("com.google.code.gson:gson:2.8.6")

import com.google.gson.Gson

data class DatasetExample(
        val exampleName: String,
        val data: Float
)

val exampleJson = "{\"exampleName\": \"image.png\", \"data\":0.05}"
val gson = Gson()
val example: DatasetExample = gson.fromJson(exampleJson, DatasetExample::class.java)
println(example.data)
