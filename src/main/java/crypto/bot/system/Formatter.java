package crypto.bot.system;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter {
    private static final SimpleDateFormat SIMPLE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String formatDate(long timestamp) {
        return SIMPLE_FORMATTER.format(new Date(timestamp));
    }

    public static String formatDecimal(double decimal) {
        if ((decimal == Math.floor(decimal)) && Double.isFinite(decimal)) return String.valueOf((long) decimal);
        int zeroes = 0;
        String s = String.format("%.12f", decimal).replaceAll("[,.]", "");
        for (char c : s.toCharArray()) {
            if (c == '0') {
                zeroes++;
            } else if (c != '-') {
                break;
            }
        }
        NumberFormat decimalFormat = new DecimalFormat("0." + "0".repeat(3 + zeroes));
        return decimalFormat.format(decimal);
    }
}
