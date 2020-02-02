package party.lemons.betahealth;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CATEGORY_GENERAL = "general";
	
	public static int hungerValue = 10;
	public static int stackValue = 1;
	
    public static void readConfig() {
    	Configuration cfg = BetaHealth.config;
    	cfg.load();
        initGeneralConfig(cfg);
        if(cfg.hasChanged())
        {
        	cfg.save();
        }
    }
    
    private static void initGeneralConfig(Configuration cfg) {
    	cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
    	hungerValue = cfg.getInt("hungerAmount", CATEGORY_GENERAL, 10, 1, 20, "20 = fast regen, 18 = slow regen, 17 = no regen, 6 = no sprinting, 0 = health depletion");
    	stackValue = cfg.getInt("stackAmount", CATEGORY_GENERAL, 1, 1, 64, "sets the maximum stack size of all food");
    }
    	
}
