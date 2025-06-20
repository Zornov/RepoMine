package dev.zornov.repomine.ext

import net.minestom.server.coordinate.Vec
import kotlin.math.cos
import kotlin.math.sin

fun Vec.toQuaternion(): FloatArray {
    val (pitchRad, yawRad, rollRad) = listOf(x, y, z)
        .map { Math.toRadians(it / 2.0).toFloat() }

    val (sinPitch, cosPitch) = sin(pitchRad) to cos(pitchRad)
    val (sinYaw, cosYaw) = sin(yawRad) to cos(yawRad)
    val (sinRoll, cosRoll) = sin(rollRad) to cos(rollRad)

    return floatArrayOf(
        sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw, // x
        cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw, // y
        cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw, // z
        cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw  // w
    )
}