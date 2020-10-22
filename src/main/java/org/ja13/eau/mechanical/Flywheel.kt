package org.ja13.eau.mechanical

import org.ja13.eau.misc.Direction
import org.ja13.eau.misc.LinearFunction
import org.ja13.eau.misc.Obj3D
import org.ja13.eau.misc.Utils
import org.ja13.eau.node.transparent.EntityMetaTag
import org.ja13.eau.node.transparent.TransparentNode
import org.ja13.eau.node.transparent.TransparentNodeDescriptor
import org.ja13.eau.sim.IProcess
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.DamageSource

class FlywheelDescriptor(baseName: String, obj: org.ja13.eau.misc.Obj3D) : SimpleShaftDescriptor(baseName,
    FlyWheelElement::class, ShaftRender::class, org.ja13.eau.node.transparent.EntityMetaTag.Basic) {
    override val obj = obj
    override val static = arrayOf(obj.getPart("Stand"), obj.getPart("Cowl"))
    override val rotating = arrayOf(obj.getPart("Flywheel"), obj.getPart("Shaft"))
}

class FlyWheelElement(node: org.ja13.eau.node.transparent.TransparentNode, desc_: org.ja13.eau.node.transparent.TransparentNodeDescriptor) : StraightJointElement(node, desc_) {
    override val shaftMass = 10.0

    inner class FlyWheelFlingProcess : org.ja13.eau.sim.IProcess {
        val interval = 0.05
        val yTolerance = 1.0
        val xzTolerance = 0.5
        val minRads = 5.0
        val velocityF = LinearFunction(0f, 0f, 1000f, 10f)
        val damageF = LinearFunction(5f, 1f, 1000f, 10f)

        var timer = 0.0

        override fun process(time: Double) {
            timer += time
            if(timer >= interval) {
                timer = 0.0
                slowProcess()
            }
        }

        fun slowProcess() {
            // Utils.println("FFP.sP: tick")
            val rads = shaft.rads
            if(rads < minRads) return
            val coord = coordonate()
            val objects = coord.world().getEntitiesWithinAABB(Entity::class.java, coord.getAxisAlignedBB(1))
            //if(objects.size > 0) Utils.println("FFP.sP: within range: " + objects.size)
            for(obj in objects) {
                val ent = obj as Entity
                Utils.println(String.format("FPP.sP: considering %s", ent))
                val dx = Math.abs(ent.posX - coord.x - 0.5)
                val dy = Math.abs(ent.posY - coord.y - 1)
                val dz = Math.abs(ent.posZ - coord.z - 0.5)
                if(dy > yTolerance) {
                    Utils.println("FPP.sP: dy out of range (" + dy + "; c.y " + coord.y + " e.y" + ent.posY + "): " + ent)
                    continue
                }
                if(dx > xzTolerance) {
                    Utils.println("FPP.sP: dx out of range (" + dx + "; c.x " + coord.x + " e.x" + ent.posX + "): " + ent)
                    continue
                }
                if(dz > xzTolerance) {
                    Utils.println("FPP.sP: dz out of range (" + dz + "; c.z " + coord.z + " e.z" + ent.posZ + "): " + ent)
                    continue
                }
                val mag = velocityF.getValue(rads)
                val vel = when(front) {
                    Direction.ZN, Direction.ZP -> arrayOf(0.0, mag * 0.1, mag)
                    Direction.XN, Direction.XP -> arrayOf(mag, mag * 0.1, 0.0)
                    else -> arrayOf(0.0, mag, 0.0) // XXX
                }
                ent.addVelocity(vel[0], vel[1], vel[2])
                var dmg = damageF.getValue(rads).toInt()
                if (ent is EntityPlayer) {
                    val ply = ent
                    // creative mode players can't have their position set, apparently.
                    if (!ply.capabilities.isCreativeMode) {
                        ent.addVelocity(vel[0], vel[1], vel[2])
                    }
                } else {
                    // not a player, we do what we want
                    ent.addVelocity(vel[0], vel[1], vel[2])
                }
                Utils.println("FFP.sP: ent " + ent + " flung " + vel.joinToString(",") + " for damage " + dmg)
                if(dmg <= 0) continue
                ent.attackEntityFrom(DamageSource("Flywheel"), dmg.toFloat())
            }
        }
    }
    var flingProcess = FlyWheelFlingProcess()

    init {
        slowProcessList.add(flingProcess)
    }

    override fun getWaila(): Map<String, String> {
        var info = mutableMapOf<String, String>()
        info.put("Speed", Utils.plotRads(shaft.rads))
        info.put("Energy", Utils.plotEnergy(shaft.energy))
        return info
    }
}