package ru.astemir.skillsbuster.common.item;

import net.minecraft.network.chat.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsbuster.client.misc.EasingType;
import ru.astemir.skillsbuster.manager.camera.motion.CameraFrame;
import ru.astemir.skillsbuster.client.misc.InterpolationType;
import ru.astemir.skillsbuster.common.io.json.SBJson;

public class ItemVideoCam extends Item {

    public ItemVideoCam() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1).rarity(Rarity.EPIC));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!player.getCooldowns().isOnCooldown(this)) {
            if (!level.isClientSide) {
                CameraFrame frame = new CameraFrame(Vector3.from(player.getEyePosition()), new Vector3(player.getXRot(), player.getYHeadRot(), 0),null,-1, InterpolationType.LINEAR, EasingType.NONE, 1, 0);
                String text = SBJson.GSON.toJson(frame);
                player.sendSystemMessage(Component.literal(text).withStyle(Style.EMPTY.
                        withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(text))).
                        withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text))
                ));
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return super.use(level,player,hand);
    }

}
