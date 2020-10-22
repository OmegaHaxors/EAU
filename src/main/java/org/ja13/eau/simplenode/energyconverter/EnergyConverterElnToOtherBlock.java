package org.ja13.eau.simplenode.energyconverter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.ja13.eau.misc.Direction;
import org.ja13.eau.node.simple.SimpleNode;
import org.ja13.eau.node.simple.SimpleNodeBlock;
import org.ja13.eau.node.simple.SimpleNodeEntity;

public class EnergyConverterElnToOtherBlock extends SimpleNodeBlock {

    private final EnergyConverterElnToOtherDescriptor descriptor;

    private IIcon elnIcon, eln2Icon;
    private IIcon sideIcon;

    public EnergyConverterElnToOtherBlock(EnergyConverterElnToOtherDescriptor descriptor) {
        super(Material.packedIce);
        this.descriptor = descriptor;
        setDescriptor(descriptor);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new EnergyConverterElnToOtherEntity();
    }

    @Override
    protected SimpleNode newNode() {
        return new EnergyConverterElnToOtherNode();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
        SimpleNodeEntity e = (SimpleNodeEntity) w.getTileEntity(x, y, z);
        Direction s = Direction.fromIntMinecraftSide(side);
        if (e == null) return sideIcon;
        if (e.front == null) return sideIcon;
        if (e.front == s) return getElnIcon(side);
        if (e.front.back() == s) return blockIcon;
        return sideIcon;
    }

    public IIcon getIcon(int side, int meta) {
        Direction s = Direction.fromIntMinecraftSide(side);
        if (s == Direction.XP) return getElnIcon(side);
        if (s == Direction.XN) return blockIcon;
        return sideIcon;
    }

    IIcon getElnIcon(int side) {
        if (side == 2 || side == 5) return eln2Icon;
        return elnIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon("eau:elntoic2lvu_ic2");
        this.elnIcon = register.registerIcon("eau:elntoic2lvu_eln");
        this.eln2Icon = register.registerIcon("eau:elntoic2lvu_eln2");
        this.sideIcon = register.registerIcon("eau:elntoic2lvu_side");
    }
}
