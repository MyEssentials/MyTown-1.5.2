package ee.lutsu.alpha.mc.mytown.event;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import ee.lutsu.alpha.mc.mytown.MyTownDatasource;
import ee.lutsu.alpha.mc.mytown.entities.Resident;

public class NewProtectionEvents {
    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void entityAttacked(LivingAttackEvent event){
        EntityLiving attacked = event.entityLiving;
        DamageSource source = event.source;
        
        if (source.getSourceOfDamage() instanceof EntityPlayer){
            Resident res = MyTownDatasource.instance.getOrMakeResident((EntityPlayer)source.getSourceOfDamage());
            if (!res.canAttack(attacked)){
                event.setCanceled(true);
            }
        }
    }
}