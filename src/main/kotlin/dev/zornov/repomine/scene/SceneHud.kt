package dev.zornov.repomine.scene

import dev.zornov.repomine.resourcepack.hud.builder.DisplayType
import dev.zornov.repomine.resourcepack.hud.builder.HudScreen
import dev.zornov.repomine.resourcepack.hud.builder.annotations.Widget
import dev.zornov.repomine.resourcepack.hud.widget.TextWidget
import net.kyori.adventure.text.Component

class SceneHud : HudScreen(
    DisplayType.ACTION_BAR
) {
    @Widget
    val helloWorldText = TextWidget(
        Component.text("Hello World!"),
        -120, 10
    )
}