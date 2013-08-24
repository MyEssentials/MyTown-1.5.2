package ee.lutsu.alpha.mc.mytown.event.prot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import ee.lutsu.alpha.mc.mytown.Log;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.TownSettingCollection.Permissions;
import ee.lutsu.alpha.mc.mytown.event.ProtBase;

public class TwilightForest extends ProtBase {
    public static TwilightForest instance = new TwilightForest();
    
    Class<?> cTFCrumbleHorn;
    
    @Override
    public void load() throws Exception{
        cTFCrumbleHorn = Class.forName("twilightforest.item.ItemTFCrumbleHorn");
    }

    /**
     * Check if a tool was used inside a town and sees if the user of the tool
     * is allowed to use it
     */
    @Override
    public String update(Resident res, Item tool, ItemStack item) throws Exception {
        if (cTFCrumbleHorn.isInstance(tool)){
            //WIP... not actually used yet
            EntityPlayer player = res.onlinePlayer;
            int direction = MathHelper.floor_double((double)((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
            Vec3 pos = res.onlinePlayer.getLookVec();

            Log.info("------------------------------");
            Log.info("Checking (%s, %s, %s)", pos.xCoord, pos.yCoord, pos.zCoord);
            Log.info("------------------------------");
            
            for (int z=1; z<=5; z++){
                for (int x=-2; x<=2; x++){
                    for(int y=-2; y<=2; y++){
                        Log.info("Checking (%s, %s, %s)", player.posX+x, player.posY+y, player.posZ+z);
                        /*
                        if (!res.canInteract((int)pos2.xCoord+x, (int)pos2.yCoord, (int)pos2.zCoord+z, Permissions.Build)){
                            Log.info("Hit something!");
                            return "Cannot attack here";
                        }
                        */
                    }
                }
            }
            
            Log.info("------------------------------");
        }
        return null;
    }
    
    @Override
    public boolean isEntityInstance(Item e) {
        return cTFCrumbleHorn.isInstance(e);
    }
    
    @Override
    public boolean loaded() {
        return cTFCrumbleHorn != null;
    }

    @Override
    public String getMod() {
        return "TwilightForest";
    }

    @Override
    public String getComment() {
        return "Blocks Twilight Forest items";
    }

}
