package net.tnemc.hellconomy.core;

import net.tnemc.hellconomy.core.currency.HellCurrency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by creatorfromhell.
 *
 * HellConomy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class CurrencyManager {

  private static final BigDecimal largestSupported = new BigDecimal("900000000000000000000000000000000000000000000");
  private Map<String, HellCurrency> globalCurrencies = new HashMap<>();
}