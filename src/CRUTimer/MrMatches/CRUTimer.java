package CRUTimer.MrMatches;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CRUTimer extends JavaPlugin implements Listener {
    private FileConfiguration config = this.getConfig();

    //Cache all the config to increase performance
    private int allowCommandFrom;
    private int allowCommandTo;
    private List<String> allowCommand;
    private String blockCommandMessage;

    private int allowEndFrom;
    private int allowEndTo;
    private String enterFallBackWorldName;
    private int enterFallBackWorldX;
    private int enterFallBackWorldY;
    private int enterFallBackWorldZ;
    private String enterFallBackWorldMessage;
    private String loginFallBackWorldName;
    private int loginFallBackWorldX;
    private int loginFallBackWorldY;
    private int loginFallBackWorldZ;
    private String loginFallBackWorldMessage;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        allowCommandFrom = config.getInt("AllowCommand.From");
        allowCommandTo = config.getInt("AllowCommand.To");
        allowCommand = config.getStringList("AllowCommand.Commands");
        blockCommandMessage = config.getString("AllowCommand.Message");

        allowEndFrom = config.getInt("AllowEnd.From");
        allowEndTo = config.getInt("AllowEnd.To");
        enterFallBackWorldName = config.getString("AllowEnd.FallBackWorld.Enter.Name");
        enterFallBackWorldX = config.getInt("AllowEnd.FallBackWorld.Enter.X");
        enterFallBackWorldY = config.getInt("AllowEnd.FallBackWorld.Enter.Y");
        enterFallBackWorldZ = config.getInt("AllowEnd.FallBackWorld.Enter.Z");
        enterFallBackWorldMessage = config.getString("AllowEnd.FallBackWorld.Enter.Message");
        loginFallBackWorldName = config.getString("AllowEnd.FallBackWorld.Login.Name");
        loginFallBackWorldX = config.getInt("AllowEnd.FallBackWorld.Login.X");
        loginFallBackWorldY = config.getInt("AllowEnd.FallBackWorld.Login.Y");
        loginFallBackWorldZ = config.getInt("AllowEnd.FallBackWorld.Login.Z");
        loginFallBackWorldMessage = config.getString("AllowEnd.FallBackWorld.Login.Message");

        getLogger().info("CRUTimer powered by　MrMatchesExMark");
        getLogger().info("Config loaded:");
        getLogger().info("  Allow command from " + allowCommandFrom + " to " + allowCommandTo);
        getLogger().info("  Allow end from " + allowEndFrom + " to " + allowEndTo);
        getLogger().info("    Fallback worlds:");
        getLogger().info("      Enter:" + enterFallBackWorldName + " x:" + enterFallBackWorldX + " Y:" + enterFallBackWorldY + " Z:" + enterFallBackWorldZ);
        getLogger().info("      Login:" + loginFallBackWorldName + " x:" + loginFallBackWorldX + " Y:" + loginFallBackWorldY + " Z:" + loginFallBackWorldZ);
    }

    @Override
    public void onDisable() {
        getLogger().info("CRUTimer has disable");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        if (allowCommand.contains(e.getMessage().toLowerCase())) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HHmm");
            int time = Integer.parseInt(format.format(now));
            if (!(time >= allowCommandFrom && time <= allowCommandTo)) {
                e.setCancelled(true);
                e.getPlayer().sendRawMessage(blockCommandMessage);
                getLogger().info(e.getPlayer().getDisplayName() + " try to use command " + e.getMessage() + " at " + time);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HHmm");
            int time = Integer.parseInt(format.format(now));
            if (!(time >= allowEndFrom && time <= allowEndTo)) {
                e.setCancelled(true);
                Player player = e.getPlayer();
                player.teleport(new Location(Bukkit.getWorld(enterFallBackWorldName), enterFallBackWorldX, enterFallBackWorldY, enterFallBackWorldZ));
                player.sendRawMessage(enterFallBackWorldMessage);
                getLogger().info(e.getPlayer().getDisplayName() + " try to enter the end at " + time);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent e) {
        if (e.getPlayer().getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HHmm");
            int time = Integer.parseInt(format.format(now));
            if (!(time >= allowEndFrom && time <= allowEndTo)) {
                Player player = e.getPlayer();
                player.teleport(new Location(Bukkit.getWorld(loginFallBackWorldName), loginFallBackWorldX, loginFallBackWorldY, loginFallBackWorldZ));
                player.sendRawMessage(loginFallBackWorldMessage);
                getLogger().info(e.getPlayer().getDisplayName() + " try to login at the end at " + time);
            }
        }
    }
}
