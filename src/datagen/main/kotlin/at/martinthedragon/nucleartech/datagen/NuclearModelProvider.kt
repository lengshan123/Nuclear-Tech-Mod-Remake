package at.martinthedragon.nucleartech.datagen

import at.martinthedragon.nucleartech.NuclearTech
import net.minecraft.data.DataGenerator
import net.minecraftforge.client.model.generators.BlockModelBuilder
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder
import net.minecraftforge.common.data.ExistingFileHelper

class NuclearModelProvider(
    dataGenerator: DataGenerator,
    existingFileHelper: ExistingFileHelper,
) : ModelProvider<BlockModelBuilder>(
    dataGenerator,
    NuclearTech.MODID,
    OTHER_FOLDER,
    ::BlockModelBuilder,
    existingFileHelper
) {
    override fun getName(): String = "Nuclear Tech Mod Generic Models"

    override fun registerModels() {
        getBuilder("mushroom_cloud")
            .customLoader { modelLoader: BlockModelBuilder, existingFileHelper -> OBJLoaderBuilder.begin(modelLoader, existingFileHelper) }
            .modelLocation(modLoc("models/other/mushroom_cloud/mush.obj"))
            .flipV(true).detectCullableFaces(false).end()
            .texture("fireball_texture", modLoc("entity/mushroom_cloud/fireball"))
    }

    companion object {
        const val OTHER_FOLDER = "other"
    }
}
