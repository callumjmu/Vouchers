package net.aerh.vouchers.listener;

import net.aerh.vouchers.VoucherPlugin;
import net.aerh.vouchers.voucher.UseStatus;
import net.aerh.vouchers.voucher.VoucherManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class VoucherClickListener implements Listener {

    @EventHandler
    public void onVoucherClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemStack = event.getItem();
        if (itemStack == null || itemStack.getItemMeta() == null || action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
        if (!container.has(VoucherPlugin.VOUCHER_ID_KEY, PersistentDataType.STRING)) return;

        String voucherId = container.get(VoucherPlugin.VOUCHER_ID_KEY, PersistentDataType.STRING);
        UseStatus useStatus = VoucherManager.get().useVoucher(player, voucherId);
        if (useStatus == UseStatus.SUCCESS) {
            deleteOneOrMore(itemStack);
        } else {
            player.sendMessage(useStatus.getMessage());
            VoucherPlugin.get().getLogger().warning("An error occurred trying to redeem voucher " + voucherId + " for player " + player.getName() + ":");
            VoucherPlugin.get().getLogger().warning(useStatus.getMessage());
        }
    }

    private void deleteOneOrMore(ItemStack itemStack) {
        if (itemStack.getAmount() > 1) {
            itemStack.setAmount(itemStack.getAmount() - 1);
        } else {
            itemStack.setAmount(0);
        }
    }
}
