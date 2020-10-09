#!/usr/bin/env kotlin

@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.30")

@file:Suppress("EXPERIMENTAL_API_USAGE")

import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import kotlin.system.exitProcess

val logger: Logger = LoggerFactory.getLogger("converting-script")

logger.info("Started")

if(args.size != 5) {
    logger.error("""
    Converts images in the specified directory using the provided target resolution and format.
    Usage: convert_images.main.kts [arguments]

    Arguments:
    0|      the directory in which the images are located
    1|      the directory in which the images are located
    2|      the target width in pixels
    3|      the target height in pixels
    4|      the target image format (e.g. png)
    
    Example:
    convert_images.main.kts ./images 600 600 png
    
    """.trimIndent())
    exitProcess(1)
}

val (directoryPath: String, targetPath: String, targetWidth: String, targetHeight: String, targetFormat: String) = args

val imageDirectory = File(directoryPath)
require(imageDirectory.exists() && imageDirectory.isDirectory)

File(targetPath).mkdirs()

val supportedInputExtensions = listOf("jpg", "png", "jpeg", "bmp", "tif")

runBlocking {
    val jobs = imageDirectory.listFiles(FileFilter { it.extension in supportedInputExtensions})
            .sortedBy { it.name }
            .mapIndexed { index, file ->
                val newFileName = "${index.toString().padStart(4, '0')}.$targetFormat"
                async (Dispatchers.Default){
                    logger.info("Converting ${file.name} to $newFileName")
                    shell {
                        "convert ${file.absolutePath} -resize ${targetWidth}x${targetHeight}! $targetPath/$newFileName"()
                    }
                }
            }
    jobs.awaitAll()
}

exitProcess(0)
