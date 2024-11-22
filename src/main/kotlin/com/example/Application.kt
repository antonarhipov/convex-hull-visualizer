package com.example

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        staticResources("/", "static") {
            default("index.html")
        }
        post("/points") {
            val points = call.receive<List<Point>>()
            val result = grahamScan(points).also { println("Algorithm: Graham Scan") }
//            val result = jarvisMarch(points)        .also { println("Algorithm: Jarvis March") }
//            val result = quickHull(points)          .also { println("Algorithm: Quickhull") }
//            val result = monotoneChain(points)      .also { println("Algorithm: Monotone Chain") }
            call.respond(result)
        }
    }
}


@Serializable
data class Point(val x: Int, val y: Int)