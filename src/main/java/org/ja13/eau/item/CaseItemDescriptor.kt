package org.ja13.eau.item

import org.ja13.eau.i18n.I18N
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

class CaseItemDescriptor(name: String) : GenericItemUsingDamageDescriptorUpgrade(name) {
    override fun addInformation(itemStack: ItemStack?, entityPlayer: EntityPlayer?, list: MutableList<String>, par4: Boolean) {
        super.addInformation(itemStack, entityPlayer, list, par4)
        list.add(org.ja13.eau.i18n.I18N.tr("Can be used to encase EA items that support it"))
    }
}
