package wecui.event.listeners;

import wecui.fevents.Listener;
import wecui.WorldEditCUI;
import wecui.event.ChatCommandEvent;
import wecui.event.command.CommandEventBase;
import wecui.event.command.CommandEventType;
import wecui.vendor.org.joor.Reflect;
import wecui.exception.ReflectException;

/**
 * Parses outgoing commands, and checks if they match an existing command.
 * 
 * @author lahwran
 * @author yetanotherx
 * 
 */
public class WorldEditCommandListener implements Listener<ChatCommandEvent> {

    protected WorldEditCUI controller;

    public WorldEditCommandListener(WorldEditCUI controller) {
        this.controller = controller;
    }

    public void onEvent(ChatCommandEvent event) {
        if (event.getArgs().length == 0) {
            return;
        }

        CommandEventType commEventType = CommandEventType.getTypeFromCommand(event.getArgs()[0]);

        if (commEventType != null) {
            try {
                CommandEventBase newEvent = Reflect.on(commEventType.getEventClass()).create(this.controller, event.getArgs()).get();
                newEvent.run();

                if (newEvent.isCancelled()) {
                    event.setCancelled(true);
                }

            } catch (ReflectException ex) {
            }

        }
    }
}
