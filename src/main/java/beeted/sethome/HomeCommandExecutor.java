package beeted.sethome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class HomeCommandExecutor implements CommandExecutor {
    private final SetHome plugin;

    public HomeCommandExecutor(SetHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration config = plugin.getConfig();
        String userCommand = config.getString("menu.open-command", "/home").replace("/", "");

        // Verifica si el comando coincide con el configurado
        if (!label.equalsIgnoreCase(userCommand)) {
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("sethome.reload")) {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.plugin-reloaded", "&a插件已成功重新加载。 ")));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.no-permissions", "&c你没有权限执行此操作。")));
            }
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a正在打开家园菜单..."));
            // Lógica para abrir el menú
        } else {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家执行。 ");
        }
        return true;
    }

    public static void registerDynamicCommand(SetHome plugin, String commandName) {
        try {
            // Obtener el CommandMap
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            // Crear y registrar el comando dinámico
            Command dynamicCommand = new BukkitCommand(commandName) {
                @Override
                public boolean execute(CommandSender sender, String label, String[] args) {
                    return plugin.getCommandExecutor().onCommand(sender, this, label, args);
                }
            };
            ((BukkitCommand) dynamicCommand).setDescription("打开家园菜单。 ");
            commandMap.register(plugin.getDescription().getName(), dynamicCommand);
        } catch (Exception e) {
            plugin.getLogger().severe("注册动态命令失败： " + commandName);
            e.printStackTrace();
        }
    }

}
