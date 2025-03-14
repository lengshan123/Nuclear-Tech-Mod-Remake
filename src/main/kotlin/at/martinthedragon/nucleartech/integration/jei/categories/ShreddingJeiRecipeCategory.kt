package at.martinthedragon.nucleartech.integration.jei.categories

import at.martinthedragon.nucleartech.ModBlockItems
import at.martinthedragon.nucleartech.NuclearTags
import at.martinthedragon.nucleartech.NuclearTech
import at.martinthedragon.nucleartech.blocks.entities.ShredderBlockEntity
import at.martinthedragon.nucleartech.ntm
import at.martinthedragon.nucleartech.recipes.ShreddingRecipe
import com.mojang.blaze3d.vertex.PoseStack
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class ShreddingJeiRecipeCategory(guiHelper: IGuiHelper) : IRecipeCategory<ShreddingRecipe> {
    private val background = guiHelper.createDrawable(GUI_RESOURCE, 0, 0, 111, 54)
    private val icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, ItemStack(ModBlockItems.shredder.get()))
    private val arrow = guiHelper.drawableBuilder(GUI_RESOURCE, 16, 90, 22, 16).buildAnimated(ShredderBlockEntity.SHREDDING_TIME, IDrawableAnimated.StartDirection.LEFT, false)
    private val energyBar = guiHelper.drawableBuilder(GUI_RESOURCE, 0, 54, 16, 52).buildAnimated(ShredderBlockEntity.MAX_ENERGY / ShredderBlockEntity.ENERGY_CONSUMPTION_RATE / 10, IDrawableAnimated.StartDirection.TOP, true)

    override fun getUid() = UID

    override fun getRecipeClass() = ShreddingRecipe::class.java

    override fun getTitle() = TranslatableComponent("jei.${NuclearTech.MODID}.category.shredding")

    override fun getBackground(): IDrawable = background

    override fun getIcon(): IDrawable = icon

    override fun setIngredients(recipe: ShreddingRecipe, ingredients: IIngredients) {
        val shredderBlades = Ingredient.of(NuclearTags.Items.SHREDDER_BLADES)
        ingredients.setInputIngredients(listOf(recipe.ingredient, shredderBlades, shredderBlades))
        ingredients.setOutput(VanillaTypes.ITEM, recipe.result)
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipe: ShreddingRecipe, ingredients: IIngredients) {
        val guiItemStacks = recipeLayout.itemStacks
        guiItemStacks.init(0, true, 23, 18)
        guiItemStacks.init(10, true, 48, 0)
        guiItemStacks.init(11, true, 48, 36)
        guiItemStacks.init(12, false, 73, 19)
        guiItemStacks.set(ingredients)
    }

    override fun draw(recipe: ShreddingRecipe, matrixStack: PoseStack, mouseX: Double, mouseY: Double) {
        arrow.draw(matrixStack, 46, 20)
        energyBar.draw(matrixStack, 1, 1)
        drawExperience(recipe, matrixStack)
    }

    private fun drawExperience(recipe: ShreddingRecipe, matrixStack: PoseStack) {
        val experience = recipe.experience
        if (experience > 0) {
            val experienceString = TranslatableComponent("gui.jei.category.smelting.experience", experience)
            val fontRenderer = Minecraft.getInstance().font
            val stringWidth = fontRenderer.width(experienceString)
            fontRenderer.draw(matrixStack, experienceString, (background.width - stringWidth).toFloat(), 0F, -0x7F7F80)
        }
    }

    companion object {
        val GUI_RESOURCE = ntm("textures/gui/jei_shredder.png")
        val UID = ntm("shredding")
    }
}
