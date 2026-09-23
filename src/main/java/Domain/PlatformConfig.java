package Domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//singleton
public class PlatformConfig {
    private static final PlatformConfig INSTANCE=new PlatformConfig();
    private final BigDecimal BASE_DELIVERY_FEE=BigDecimal.valueOf(15);
    private final BigDecimal EXTRA_KM_FEE=BigDecimal.valueOf(3);
    private final BigDecimal SERVICE_FEE_RATE=BigDecimal.valueOf(0.1);
    private final List<String> riderDashboard = new ArrayList<>();
    private PlatformConfig(){

    }
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
    public void addRiderDashboardEntry(String message) {
        riderDashboard.add(message);
    }

    public List<String> getRiderDashboard() {
        return List.copyOf(riderDashboard);
    }
}
