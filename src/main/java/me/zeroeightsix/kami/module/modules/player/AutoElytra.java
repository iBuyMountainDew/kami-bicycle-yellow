package me.zeroeightsix.kami.module.modules.player;

import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;

/**
 * @author S-B99
 * @see AutoArmour
 */
@Module.Info(name = "AutoElytra", description = "Automatically equips elytras when necessary", category = Module.Category.PLAYER)
public class AutoElytra extends Module {
    private Setting<Integer> timeoutTime = register(Settings.integerBuilder().withName("Timeout 10%s").withMinimum(0).withMaximum(10).withValue(1).build());
    private int currentDamage = -1;
    private int newDamage = -1;
    private int newSlot = -1;

    @Override
    public void onUpdate() {
        if (mc.player.ticksExisted % 2 == 0) return;
        // check screen
        if (mc.currentScreen instanceof GuiContainer
                && !(mc.currentScreen instanceof InventoryEffectRenderer))
            return;

        // initialize with currently equipped elytra
        ItemStack oldArmor = mc.player.inventory.armorItemInSlot(2); // armorItemInSlot counts from 1 to 4
        if (oldArmor.getItem() instanceof ItemElytra)
            currentDamage = oldArmor.getItemDamage();


        // search inventory for better armor
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (stack.getCount() > 1)
                continue;

            if (!(stack.getItem() instanceof ItemElytra))
                continue;

            if (stack.getItemDamage() > currentDamage) {
                newDamage = stack.getItemDamage();
                newSlot = slot;
            }
        }

        // have a delay for the onground
        if (!timeout()) return;
        Command.sendChatMessage("working");

        if (newSlot != -1 && !mc.player.onGround) { // 6 is 8 minus armour slot
            if (newSlot < 9) newSlot += 36;
            mc.playerController.windowClick(1, newSlot, 0, ClickType.PICKUP, mc.player); // pick up new elytra
            mc.playerController.windowClick(1, 7, 0, ClickType.SWAP, mc.player); // switch old elytra with new one
            mc.playerController.windowClick(1, newSlot, 0, ClickType.PICKUP, mc.player); // move old elytra to new's spot
        }
    }

    private static long startTime = 0;
    private boolean timeout() {
        if (!mc.player.onGround) return true;
        if (startTime == 0) startTime = System.currentTimeMillis();
        if (startTime + (timeoutTime.getValue() * 100) <= System.currentTimeMillis()) { // 1 timeout = 0.1 seconds
            startTime = System.currentTimeMillis();
            return !mc.player.onGround;
        }
        return false;
    }
}
