package dev.zornov.repomine.resourcepack.hud

import dev.zornov.repomine.resourcepack.hud.widget.HudWidget
import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.network.ConnectionManager
import net.minestom.server.timer.Scheduler
import net.minestom.server.timer.TaskSchedule
import java.util.*

@Singleton
class HudService(
    val scheduler: Scheduler,
    val connectionManager: ConnectionManager
) : ApplicationEventListener<StartupEvent> {

    /**
     * A mutable map that associates players, identified by their unique UUIDs, with their respective lists of
     * HUD components (`HudWidget`). Each player can have a dynamic list of `HudWidget` instances, which represent
     * UI elements rendered on the player's screen, such as action bars.
     *
     * This map is used to manage and update individual player-specific HUD components dynamically.
     *
     * Key: The player's UUID.
     * Value: A linked list of `HudWidget` instances assigned to the player.
     */
    val playerComponents = mutableMapOf<UUID, LinkedList<HudWidget>>()
    val globalComponents = LinkedList<HudWidget>()

    /**
     * A map that associates each player's unique identifier (UUID) to the corresponding
     * action bar text representation, stored as a [Component].
     *
     * This variable is used to manage and dynamically update the action bar text for online players.
     * Each player's action bar text is generated or updated based on global HUD components,
     * player-specific HUD components, or other relevant factors.
     *
     * The map is updated primarily through the `updateActionBar` function, which constructs
     * the text by combining global and player-specific components. It is also queried and utilized
     * in scheduled tasks, such as during the startup event, to send appropriate action bar
     * updates to online players.
     *
     * The key-value structure allows efficient storage and retrieval of action bar text details
     * for multiple players. The key is the player's UUID, and the value is the [Component]
     * representing the current text to be displayed in their action bar.
     */
    private val playerTextMap = mutableMapOf<UUID, Component>()

    /**
     * Handles the `StartupEvent` by scheduling a repeating task that updates player action bars
     * based on their associated text from `playerTextMap`.
     *
     * @param event the startup event that triggers the initialization process
     */
    override fun onApplicationEvent(event: StartupEvent) {
        scheduler.buildTask {
            connectionManager.onlinePlayers.forEach { player ->
                playerTextMap[player.uuid]?.let {
                    player.sendActionBar(it)
                }
            }
        }.repeat(TaskSchedule.tick(20)).schedule()
    }

    /**
     * Updates the action bar for the specified player by aggregating all global and player-specific HUD components.
     * The generated combined text is then mapped to the player's unique identifier.
     *
     * @param player the player whose action bar needs to be updated
     */
    fun updateActionBar(player: Player) {
        val text = Component.text()

        globalComponents.forEach { widget ->
            if (widget.isVisible) text.append(widget.getComponent())
        }

        playerComponents[player.uuid]?.forEach { widget ->
            if (widget.isVisible) text.append(widget.getComponent())
        }

        playerTextMap[player.uuid] = text.build()
    }

    /**
     * Adds a HUD widget to the player's action bar. If a widget with the same ID already exists,
     * it is replaced with the new one. After adding the widget, the player's action bar is updated.
     *
     * @param widget the `HudWidget` instance to be added to the player's HUD components.
     */
    fun Player.addPlayerHudComponent(widget: HudWidget) {
        val list = playerComponents.computeIfAbsent(uuid) { LinkedList() }
        list.removeIf { it.id == widget.id }

        widget.onUpdate = { updateActionBar(this) }

        list.add(widget)
        updateActionBar(this)
    }


    /**
     * Removes a HUD component from the player's action bar using its unique identifier.
     * If the component is successfully removed, the player's action bar is updated
     * to reflect the changes.
     *
     * @param id the unique identifier of the HUD component to be removed
     */
    fun Player.removePlayerHudComponent(id: String) {
        val list = playerComponents[uuid] ?: return
        if (list.removeIf { it.id == id }) {
            updateActionBar(this)
        }
    }

    /**
     * Retrieves a HUD component of the specified type and ID associated with the player.
     * If the component is a `TextWidget`, a proxy object is created to ensure changes to the text
     * update the player's action bar.
     *
     * @param T the type of the HUD component to retrieve, which must extend `HudWidget`
     * @param id the unique identifier of the HUD component to retrieve
     * @return the HUD component of type `T` with the given ID, or `null` if no matching component is found
     */
    inline fun <reified T : HudWidget> Player.getHudComponent(id: String): T? {
        return playerComponents[uuid]?.firstOrNull { it.id == id && it is T } as? T
    }

    /**
     * Adds a HUD widget to the global HUD components. If a widget with the same ID already exists
     * in the global components, it will be replaced with the provided widget. After adding the
     * widget, the action bars for all players are updated to reflect the change.
     *
     * @param widget the `HudWidget` instance to be added to the global HUD components
     */
    fun addGlobalHudComponent(widget: HudWidget) {
        globalComponents.removeIf { it.id == widget.id }
        widget.onUpdate = { updateAllPlayers() }

        globalComponents.add(widget)
        updateAllPlayers()
    }


    /**
     * Removes a global HUD component by its unique identifier. If the specified component is found
     * and removed from the global components, the action bars for all players are updated to
     * reflect the changes in the HUD layout.
     *
     * @param id the unique identifier of the HUD component to be removed
     */
    fun removeGlobalHudComponent(id: String) {
        if (globalComponents.removeIf { it.id == id }) {
            updateAllPlayers()
        }
    }

    /**
     * Retrieves a global HUD component of the specified type and ID from the global HUD components.
     *
     * @param T The type of the HUD component to retrieve, which must extend `HudWidget`.
     * @param id The unique identifier of the HUD component to retrieve.
     * @return The HUD component of type `T` with the given ID, or `null` if no matching component is found.
     */
    inline fun <reified T : HudWidget> getGlobalHudComponent(id: String): T? {
        return globalComponents.firstOrNull { it.id == id && it is T } as? T
    }

    /**
     * Updates the action bars of all currently online players by iterating through the list of
     * connected players and invoking the `updateActionBar` method for each.
     *
     * This method ensures that the displayed HUD components are refreshed for every online player,
     * reflecting any changes to the global or player-specific HUD components.
     */
    fun updateAllPlayers() {
        connectionManager.onlinePlayers.forEach {
            updateActionBar(it)
        }
    }
}
