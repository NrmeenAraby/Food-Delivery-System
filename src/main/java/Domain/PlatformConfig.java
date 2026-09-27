package Domain;

import java.math.BigDecimal;


//singleton
public class PlatformConfig {
    private static final PlatformConfig INSTANCE=new PlatformConfig();
    private final BigDecimal BASE_DELIVERY_FEE=BigDecimal.valueOf(15);
    private final BigDecimal EXTRA_KM_FEE=BigDecimal.valueOf(3);
    private final BigDecimal SERVICE_FEE_RATE=BigDecimal.valueOf(0.1);
    private PlatformConfig(){}
    public static PlatformConfig getInstance(){
        return INSTANCE;
    }

    public BigDecimal getBaseDeliveryFee() {
        return BASE_DELIVERY_FEE;
    }

    public BigDecimal getExtraKmFee() {
        return EXTRA_KM_FEE;
    }

    public BigDecimal getServiceFeeRate() {
        return SERVICE_FEE_RATE;
    }

}
