package dev.zornov.repomine.scene

import dev.zornov.repomine.resourcepack.hud.builder.DisplayType
import dev.zornov.repomine.resourcepack.hud.builder.HudScreen
import dev.zornov.repomine.resourcepack.hud.builder.annotations.Position
import dev.zornov.repomine.resourcepack.hud.builder.annotations.RenderType
import dev.zornov.repomine.resourcepack.hud.builder.annotations.Widget
import dev.zornov.repomine.resourcepack.hud.widget.TextWidget
import net.kyori.adventure.text.Component

@RenderType(DisplayType.ACTION_BAR)
class SceneHud : HudScreen() {
    @Widget
    @Position(vertical = -120, horizontal = 10)
    val helloWorldText = TextWidget(
        Component.text("Hello World!")
    )
}