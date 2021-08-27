package lucien2714.lucien2714;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.util.*;

public final class RebornWithDifferentBuff extends JavaPlugin {

    private static RebornWithDifferentBuff plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        System.out.println("ck Plugin Started");
        Collection<? extends Player> temp = getServer().getOnlinePlayers();
        for (Player p : temp
        ) {
            Game.add(p);
        }
        getServer().getPluginManager().registerEvents(new playerJoins(), this);
        getServer().getPluginManager().registerEvents(new playerLeaves(), this);
        getServer().getPluginManager().registerEvents(new playerDies(), this);
        getServer().getPluginManager().registerEvents(new playerReborn(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RebornWithDifferentBuff getplugin() {
        return plugin;
    }
}

class playerJoins implements Listener {
    @EventHandler
    public void playerJoins(PlayerJoinEvent e) {
        e.joinMessage(Component.text("欢迎加入本服务器"));
        System.out.println("player added");
        Player p = e.getPlayer();
        Game.add(p);
        System.out.println("player added");
    }
}

class playerLeaves implements Listener {
    @EventHandler
    public void playerLeaves(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        player temp = null;
        for (player t : Game.OnlinePlayers
        ) {
            if (t.p == p) {
                temp = t;
                int index = -1;
                for (int i = 0; i < 31; i++) {
                    if (player.all[i] == t.buff.getType())
                        index = i;
                }
                storage.saveData(saveData.save(temp.p.displayName().toString(), temp.buffLevel, index));
                break;
            }
        }
        if (temp != null) {

            Game.OnlinePlayers.remove(temp);
        }
    }
}

class playerReborn implements Listener {
    @EventHandler
    public void playerReborn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        for (player t : Game.OnlinePlayers
        ) {
            if (t.p == p) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RebornWithDifferentBuff.getplugin(), () -> t.p.addPotionEffect(t.buff), 5);
                //t.p.addPotionEffect(t.buff);
                System.out.println(t.buff.getType().getName());
                System.out.println(t.buff.getAmplifier());
                System.out.println(t.buff.getDuration());
                Game.sendMessageToAll(p.displayName().append(Component.text("重生并获得了能力:")), t.buff.getType().getName());
                break;
            }
        }

    }
}

class playerDies implements Listener {
    @EventHandler
    public void playerDies(PlayerDeathEvent e) {
        Player p = e.getEntity();
        for (player t : Game.OnlinePlayers
        ) {
            if (t.p == p) {
                t.upgrade();
                break;
            }
        }
    }
}

class Game {
    public static ArrayList<player> OnlinePlayers = new ArrayList<player>();

    public static void sendMessageToAll(Component main, String what) {
        for (player p : OnlinePlayers
        ) {
            Component msg = main.append(Component.text(what).color(TextColor.color(255, 0, 0)));
            p.p.sendMessage(msg);
        }
    }

    public static void add(Player p) {
        player temp = new player();
        temp.p = p;
        ArrayList<saveData> saved = storage.getData();
        if (saved != null) {
            for (saveData sd : saved
            ) {
                if (p.getName().equals(sd.pName)) {
                    temp.buffLevel = sd.buffLevel;
                    temp.buff = new PotionEffect(player.all[sd.buffIndex], 60 * 60 * 60 * 60, temp.buffLevel);
                }
            }
        }
        Game.OnlinePlayers.add(temp);
    }
}

class player {
    public static PotionEffectType[] all = {
            PotionEffectType.ABSORPTION, PotionEffectType.CONDUIT_POWER, PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.FAST_DIGGING, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.BAD_OMEN,
            PotionEffectType.INCREASE_DAMAGE, PotionEffectType.GLOWING, PotionEffectType.INVISIBILITY,
            PotionEffectType.JUMP, PotionEffectType.LUCK, PotionEffectType.LEVITATION,
            PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.DOLPHINS_GRACE,
            PotionEffectType.HEALTH_BOOST, PotionEffectType.HERO_OF_THE_VILLAGE, PotionEffectType.HUNGER,
            PotionEffectType.NIGHT_VISION, PotionEffectType.POISON, PotionEffectType.REGENERATION,
            PotionEffectType.SATURATION, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING,
            PotionEffectType.SLOW_FALLING, PotionEffectType.SPEED, PotionEffectType.UNLUCK,
            PotionEffectType.WATER_BREATHING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER
    };
    Player p;
    PotionEffect buff = null;
    int buffLevel = 1;

    public void upgrade() {
        Random ran = new Random();
        int index = ran.nextInt(31);
        if (this.buff != null) {
            if (buff.getType() == all[index]) {
                this.buffLevel += 1;
                this.buff = this.buff.withAmplifier(buffLevel);
                this.p.removePotionEffect(all[index]);
            } else {
                this.buffLevel = 1;
                this.p.removePotionEffect(this.buff.getType());
                this.buff = new PotionEffect(all[index], 60 * 60 * 60 * 60 * 60, 1);
            }
        } else {
            this.buffLevel = 1;
            this.buff = new PotionEffect(all[index], 60 * 60 * 60 * 60 * 60, 1);
        }
    }
}

class storage {
    public static File f = new File("data.json");

    public static boolean writeToFile(String str) {//write data to file
        try (FileWriter writer = new FileWriter(f);
             BufferedWriter out = new BufferedWriter(writer)
        ) {
            out.write(str);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveData(saveData s) {
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<saveData> Data = getData();
        boolean isInData = false;
        if (!Data.isEmpty()) {
            for (saveData temp : Data
            ) {
                if (Objects.equals(temp.pName, s.pName)) {
                    temp = s;
                    isInData = true;
                }
            }
            if(!isInData){
                Data.add(s);
            }
        } else Data.add(s);
        Gson gson = new Gson();
        if (!writeToFile(gson.toJson(Data)))
            System.out.println("保存文件失败");
    }

    public static ArrayList<saveData> getData() {
        String rawData = readToString("data.json");
        Gson gson = new Gson();
        return gson.fromJson(rawData, new TypeToken<List<saveData>>() {
        }.getType());
    }

    public static String readToString(String fileName) {//read file
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}

class saveData {
    String pName;
    int buffLevel, buffIndex;

    public static saveData save(String pName, int buffLevel, int buffIndex) {
        saveData temp = new saveData();
        temp.pName = pName;
        temp.buffIndex = buffIndex;
        temp.buffLevel = buffLevel;
        return temp;
    }
}