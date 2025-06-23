package dev.zornov.repomine.ext

import net.kyori.adventure.key.Key

fun String.toKey(): Key {
    return Key.key(this)
}