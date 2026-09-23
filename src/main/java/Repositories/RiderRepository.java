package Repositories;


import Domain.Restaurant;
import Domain.Rider;

import java.util.HashMap;
import java.util.List;

public class RiderRepository {
    private HashMap<String, Rider> riders=new HashMap<>();
    public void addRider(Rider rider){
        riders.put(rider.getId(),rider);
    }
    public Rider findById(String riderId){
        return riders.get(riderId);
    }
    public List<Rider> getAllRiders(){
        return riders.values().stream().toList();
    }
}
