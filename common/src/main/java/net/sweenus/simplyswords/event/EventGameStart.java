package net.sweenus.simplyswords.event;

import dev.architectury.event.events.client.ClientPlayerEvent;
import net.minecraft.client.network.ClientPlayerEntity;

public class EventGameStart implements ClientPlayerEvent.ClientPlayerJoin {
    @Override
    public void join(ClientPlayerEntity player) {

        //Uncomment the following when a release requires a config regeneration
        /*
        if (SimplySwords.isConfigOutdated && SimplySwords.isConfigOutdated) {
            player.sendMessage(Text.literal("SimplySwords: Your config is outdated. (this message can be disabled in the config)").formatted(Formatting.GOLD), false);
        }
        */
    }
}
