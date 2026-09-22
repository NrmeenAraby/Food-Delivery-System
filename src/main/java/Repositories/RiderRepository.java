package Repositories;


import Domain.Rider;

import java.util.HashMap;

public class RiderRepository {
    private HashMap<String, Rider> riders=new HashMap<>();
    public void addRider(Rider rider){
        riders.put(rider.getId(),rider);
    }
    public Rider findById(String riderId){
        return riders.get(riderId);
    }
}
