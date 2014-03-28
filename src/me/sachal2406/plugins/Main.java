package me.sachal2406.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Main plugin;
	FileConfiguration config;
	File cfile;
	//c'est très le swagg du slime   
	
	@Override
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " a ete desactive");
		
	}
		
	
	@Override
	public void onEnable(){
		grecipe();
		getServer().getPluginManager().registerEvents(this, this);
		config = getConfig();
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("------------------------------------- Infos FR ----------------------------------------------");
		this.logger.info("[MobGuns]" + " Version " + pdfFile.getVersion() + " a été activé");
		this.logger.info("Ce serveur utilise " + getServer().getVersion() + " sur l'ip " + getServer().getIp() + ":" + getServer().getPort() + "!");
		this.logger.info("[MobGuns] Chargement de la config...");
		saveDefaultConfig();
		this.logger.info("Fin du chargement du plugin.");
		this.logger.info("Merci de l'avoir téléchargé!");
		this.logger.info("---------------------------------------------------------------------------------------------");
		this.logger.info("------------------------------------- Infos EN ----------------------------------------------");
		this.logger.info("[MobGuns]" + " Version " + pdfFile.getVersion() + " has been activated");
		this.logger.info("This server's using " + getServer().getVersion() + " with the IP " + getServer().getIp() + ":" + getServer().getPort() + "!");
		this.logger.info("[MobGuns] Loading config...");
		this.logger.info("Plugin loading successfully finished.");
		this.logger.info("Thanks for downloading!");
		this.logger.info("---------------------------------------------------------------------------------------------");
		cfile = new File(getDataFolder(), "config.yml");		
    }
	
	ArrayList<Player> cooldown = new ArrayList<Player>();
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	
    	final Player player = e.getPlayer();
    	Location location = player.getLocation();
    	PotionEffectType dr = PotionEffectType.DAMAGE_RESISTANCE;
    			
    		if(!cooldown.contains(player)) {
            	if (!(e.getAction() == Action.RIGHT_CLICK_AIR)) return;
           
            	if (!(e.getItem().getType() == Material.BLAZE_ROD)) return;
                	Pig pig = (Pig)e.getPlayer().getWorld().spawnEntity(player.getLocation().add(0.0D, 2.0D, 0.0D), EntityType.PIG);
                	pig.setVelocity(player.getLocation().getDirection().multiply(5));
                	pig.addPotionEffect(new PotionEffect(dr, 80, 100));
                	e.getItem().setDurability((short) (e.getItem().getDurability() +1));
                	player.playSound(location, Sound.CHICKEN_EGG_POP, 1, (float) 0.1);
                	player.sendMessage(config.getString("Prefix").replaceAll("&", "§") + config.getString("Mob-Launched").replaceAll("&", "§"));
                	cooldown.add(player);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							
                                cooldown.remove(player);
                                player.sendMessage(config.getString("Prefix").replaceAll("&", "§") + config.getString("Gun-Ready").replaceAll("&", "§"));
						}    
                    }, 100);          	
    		}
    		else {
    			player.sendMessage(config.getString("Prefix").replaceAll("&", "§") + config.getString("Wait-Before-Using-Again").replaceAll("&", "§"));
    			return;
    		}
    		       
    }

    private void grecipe() {
    	ItemStack blazerod = new ItemStack(Material.BLAZE_ROD, 1);
    	ItemMeta meta2 = blazerod.getItemMeta();
    	meta2.setDisplayName(ChatColor.GREEN + "§lLanceur de slime");
    	meta2.setLore(Arrays.asList("§7Clique droit pour lancer un slime"));
    	blazerod.setItemMeta(meta2);
       
    	ShapelessRecipe grecipe = new ShapelessRecipe(blazerod);
        grecipe.addIngredient(1, Material.BLAZE_ROD);
        grecipe.addIngredient(1, Material.SLIME_BALL);
        Bukkit.getServer().addRecipe(grecipe);
        
    	}
    
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		
		if(cmd.getName().equalsIgnoreCase("mg")){
			sender.sendMessage("=============================");
			sender.sendMessage("§3§lPlugin by sachal2406");
			sender.sendMessage("§6§lCommands:");
			sender.sendMessage("  §c§l- §c§l§n/mgreload§c§l §3| Reloads the config");
			sender.sendMessage("=============================");
		
		}else if(cmd.getName().equalsIgnoreCase("mgreload")){
			this.reloadConfig();
			this.saveDefaultConfig();
			sender.sendMessage(config.getString("Prefix").replaceAll("&", "§") + config.getString("Config-Reloaded").replaceAll("&", "§"));
		}
		return true;
	}
	
	public void onProjectileHit(ProjectileHitEvent e) {
		
		Pig pig = (Pig) e.getEntity();
		Firework f = (Firework) pig.getWorld().spawn(pig.getLocation(), Firework.class);
		
        pig.addPotionEffect(new PotionEffect (PotionEffectType.HARM, 1, 100));
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                        .flicker(false)
                        .trail(true)
                        .with(Type.STAR)
                        .withColor(Color.GREEN)
                        .withFade(Color.BLUE)
                        .build());
        fm.setPower(0);
        f.setFireworkMeta(fm);
		
		
	}
	

    
}
