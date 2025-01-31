package at.martinthedragon.nucleartech.blocks.entities

import at.martinthedragon.nucleartech.ModItems
import at.martinthedragon.nucleartech.NuclearTech
import at.martinthedragon.nucleartech.blocks.LittleBoy
import at.martinthedragon.nucleartech.config.NuclearConfig
import at.martinthedragon.nucleartech.entities.NukeExplosionEntity
import at.martinthedragon.nucleartech.math.toVec3Middle
import at.martinthedragon.nucleartech.menus.LittleBoyMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler

class LittleBoyBlockEntity(pos: BlockPos, state: BlockState) : BaseContainerBlockEntity(BlockEntityTypes.littleBoyBlockEntityType.get(), pos, state), BombBlockEntity<LittleBoyBlockEntity> {
    // Bits in following order: Is Ready?, Neutron Shielding, Target, Projectile, Propellant, Igniter
    private var bombCompletion = 0b0

    private val dataAccess = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> bombCompletion
            else -> 0
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> bombCompletion = value
            }
        }

        override fun getCount() = 1
    }

    private val items = NonNullList.withSize(5, ItemStack.EMPTY)
    private val inventory = object : ItemStackHandler(items) {
        override fun onContentsChanged(slot: Int) {
            setChanged()
        }

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = when (slot) {
            0 -> stack.item == ModItems.neutronShieldingLittleBoy.get()
            1 -> stack.item == ModItems.subcriticalUraniumTarget.get()
            2 -> stack.item == ModItems.uraniumProjectile.get()
            3 -> stack.item == ModItems.propellantLittleBoy.get()
            4 -> stack.item == ModItems.bombIgniterLittleBoy.get()
            else -> false
        }
    }

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        items.clear()
        ContainerHelper.loadAllItems(nbt, items)
        updateBombCompletion()
    }

    override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        ContainerHelper.saveAllItems(nbt, items)
    }

    override fun clearContent() { items.clear() }
    override fun getContainerSize() = items.size
    override fun isEmpty() = items.all { it.isEmpty }
    override fun getItem(slot: Int) = items[slot]
    override fun removeItem(slot: Int, amount: Int): ItemStack = ContainerHelper.removeItem(items, slot, amount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(items, slot)
    override fun setItem(slot: Int, itemStack: ItemStack) {
        items[slot] = itemStack
        if (itemStack.count > maxStackSize)
            itemStack.count = maxStackSize
    }

    override fun stillValid(player: Player) = if (level!!.getBlockEntity(worldPosition) != this) false
    else player.distanceToSqr(worldPosition.x + .5, worldPosition.y + .5, worldPosition.z + .5) <= 64

    override fun createMenu(windowID: Int, playerInventory: Inventory) =
        LittleBoyMenu(windowID, playerInventory, this, dataAccess)

    override fun getDefaultName() = TranslatableComponent("container.${NuclearTech.MODID}.little_boy")

    override fun setChanged() {
        updateBombCompletion()
        super.setChanged()
    }

    private fun updateBombCompletion() {
        var result = 0
        if (itemInSlotIs(0, ModItems.neutronShieldingLittleBoy.get())) result = result or (1 shl 4)
        if (itemInSlotIs(1, ModItems.subcriticalUraniumTarget.get())) result = result or (1 shl 3)
        if (itemInSlotIs(2, ModItems.uraniumProjectile.get())) result = result or (1 shl 2)
        if (itemInSlotIs(3, ModItems.propellantLittleBoy.get())) result = result or (1 shl 1)
        if (itemInSlotIs(4, ModItems.bombIgniterLittleBoy.get())) result = result or 1
        if (result == 0b11111) result = result or (1 shl 5)
        bombCompletion = result
    }

    private fun itemInSlotIs(slot: Int, item: Item) : Boolean =
        getItem(slot).let { it.item == item && !it.isEmpty }

    override fun getRequiredDetonationComponents() = LittleBoy.requiredComponents

    override fun detonate(): Boolean = if (level != null && !level!!.isClientSide) {
        setRemoved()
        level!!.removeBlock(worldPosition, false)
        level!!.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1F, level!!.random.nextFloat() * .1F + .9F)
        NukeExplosionEntity.create(
            level!!,
            blockPos.toVec3Middle(),
            NuclearConfig.explosions.littleBoyStrength.get()
        )
    } else false

    private val inventoryCapability = LazyOptional.of(this::inventory)

    override fun <T : Any?> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
        if (!remove && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryCapability.cast()
        }
        return super.getCapability(cap, direction)
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        inventoryCapability.invalidate()
    }
}
