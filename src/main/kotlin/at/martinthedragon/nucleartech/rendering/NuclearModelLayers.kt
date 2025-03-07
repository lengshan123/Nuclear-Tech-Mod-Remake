package at.martinthedragon.nucleartech.rendering

import at.martinthedragon.nucleartech.ntm
import net.minecraft.client.model.geom.ModelLayerLocation

object NuclearModelLayers {
    val STEAM_PRESS = createLocation("steam_press")

    private fun createLocation(name: String, sub: String = "main") = ModelLayerLocation(ntm(name), sub)
}
