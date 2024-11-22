package com.example

import kotlin.collections.filter
import kotlin.collections.maxByOrNull
import kotlin.collections.minByOrNull
import kotlin.math.sqrt
import kotlin.math.abs
import kotlin.math.atan2


/**
 * Applies the Graham Scan algorithm to find the convex hull of a set of 2D points.
 *
 * @param points A list of points represented by instances of the `Point` data class.
 * @return A list of points forming the convex hull in counter-clockwise order.
 */
internal fun grahamScan(points: List<Point>): List<Point> {
    if (points.size < 3) return points

    // Find the point with the lowest y-coordinate (and leftmost if tie)
    val pivot = points.minWith(compareBy<Point> { it.y }.thenBy { it.x })

    // Sort points based on polar angle with respect to pivot
    val sortedPoints = points.sortedWith(compareBy<Point> {
        if (it == pivot) return@compareBy Double.NEGATIVE_INFINITY
        atan2((it.y - pivot.y).toDouble(), (it.x - pivot.x).toDouble())
    }.thenBy {
        (it.x - pivot.x) * (it.x - pivot.x) + (it.y - pivot.y) * (it.y - pivot.y)
    })

    val stack = mutableListOf<Point>()

    for (point in sortedPoints) {
        while (stack.size >= 2 && !isCounterClockwise(stack[stack.size - 2], stack.last(), point)) {
            stack.removeAt(stack.lastIndex)
        }
        stack.add(point)
    }

    return stack
}

private fun isCounterClockwise(a: Point, b: Point, c: Point): Boolean {
    return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x) > 0
}

/**
 * Computes the convex hull of a set of 2D points using the Jarvis March algorithm, also known as the Gift Wrapping algorithm.
 *
 * @param points The list of points for which to calculate the convex hull. Must contain at least three points.
 * @return A list of points representing the vertices of the convex hull in counter-clockwise order.
 */
internal fun jarvisMarch(points: List<Point>): List<Point> {
    if (points.size < 3) return points

    val hull = mutableListOf<Point>()
    var leftmostPoint = points.minByOrNull { it.x }!!

    var currentPoint = leftmostPoint
    do {
        hull.add(currentPoint)
        var nextPoint = points[0]
        for (point in points) {
            if (point == currentPoint) continue
            val turn = cross(currentPoint, nextPoint, point)
            if (nextPoint == currentPoint || turn > 0 || (turn == 0 && distance(currentPoint, point) > distance(currentPoint, nextPoint))) {
                nextPoint = point
            }
        }
        currentPoint = nextPoint
    } while (currentPoint != leftmostPoint)

    return hull
}

/**
 * Computes the convex hull of a set of 2D points using the QuickHull algorithm.
 *
 * @param points The list of points for which to calculate the convex hull. Must contain at least three points.
 * @return A list of points representing the vertices of the convex hull in counter-clockwise order.
 */
internal fun quickHull(points: List<Point>): List<Point> {
    if (points.size < 3) return points

    val leftmost = points.minByOrNull { it.x }!!
    val rightmost = points.maxByOrNull { it.x }!!

    val hull = mutableListOf(leftmost, rightmost)
    val upperPoints = points.filter { it !in hull && cross(leftmost, rightmost, it) > 0 }
    val lowerPoints = points.filter { it !in hull && cross(leftmost, rightmost, it) < 0 }

    findHull(upperPoints, leftmost, rightmost, hull)
    findHull(lowerPoints, rightmost, leftmost, hull)

    return hull
}

private fun findHull(points: List<Point>, p1: Point, p2: Point, hull: MutableList<Point>) {
    if (points.isEmpty()) return

    val farthest = points.maxByOrNull { abs(cross(p1, p2, it)) }!!
    hull.add(hull.indexOf(p2), farthest)

    val leftSet = points.filter { cross(p1, farthest, it) > 0 }
    val rightSet = points.filter { cross(farthest, p2, it) > 0 }

    findHull(leftSet, p1, farthest, hull)
    findHull(rightSet, farthest, p2, hull)
}

/**
 * Computes the convex hull of a set of 2D points using the Monotone Chain algorithm.
 *
 * @param points The list of 2D points for which to calculate the convex hull.
 * @return A list of points representing the vertices of the convex hull in counter-clockwise order.
 */
internal fun monotoneChain(points: List<Point>): List<Point> {
    if (points.size < 3) return points

    val sortedPoints = points.sortedWith(compareBy(Point::x, Point::y))
    val lower = mutableListOf<Point>()
    val upper = mutableListOf<Point>()

    for (point in sortedPoints) {
        while (lower.size >= 2 && cross(lower[lower.size - 2], lower.last(), point) <= 0) {
            lower.removeAt(lower.lastIndex)
        }
        lower.add(point)
    }

    for (point in sortedPoints.asReversed()) {
        while (upper.size >= 2 && cross(upper[upper.size - 2], upper.last(), point) <= 0) {
            upper.removeAt(upper.lastIndex)
        }
        upper.add(point)
    }

    return (lower.dropLast(1) + upper.dropLast(1))
}


private fun distance(a: Point, b: Point): Double {
    val dx = b.x - a.x
    val dy = b.y - a.y
    return sqrt((dx * dx + dy * dy).toDouble())
}

private fun cross(o: Point, a: Point, b: Point): Int {
    return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x)
}