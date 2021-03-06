package ee.lutsu.alpha.mc.mytown.event.prot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import ee.lutsu.alpha.mc.mytown.MyTownDatasource;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.TownSettingCollection.Permissions;
import ee.lutsu.alpha.mc.mytown.event.ProtBase;
import ee.lutsu.alpha.mc.mytown.event.ProtectionEvents;

public class MFR extends ProtBase {
    public static MFR instance = new MFR();

    private Class<?> clEntitySafariNet;

    @Override
    public void load() throws Exception {
        clEntitySafariNet = Class.forName("powercrystals.minefactoryreloaded.entity.EntitySafariNet");
    }

    @Override
    public String update(Entity e) throws Exception {
        if (clEntitySafariNet.isInstance(e)) {
            EntityThrowable t = (EntityThrowable) e;
            EntityLiving owner = t.getThrower();

            if (owner == null || !(owner instanceof EntityPlayer)) {
                return "No owner or is not a player";
            }

            Resident thrower = ProtectionEvents.instance.lastOwner = MyTownDatasource.instance.getResident((EntityPlayer) owner);

            int x = (int) (t.posX + t.motionX);
            int y = (int) (t.posY + t.motionY);
            int z = (int) (t.posZ + t.motionZ);
            int dim = thrower.onlinePlayer.dimension;

            if (!thrower.canInteract(dim, x, y, z, Permissions.Loot)) {
                return "SafariNet would land in a town";
            }
        }

        return null;
    }

    @Override
    public boolean isEntityInstance(Entity e) {
        return clEntitySafariNet.isInstance(e);
    }

    @Override
    public boolean loaded() {
        return clEntitySafariNet != null;
    }

    @Override
    public String getMod() {
        return "MineFactory Reloaded";
    }

    @Override
    public String getComment() {
        return "Stops MineFactory Reloaded items";
    }
}
