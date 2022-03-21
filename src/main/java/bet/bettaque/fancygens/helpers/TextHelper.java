package bet.bettaque.fancygens.helpers;

import de.themoep.minedown.MineDown;
import io.th0rgal.oraxen.OraxenPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class TextHelper {
    public static String formatScore(double score){
        return withSuffix(score);
    }

    public static String formatMultiplier(double multiplier, boolean icon, Player player){
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (icon) {
            String multiIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_multi%");
            return  ChatColor.LIGHT_PURPLE + "x" + df.format(multiplier) + " " + multiIcon;
        }
        return df.format(multiplier);
    }



    public static String formatCurrency(double number, Player player){
        String coinsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_coins%");
        return ChatColor.YELLOW + withSuffix(number) + " " + coinsIcon;
    }

    public static String formatPoints(double number, Player player){
        String pointsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_points%");
        return ChatColor.AQUA + withSuffix(number) + " " + pointsIcon;
    }

    public static String formatPrestige(double number, Player player){
        String pointsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_prestige%");
        return ChatColor.GOLD + pointsIcon + " " + (int) number;
    }

    public static String formatSlots(double number, Player player){
        String slotsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_slots%");
        return ChatColor.WHITE + withSuffix((long) number) + " " + slotsIcon;
    }

    public static String formatGems(double number, Player player){
        String gemsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_gems%");
        return ChatColor.GREEN.toString() + Math.round(number) + " " + gemsIcon;
    }

    private static final String[] Q = new String[]{
            "", "K", "m", "M", "b", "B", "t", "T" ,"q", "Q", "qq", "Qq", "s", "S", "ss", "Ss", "o", "O", "n", "N", "d",
            "D", "u", "U", "dd", "Dd", "tt", "Tt", "qq", "Qq", "QQ", "qqq", "SS", "sss", "Sss", "SSs", "oo", "Oo", "nn",
            "Nn", "v", "V", "Qqq","TT", "QQq", "QQQ", "QQQq", "QQQQ", "uu", "qqqqq", "SSS", "ssss", "uU", "Qqqqq", "Ssss",
            "QQqqq", "OO", "QQQqq", "NN", "QQQQq", "c", "QQQQQ", "DD", "qqqqqq", "ttt", "Qqqqqq", "QQqqqq", "QQQqqq", "QQQQqq"
    };

    public static String getAsString(double bytes)
    {
        for (int i = Q.length -1; i > 0; i--)
        {
            double step = Math.pow(1000, i);
            if (bytes > step) return String.format("%3.1f %s", bytes / step, Q[i]);
        }
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(bytes);
    }



    public static String withSuffix(double count) {
//        if (count < 1000) return "" + count;
//        int exp = (int) (Math.log(count) / Math.log(1000));
//
//        return String.format("%.1f %c",
//                count / Math.pow(1000, exp),
//                "KMBtqQsS".charAt(exp-1));

        return TextHelper.getAsString(count);
    }

    public static String parseFancyString(String string){
        BaseComponent[] fancyComponents = new MineDown(string).toComponent();
        return BaseComponent.toLegacyText(fancyComponents);
    }

    public static BaseComponent[] parseFancyComponents(String string){
        return new MineDown(string).toComponent();
    }

}
