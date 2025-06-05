package dev.zornov.repomine.ext

import kotlin.math.cos
import kotlin.math.sin


fun Triple<Double, Double, Double>.toQuaternion(): FloatArray {
    val (pitch, yaw, roll) = listOf(first, second, third)
        .map { Math.toRadians(it / 2.0).toFloat() } // Convert angles to radians and halve them

    val (sinPitch, cosPitch) = sin(pitch) to cos(pitch) // Sine and cosine of pitch
    val (sinYaw, cosYaw) = sin(yaw) to cos(yaw)       // Sine and cosine of yaw
    val (sinRoll, cosRoll) = sin(roll) to cos(roll)   // Sine and cosine of roll

    return floatArrayOf(
        sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw, // x component
        cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw, // y component
        cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw, // z component
        cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw  // w component
    )
}