package at.martinthedragon.nucleartech.world.gen.features

import at.martinthedragon.nucleartech.ModBlocks
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration

class OilBubbleFeature(codec: Codec<NoneFeatureConfiguration>) : Feature<NoneFeatureConfiguration>(codec) {
    override fun place(context: FeaturePlaceContext<NoneFeatureConfiguration>): Boolean {
        val radius = 7 + context.random().nextInt(9)
        val radiusSquared = radius * radius
        val radiusSquaredHalved = radiusSquared / 2

        for (xIteration in -radius until radius) {
            val x = xIteration + context.origin().x
            val xBorder = xIteration * xIteration
            for (yIteration in -radius until radius) {
                val y = yIteration + context.origin().y
                val yBorder = xBorder + yIteration * yIteration
                for (zIteration in -radius until radius) {
                    val z = zIteration + context.origin().z
                    val zBorder = yBorder + zIteration * zIteration
                    if (zBorder < radiusSquaredHalved) {
                        val oilOrePos = BlockPos(x, y, z)
                        val blockState = context.level().getBlockState(oilOrePos)
                        if (blockState.`is`(BlockTags.STONE_ORE_REPLACEABLES)) {
                            setBlock(context.level(), oilOrePos, ModBlocks.oilDeposit.get().defaultBlockState())
                        } else if (blockState.`is`(BlockTags.DEEPSLATE_ORE_REPLACEABLES)) {
                            setBlock(context.level(), oilOrePos, ModBlocks.deepslateOilDeposit.get().defaultBlockState())
                        }
                    }
                }
            }
        }

        return true
    }
}
