package net.tnemc.hellconomy.core.currency;

import net.tnemc.core.economy.currency.Currency;
import net.tnemc.hellconomy.core.HellConomy;
import net.tnemc.hellconomy.core.utils.Message;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by Daniel on 7/16/2017.
 */
public class CurrencyFormatter {

  public static String format(String world, BigDecimal amount) {
    return format(HellConomy.manager().currencyManager().get(world), world, amount);
  }

  public static String format(String world, String name, BigDecimal amount) {
    return format(HellConomy.manager().currencyManager().get(world, name), world, amount);
  }

  public static String format(String world, String name, BigDecimal amount, String format) {
    return format(HellConomy.manager().currencyManager().get(world, name), world, amount, format);
  }

  public static String format(Currency currency, String world, BigDecimal amount) {
    return format(HellConomy.manager().currencyManager().get(world, currency.name()), world, amount);
  }

  public static String format(HellCurrency currency, String world, BigDecimal amount) {

    if(currency == null) currency = HellConomy.manager().currencyManager().get(HellConomy.instance().getDefaultWorld());
    return format(currency, world, amount, ((currency.shorten())? "<symbol><short.amount>" : currency.getFormat()));
  }

  public static String format(HellCurrency currency, String world, BigDecimal amount, String format) {

    if(currency == null) currency = HellConomy.manager().currencyManager().get(HellConomy.instance().getDefaultWorld());

    amount = round(world, currency.name(), amount);

    String[] amountStr = (String.valueOf(amount) + (String.valueOf(amount).contains(".")? "" : ".00")).split("\\.");
    final BigInteger major = new BigInteger(amountStr[0]);
    final BigInteger minor = new BigInteger(String.format("%-" + currency.getDecimalPlaces() + "s", amountStr[1]).replace(' ', '0'));
    final String majorName = (major.compareTo(BigInteger.ONE) == 0)? currency.name() : currency.plural();
    final String minorName = (minor.compareTo(BigInteger.ONE) == 0)? currency.getSingleMinor() : currency.getPluralMinor();

    Map<String, String> replacements = new HashMap<>();
    replacements.put("<symbol>", currency.symbol());
    replacements.put("<decimal>", currency.getDecimal());
    replacements.put("<major>", major.toString() + " " + majorName);
    replacements.put("<minor>", minor.toString() + " " + minorName);
    replacements.put("<major.name>", majorName);
    replacements.put("<minor.name>", minorName);
    replacements.put("<major.amount>", major.toString() + "");
    replacements.put("<minor.amount>", String.format("%0" + currency.getDecimalPlaces() + "d", Integer.valueOf(minor.toString())).replace(' ', '0'));
    replacements.put("<short.amount>", shorten(currency, amount));
    replacements.putAll(Message.colours);

    String formatted = format;

    for(Map.Entry<String, String> entry : replacements.entrySet()) {
      formatted = formatted.replace(entry.getKey(), entry.getValue());
    }
    return formatted;
  }

  public static BigDecimal round(String world, String currency, BigDecimal amount) {
    if(HellConomy.manager().currencyManager().contains(world, currency)) {
      return amount.setScale(HellConomy.manager().currencyManager().get(world, currency).decimalPlaces(), BigDecimal.ROUND_CEILING);
    }

    if(HellConomy.manager().currencyManager().contains(world)) {
      return amount.setScale(HellConomy.manager().currencyManager().get(world).decimalPlaces(), BigDecimal.ROUND_CEILING);
    }
    return amount.setScale(HellConomy.manager().currencyManager().get(HellConomy.instance().getDefaultWorld()).decimalPlaces(), BigDecimal.ROUND_CEILING);
  }

  public static String parseAmount(HellCurrency currency, String world, String amount) {
    if(amount.length() > 40) return "Messages.Money.ExceedsCurrencyMaximum";
    if(isBigDecimal(amount, currency.name(), world)) {
      BigDecimal translated = translateBigDecimal(amount, currency.name(), world);
      if(translated.compareTo(currency.getMaxBalance()) > 0) {
        return "Messages.Money.ExceedsCurrencyMaximum";
      }
      translated = parseWeight(currency, translated);
      return translated.toPlainString();
    }
    String updated = amount.replaceAll(" ", "");
    if(!currency.getPrefixes().contains(updated.charAt(updated.length() - 1) + "")) {
      return "Messages.Money.InvalidFormat";
    }
    return fromShort(currency, updated);
  }

  private static BigDecimal parseWeight(HellCurrency currency, BigDecimal decimal) {
    String[] amountStr = (String.valueOf(decimal) + (String.valueOf(decimal).contains(".")? "" : ".00")).split("\\.");
    BigInteger major = new BigInteger(amountStr[0]);
    BigInteger minor = new BigInteger(String.format("%-" + currency.getDecimalPlaces() + "s", amountStr[1]).replace(' ', '0'));
    BigInteger majorConversion = minor;
    majorConversion = majorConversion.divide(new BigInteger(currency.getMinorWeight() + ""));
    major = major.add(majorConversion);
    minor = minor.mod(new BigInteger(currency.getMinorWeight() + ""));
    final String minorFinal = String.format("%0" + currency.getDecimalPlaces() + "d", Integer.valueOf(minor.toString())).replace(' ', '0');
    return new BigDecimal(major.toString() + "." + minorFinal);
  }

  private static String shorten(HellCurrency currency, BigDecimal balance) {
    final BigInteger wholeNum = balance.toBigInteger();
    if (wholeNum.compareTo(new BigInteger("1009")) <= 0) {
      return "" + wholeNum.toString();
    }
    final String whole = wholeNum.toString();
    final int pos = ((whole.length() - 1) / 3) - 1;
    final int posInclude = ((whole.length() % 3) == 0)? 3 : whole.length() % 3;
    String wholeSub = whole.substring(0, posInclude);

    if(whole.length() > 3) {
      String extra = whole.substring(posInclude, posInclude + 2);
      if (Integer.valueOf(extra) > 0) {
        if (extra.endsWith("0")) {
          extra = extra.substring(0, extra.length() - 1);
        }
        wholeSub = wholeSub + "." + extra;
      }
    }

    return wholeSub + currency.getPrefixes().charAt(pos);
  }
  private static String fromShort(HellCurrency currency, String amount) {
    int charIndex = currency.getPrefixes().indexOf(amount.charAt(amount.length() - 1)) + 1;
    String sub = amount.substring(0, amount.length() - 1);
    String form = "%-" + ((charIndex * 3) + sub.length()) + "s";
    return String.format(form, sub).replace(' ', '0');
  }

  public static boolean isBigDecimal(String value, String world) {
    String major = HellConomy.manager().currencyManager().get(world).name();
    return isBigDecimal(value, major, world);
  }

  private static boolean isBigDecimal(String value, String currency, String world) {
    String decimal = (HellConomy.manager().currencyManager().get(world, currency)).getDecimal();
    try {
      new BigDecimal(value.replace(decimal, "."));
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  public static BigDecimal translateBigDecimal(String value, String world) {
    String major = HellConomy.manager().currencyManager().get(world).name();
    return translateBigDecimal(value, major, world);
  }

  public static BigDecimal translateBigDecimal(String value, String currency, String world) {
    String decimal = (HellConomy.manager().currencyManager().get(world, currency)).getDecimal();
    return new BigDecimal(value.replace(decimal, "."));
  }
}