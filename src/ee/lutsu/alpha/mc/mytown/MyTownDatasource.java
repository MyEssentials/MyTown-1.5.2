package ee.lutsu.alpha.mc.mytown;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import ee.lutsu.alpha.mc.mytown.entities.Nation;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.Town;
import ee.lutsu.alpha.mc.mytown.entities.TownBlock;
import ee.lutsu.alpha.mc.mytown.sql.MyTownDB;

public class MyTownDatasource extends MyTownDB {
	public static MyTownDatasource instance = new MyTownDatasource();

	public HashSet<Resident> residents = new HashSet<Resident>();
	public HashSet<Town> towns = new HashSet<Town>();
	public HashSet<TownBlock> blocks = new HashSet<TownBlock>();
	public HashSet<Nation> nations = new HashSet<Nation>();

	public void init() throws Exception {
		residents = new HashSet<Resident>();
		towns = new HashSet<Town>();
		blocks = new HashSet<TownBlock>();
		nations = new HashSet<Nation>();

		dispose();
		connect();
		load();

		towns.addAll(loadTowns());
		residents.addAll(loadResidents()); // links to towns

		for (Town t : towns) {
			for (TownBlock res : t.blocks()) {
				if (res.owner_name != null) // map block owners
				{
					Resident r = getResident(res.owner_name);
					res.sqlSetOwner(r);
					res.owner_name = null;
				}

				blocks.add(res); // add block to global list
			}
		}

		nations.addAll(loadNations());

		addAllOnlinePlayers();
	}

	public void addAllOnlinePlayers() {
		for (Object obj : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayer pl = (EntityPlayer) obj;
			getOrMakeResident(pl);
		}
	}

	public void addTown(Town t) {
		towns.add(t);
	}

	public void addNation(Nation n) {
		nations.add(n);
	}

	public TownBlock getOrMakeBlock(int world_dimension, int x, int z) {
		long start = System.nanoTime();
		for (TownBlock res : blocks) {
			if (res.equals(world_dimension, x, z)) {
				long stop = System.nanoTime();
				Log.info("getOrMakeBlock took: %l", stop - start);
				return res;
			}
		}

		TownBlock res = new TownBlock(world_dimension, x, z);
		blocks.add(res);
		long stop = System.nanoTime();
		Log.info("getOrMakeBlock took: %l", stop - start);
		return res;
	}

	public TownBlock getBlock(int world_dimension, int x, int z) {
		long start = System.nanoTime();
		for (TownBlock res : blocks) {
			if (res.equals(world_dimension, x, z)) {
				long stop = System.nanoTime();
				Log.info("getBlock took: %l", stop - start);
				return res;
			}
		}
		long stop = System.nanoTime();
		Log.info("getBlock took: %l", stop - start);
		return null;
	}

	public TownBlock getPermBlockAtCoord(int world_dimension, int x, int y, int z) {
		return getPermBlockAtCoord(world_dimension, x, y, y, z);
	}

	public TownBlock getPermBlockAtCoord(int world_dimension, int x, int yFrom, int yTo, int z) {
		TownBlock targetBlock = getBlock(world_dimension, ChunkCoord.getCoord(x), ChunkCoord.getCoord(z));
		if (targetBlock != null && targetBlock.settings.yCheckOn) {
			if (yTo < targetBlock.settings.yCheckFrom || yFrom > targetBlock.settings.yCheckTo) {
				targetBlock = targetBlock.getFirstFullSidingClockwise(targetBlock.town());
			}
		}

		return targetBlock;
	}

	public Town getTown(String name) {
		long start = System.nanoTime();
		for (Town res : towns) {
			if (res.name().equalsIgnoreCase(name)) {
				long stop = System.nanoTime();
				Log.info("getTown took: %l", stop - start);
				return res;
			}
		}
		
		long stop = System.nanoTime();
		Log.info("getTown took: %l", stop - start);

		return null;
	}

	@Override
	public Town getTown(int id) {
		long start = System.nanoTime();
		for (Town res : towns) {
			if (res.id() == id) {
				long stop = System.nanoTime();
				Log.info("getTown took: %l", stop - start);
				return res;
			}
		}
		long stop = System.nanoTime();
		Log.info("getTown took: %l", stop - start);

		return null;
	}

	public Nation getNation(String name) {
		for (Nation res : nations) {
			if (res.name().equalsIgnoreCase(name)) {
				return res;
			}
		}

		return null;
	}

