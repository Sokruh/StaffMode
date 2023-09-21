package sokru.sokrustaff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SokruSTAFF extends JavaPlugin implements Listener {
    public List<String> invisible;

    public void enableVanish(Player player) {
        for (Player other : getServer().getOnlinePlayers()) {
            if(other.hasPermission("Staff.showvanished")) {
                continue;
            }
            other.hidePlayer(player);
        }
        player.sendMessage(ChatColor.RED + "Olet nyt näkymätön muille pelaajille!");
    }

    public void disableVanish(Player player) {
        if (invisible.remove(player.getName())) {
            for (Player other : getServer().getOnlinePlayers()) {
                other.showPlayer(player);
            }
            player.sendMessage(ChatColor.RED + "Et ole enään näkymätön!");
        } else {
            player.sendMessage(ChatColor.RED + "Et ole näkymätön!");
        }
    }

    public void showVanishList(Player player) {
        String result = "";
        boolean first = true;
        for (String hidden : invisible) {
            if(getServer().getPlayerExact(hidden) == null)
                continue;

            if (first) {
                result += hidden;
                first = false;
                continue;
            }

            result += ", " + hidden;
        }

        if(result.length() == 0)
            player.sendMessage(ChatColor.RED + "Kaikki pelaajat ovat nähtävissä!");
        else
            player.sendMessage(ChatColor.RED + "Näkymättömät pelaajat: " + result);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        invisible = getConfig().getStringList("vanished");

    }

    @Override
    public void onDisable() {
        getConfig().set("vanished", invisible);
        saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("vanish")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                showVanishList(player);
            }
            else {
                if (invisible.contains(player.getName())) {
                    player.sendMessage(ChatColor.RED + "Olet jo näkymätön!");
                }
                else {
                    invisible.add(player.getName());
                    enableVanish(player);
                }
            }
        }
        else if (command.getName().equalsIgnoreCase("unvanish")) {
            disableVanish((Player) sender);
        }

        return true;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (invisible.contains(event.getPlayer().getName())) {
            event.setJoinMessage("");
            enableVanish(event.getPlayer());
        }
        if (event.getPlayer().hasPermission("Staff.showvanished"))
            return;

        for (String hidden : invisible) {
            Player hiddenPlayer = getServer().getPlayerExact(hidden);
            if (hiddenPlayer != null) {
                event.getPlayer().hidePlayer(hiddenPlayer);
            }
        }
    }
}
