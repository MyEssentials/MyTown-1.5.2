package ee.lutsu.alpha.mc.mytown.event.prot;

import com.sperion.forgeperms.Log;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.TownSettingCollection.Permissions;
import ee.lutsu.alpha.mc.mytown.event.ProtBase;

public class TinkersConstruct extends ProtBase {
    public static TinkersConstruct instance = new TinkersConstruct();
    
    private Class<?> clHammer, clExcavator;
    
    @Override
    public void load() throws Exception{
        clHammer = Class.forName("mods.tinker.tconstruct.items.tools.Hammer");
        clExcavator = Class.forName("mods.tinker.tconstruct.items.tools.Excavator");
    }

    /**
     * Check if a tool was used inside a town and sees if the user of the tool
     * is allowed to use it
     */
    @Override
    public String update(Resident res, Item tool, ItemStack item) throws Exception {
        if (clHammer.isInstance(tool) || clExcavator.isInstance(tool)){
            Vec3 pos = res.onlinePlayer.getLookVec();
            Vec3 pos2 = pos.addVector(res.onlinePlayer.posX, res.onlinePlayer.posY, res.onlinePlayer.posZ);
            
            for (int z=-1; z<=1; z++){
                for (int x=-1; x<=1; x++){
                    Log.info("Checking (%s, %s, %s)", pos2.xCoord+x, pos2.yCoord, pos2.zCoord+z);
                    if (!res.canInteract((int)pos2.xCoord+x, (int)pos2.yCoord, (int)pos2.zCoord+z, Permissions.Build)){
                        Log.info("Hit something!");
                        return "Cannot attack here";
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean isEntityInstance(Item e) {
        return clHammer.isInstance(e) || clExcavator.isInstance(e);
    }
    
    @Override
    public boolean loaded() {
        return clHammer != null;
    }
    
    @Override
    public String getMod() {
        return "Tinkers Construct";
    }

    @Override
    public String getComment() {
        return "Blocks Tinkers Construct tools";
    }

}