	public synchronized Resident getOrMakeResident(EntityPlayer player) {
		long start = System.nanoTime();
		for (Resident res : residents) {
			if (res.onlinePlayer == player) {
				long stop = System.nanoTime();
				Log.info("getOrMakeResident took: %l", stop - start);
				return res;
			}
		}

		Resident r = getOrMakeResident(player.getEntityName());
		r.onlinePlayer = player;
		long stop = System.nanoTime();
		Log.info("getOrMakeResident took: %l", stop - start);
		return r;
	}

	public Resident getResident(EntityPlayer player) {
		long start = System.nanoTime();
		for (Resident res : residents) {
			if (res.onlinePlayer == player) {
				long stop = System.nanoTime();
				Log.info("getResident took: %l", stop - start);
				return res;
			}
		}
		long stop = System.nanoTime();
		Log.info("getResident took: %l", stop - start);

		return null;
	}

	public Resident getOrMakeResident(String name) // case sensitive
	{
		long start = System.nanoTime();
		for (Resident res : residents) {
			if (res.name().equals(name)) {
				long stop = System.nanoTime();
				Log.info("getOrMakeResident took: %l", stop - start);
				return res;
			}
		}

		Resident res = new Resident(name);
		residents.add(res);
		long stop = System.nanoTime();
		Log.info("getOrMakeResident took: %l", stop - start);
		return res;
	}

	public Resident getResident(String name) // case in-sensitive
	{
		long start = System.nanoTime();
		for (Resident res : residents) {
			if (res.name().equalsIgnoreCase(name)) {
				long stop = System.nanoTime();
				Log.info("getResident took: %l", stop - start);
				return res;
			}
		}
		long stop = System.nanoTime();
		Log.info("getResident took: %l", stop - start);

		return null;
	}

	public List<Resident> getOnlineResidents() {
		ArrayList<Resident> ret = new ArrayList<Resident>();
		for (Resident res : residents) {
			if (res.isOnline()) {
				ret.add(res);
			}
		}

		return ret;
	}

	public void unloadTown(Town t) {
		towns.remove(t);
	}

	public void unloadNation(Nation n) {
		nations.remove(n);
	}

	public void unloadBlock(TownBlock b) {
		b.settings.setParent(null);
		blocks.remove(b);
	}

	public void unloadResident(Resident r) {
		/*
		 * if (r.onlinePlayer == null && r.town() == null) residents.remove(r);
		 */
	}

	public int deleteAllTownBlocksInDimension(int dim) {
		int ret = 0;
		ArrayList<TownBlock> toRemove = new ArrayList<TownBlock>();
		for (TownBlock res : blocks) {
			if (res.worldDimension() == dim) {
				toRemove.add(res);
			}
		}

		ArrayList<Town> townsToSave = new ArrayList<Town>();
		for (TownBlock res : toRemove) {
			if (res.town() != null) {
				townsToSave.add(res.town());
				res.town().removeBlockUnsafe(res);
				ret++;
			} else {
				unloadBlock(res);
			}
		}

		for (Town t : townsToSave) {
			t.save();
		}

		return ret;
	}

	public List<Resident> getOldResidents(Date lastLoginTimeBelow) {
		ArrayList<Resident> players = new ArrayList<Resident>();
		synchronized (residents) {
			for (Resident res : residents) {
				if (res.town() != null && !res.isOnline() && res.lastLogin().compareTo(lastLoginTimeBelow) < 0) {
					players.add(res);
				}
			}
		}

		return players;
	}

	public List<Town> getOldTowns(long lastLoginTimeBelow, double plotDaysAddition) {
		ArrayList<Town> towns = new ArrayList<Town>();
		synchronized (residents) {
			for (Resident res : residents) {
				Date last = new Date(lastLoginTimeBelow - (res.town() != null ? (int) (plotDaysAddition * res.town().blocks().size()) : 0));
				if (res.town() != null && !res.isOnline() && res.lastLogin().compareTo(last) < 0) {
					if (!towns.contains(res.town())) {
						boolean allOld = true;
						for (Resident r : res.town().residents()) {
							if (r.isOnline() || r.lastLogin().compareTo(last) >= 0) {
								allOld = false;
								break;
							}
						}
						if (allOld) {
							towns.add(res.town());
						}
					}
				}
			}
		}

		return towns;
	}
}
