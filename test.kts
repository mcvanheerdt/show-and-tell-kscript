#!/usr/bin/env kscript

import java.io.File

val currentFolder = File("./")
currentFolder.listFiles()
        ?.sortedBy { it.name }
        ?.forEach { println(it.name) }
