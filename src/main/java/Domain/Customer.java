package Domain;

import Exceptions.InsufficientWalletException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Customer {
    private static AtomicInteger nextId = new AtomicInteger(1);
    private final String id;
    private String name;
    private String phoneNumber;
    private List<Address> addresses;
    private BigDecimal walletBalance;
    private int completedOrderCount;
    private static final int SILVER_TIER_ORDERS =10;
    private static final int GOLD_TIER_ORDERS =30;
    private Deque<SearchCriteria> lastSearches;
    public Customer(String name, String phoneNumber, BigDecimal walletBalance) {
        id="C-"+nextId.getAndIncrement();
        this.name = name;
        addresses=new ArrayList<>();
        setPhoneNumber(phoneNumber);
        setWalletBalance(walletBalance);
        completedOrderCount=0;
    }
    public void addSearch(SearchCriteria search){
        if(lastSearches==null){
            lastSearches=new ArrayDeque<>();
        }
        lastSearches.addFirst(search);
        if(lastSearches.size()>5) {
            lastSearches.removeLast();
        }
    }
    public void setPhoneNumber(String phoneNumber){
        if(isValidEgyptianPhone(phoneNumber)){
            this.phoneNumber = phoneNumber;
        }
        else{
            throw new IllegalArgumentException( "Invalid Egyptian mobile number");
        }
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        if(walletBalance==null ||walletBalance.compareTo(BigDecimal.ZERO)<0)
            throw new InsufficientWalletException("Invalid wallet balance");
        this.walletBalance = walletBalance;
    }

    public void addAddress(Address address){
        addresses.add(address);
    }
    public void removeAddress(Address address){
        addresses.removeIf(a->a.equals(address));
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
    public LoyaltyTier getLoyaltyTier(){
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
    public void addMoney(BigDecimal amount){
        if(amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        walletBalance=walletBalance.add(amount);
    }
    public void deductMoney(BigDecimal amount){
        if(amount.compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if(walletBalance.compareTo(amount)<=0){
            throw new InsufficientWalletException("Insufficient wallet balance");
        }
        walletBalance=walletBalance.subtract(amount);
    }
    public boolean isFirstTimeCustomer(){
        return completedOrderCount==0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof Customer other)) return false;
        return this.id.equals(other.id);
    }
    public int hashCode(){
        return Objects.hash(id);
    }
}
