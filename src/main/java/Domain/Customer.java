package Domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private static int nextId = 1;
    private final String id;
    private String name;
    private String phoneNumber;
    private List<Address> addresses;
    private BigDecimal walletBalance;
    private int completedOrderCount;
    private static final int SILVER_TIER_ORDERS =10;
    private static final int GOLD_TIER_ORDERS =30;

    public Customer(String name, String phoneNumber, BigDecimal walletBalance) {
        id="C-"+Integer.toString(nextId++);
        this.name = name;
        addresses=new ArrayList<>();
        setPhoneNumber(phoneNumber);
        this.walletBalance = walletBalance;
        completedOrderCount=0;
    }
    public void setPhoneNumber(String phoneNumber){
        if(isValidEgyptianPhone(phoneNumber)){
            this.phoneNumber = phoneNumber;
        }
        else{
            throw new IllegalArgumentException( "Invalid Egyptian mobile number");
        }
    }

    public void addAddress(Address address){
        addresses.add(address);
    }
    public void removeAddress(Address address){
        addresses.removeIf(a->a.equals(address));
    }
    public static int getNextId() {
        return nextId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Address> getAddresses() {
        return List.copyOf(addresses);
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public int getCompletedOrderCount() {
        return completedOrderCount;
    }

    public static boolean isValidEgyptianPhone(String phone) {
        return phone != null && phone.matches("01[0125]\\d{8}");
    }
    public void incrementCompletedOrderCount(){
        completedOrderCount++;
    }
    public LoyaltyTier getLoyalityTier(){
        if(completedOrderCount>= GOLD_TIER_ORDERS){
            return LoyaltyTier.Gold;
        }
        else if(completedOrderCount>= SILVER_TIER_ORDERS){
            return  LoyaltyTier.Silver;
        }
        else {
            return LoyaltyTier.Bronze;
        }
    }
}
