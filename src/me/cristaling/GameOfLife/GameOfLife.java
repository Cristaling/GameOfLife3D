package me.cristaling.GameOfLife;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameOfLife extends JavaPlugin implements Listener {

	List<Block> theBlocks = new ArrayList<Block>();
	
	Material aliveCellMaterial = Material.REDSTONE_BLOCK;
	Material deadCellMaterial = Material.AIR;
	
	int loopTicks = 20;
	int currentTicks = 0;
	
	//2x2 platform on: 6745
	
	int a = 6;
	int b = 7;
	int c = 4;
	int d = 5;
	
	boolean paused = true;
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
			@Override
			public void run() {
				currentTicks++;
				if(currentTicks >= loopTicks){
					onGameTick();
					currentTicks = 0;
				}
			}
		}, 1, 1);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event){
		if(event.getBlock().getType() == aliveCellMaterial){
			theBlocks.add(event.getBlock());
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event){
		if(theBlocks.contains(event.getBlock())){
			theBlocks.remove(event.getBlock());
		}
	}
	
	public void onGameTick(){
		if(paused){
			return;
		}
		List<Block> theDead = new ArrayList<Block>();
		List<Block> newAlive = new ArrayList<Block>();
		for(Block block:theBlocks){
			int neighbours = 0;
			for(int xx=-1;xx<=1;xx++){
				for(int zz=-1;zz<=1;zz++){
					for(int yy=-1;yy<=1;yy++){
						if(xx != 0 || yy != 0 || zz != 0){
							if(theBlocks.contains(block.getRelative(xx, yy, zz))){
								neighbours++;
							}else{
								if(!theDead.contains(block.getRelative(xx, yy, zz))){
									theDead.add(block.getRelative(xx, yy, zz));
								}
							}
						}
					}
				}
			}
			//Bukkit.broadcastMessage("Alive with: " + neighbours);
			if(neighbours >= a && neighbours <= b){
				newAlive.add(block);
			}else{
				block.setType(deadCellMaterial);
			}
		}
		for(Block block:theDead){
			int neighbours = 0;
			for(int xx=-1;xx<=1;xx++){
				for(int zz=-1;zz<=1;zz++){
					for(int yy=-1;yy<=1;yy++){
						if(xx != 0 || yy != 0 || zz != 0){
							if(theBlocks.contains(block.getRelative(xx, yy, zz))){
								neighbours++;
							}
						}
					}
				}
			}
			//Bukkit.broadcastMessage("Dead with: " + neighbours);
			if(neighbours >= c && neighbours <= d){
				newAlive.add(block);
				block.setType(aliveCellMaterial);
			}
		}
		theBlocks = newAlive;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player send = (Player) sender;
			if(!send.hasPermission("gameoflife.admin")){
				send.sendMessage(ChatColor.RED + "You don't have permission to use this");
				return false;
			}
			if(label.equalsIgnoreCase("gol")){
				if(args.length == 1 && args[0].equalsIgnoreCase("clear")){
					for(Block block:theBlocks){
						block.setType(deadCellMaterial);
					}
					theBlocks.clear();
					paused = true;
					send.sendMessage(ChatColor.GOLD + "Game cleared and paused");
					return true;
				}
				if(args.length == 1 && args[0].equalsIgnoreCase("toggle")){
					paused = !paused;
					send.sendMessage(ChatColor.GOLD + "Game paused: " + paused);
					return true;
				}
				if(args.length == 5 && args[0].equalsIgnoreCase("set")){
					paused = true;
					a = Integer.parseInt(args[1]);
					b = Integer.parseInt(args[2]);
					c = Integer.parseInt(args[3]);
					d = Integer.parseInt(args[4]);
					send.sendMessage(ChatColor.GOLD + "Game rules set");
					return true;
				}
			}
		}
		return false;
	}
	
}
