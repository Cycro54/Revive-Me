package invoker54.reviveme.common.config;


import invoker54.reviveme.ReviveMe;
import invoker54.reviveme.common.capability.FallenCapability;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = ReviveMe.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ReviveMeConfig {
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    public static Integer timeLeft;
    public static Integer reviveTime;
    public static Integer revivedHealth;
    public static FallenCapability.PENALTYPE penaltyType;
    public static Integer penaltyAmount;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void bakeConfig(){
        timeLeft = COMMON.timeLeft.get();
        reviveTime = COMMON.reviveTime.get();
        revivedHealth = COMMON.revivedHealth.get();
        penaltyType = COMMON.penaltyType.get();
        penaltyAmount = COMMON.penaltyAmount.get();
    }

    public static class CommonConfig {

        //This is how to make a config value
        //public static final ForgeConfigSpec.ConfigValue<Integer> exampleInt;
        public final ForgeConfigSpec.ConfigValue<Integer> timeLeft;
        public final ForgeConfigSpec.ConfigValue<Integer> reviveTime;
        public final ForgeConfigSpec.ConfigValue<Integer> revivedHealth;
        public final ForgeConfigSpec.EnumValue<FallenCapability.PENALTYPE> penaltyType;
        public final ForgeConfigSpec.ConfigValue<Integer> penaltyAmount;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            //This is what goes on top inside of the config
            builder.push("Revive Me! Config");
            //This is how you place a variable in the config file
            //exampleInt = BUILDER.comment("This is an integer. Default value is 3.").define("Example Integer", 54);
            timeLeft = builder.comment("How long you have before death. Default is 30 seconds").define("Time Left", 30);

            reviveTime = builder.comment("How long to revive someone").define("Revive Time", 3);

            revivedHealth = builder.comment("How much health you will be revived with, 0 is max health").defineInRange("Revive Health", 10, 0, Integer.MAX_VALUE);

            penaltyType = builder.comment("What the reviver will lose").defineEnum("Penalty Type", FallenCapability.PENALTYPE.HEALTH);

            penaltyAmount = builder.comment("Amount that will be taken from reviver").define("Penalty Amount", 10);

            builder.pop();
        }

    }

}
