package tv.unsouled_ghost.crystaldamage;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.bukkit.ChatColor.*;

public final class CrystalDamage extends JavaPlugin implements Listener {

    public int shielddamage = 50;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (!new File("CrystalDamage.yml").exists()) {
            try {
                FileWriter myWriter = new FileWriter("CrystalDamage.yml");
                myWriter.write("CrystalDamage: " + shielddamage);
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                File myObj = new File("CrystalDamage.yml");
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String nextLine = myReader.nextLine();
                    if (nextLine.contains("CrystalDamage:")) {
                        shielddamage = Integer.parseInt(nextLine.split(" ")[1]);
                    }
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                Bukkit.getConsoleSender().sendMessage("§ccan't read the file!§r");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player p && ((Player) event.getEntity()).isBlocking() && event.getDamager().getType() == EntityType.ENDER_CRYSTAL) {
            if (p.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
                Damageable itemdmg = (Damageable) p.getInventory().getItemInMainHand().getItemMeta();
                short newDurability = (short) (itemdmg.getDamage() + (Material.SHIELD.getMaxDurability() * (shielddamage / 100.0)));
                itemdmg.setDamage(newDurability);
                p.getInventory().getItemInMainHand().setItemMeta(itemdmg);
                if (newDurability >= Material.SHIELD.getMaxDurability()) {
                    p.getInventory().setItemInMainHand(null);
                }
            } else if (p.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                Damageable itemdmg = (Damageable) p.getInventory().getItemInOffHand().getItemMeta();
                short newDurability = (short) (itemdmg.getDamage() + (Material.SHIELD.getMaxDurability() * (shielddamage / 100.0)));
                itemdmg.setDamage(newDurability);
                p.getInventory().getItemInOffHand().setItemMeta(itemdmg);
                if (newDurability >= Material.SHIELD.getMaxDurability()) {
                    p.getInventory().setItemInOffHand(null);
                }
            }
            event.setDamage(0.0);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
            if (command.getName().equals("crystaldamage")) {
                if (sender.isOp()) {
                    if (args.length == 1 && args[0].equals("query")) {
                        sender.sendMessage("The Damage from Endcrystals to Shields is currently " + shielddamage + "%");
                    } else if (args.length == 2 && args[0].equals("set")) {
                        try {
                            int damage = Integer.parseInt(args[1]);
                            if (damage >= 0 && damage <= 100) {
                                shielddamage = damage;
                                FileWriter myWriter = new FileWriter("CrystalDamage.yml");
                                myWriter.write("CrystalDamage: " + shielddamage);
                                myWriter.close();
                                sender.sendMessage("The Damage from Endcrystals to Shields ist now set to " + shielddamage + "%");
                            }
                        } catch (Exception e) {
                            sender.sendMessage(RED + "You can only use Intagers between 0 and 100!");
                        }
                    } else {
                        sender.sendMessage("/crystaldamage query " + RED + "to view the current value or\n" + WHITE + "/crystaldamage set [0-100] " + RED + "to set the value");
                    }
                } else {
                    sender.sendMessage(RED + "You don't have the permission to do this!");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            if (command.getName().equals("crystaldamage")) {
                if (args.length == 1) {
                    return Arrays.asList("set", "query");
                } else if (args.length == 2 && args[0].equals("set")) {
                    List<String> intret = new ArrayList<>();
                    for (int i = 0; i < 100; i++) {
                        intret.add(Integer.toString(i + 1));
                    }
                    return intret;
                } else {
                    return new ArrayList<>();
                }
            }
        }
        return null;
    }
}
