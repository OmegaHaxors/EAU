package org.ja13.eau.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.channel.ChannelHandler.Sharable;
import org.ja13.eau.EAU;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import org.ja13.eau.EAU;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@Sharable
public class ClientPacketHandler {

    public ClientPacketHandler() {
        //FMLCommonHandler.instance().bus().register(this);
        EAU.eventChannel.register(this);
    }

    @SubscribeEvent
    public void onClientPacket(ClientCustomPacketEvent event) {
        //Utils.println("onClientPacket");
        FMLProxyPacket packet = event.packet;
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.payload().array()));
        NetworkManager manager = event.manager;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer; // EntityClientPlayerMP

        EAU.packetHandler.packetRx(stream, manager, player);
    }
}
