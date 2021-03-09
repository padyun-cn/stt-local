package com.uls.utilites.content;

import android.os.Bundle;

import com.uls.utilites.common.BreakedConsumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by daiepngfei on 2021-01-21
 */
public class PostOffice {

    private static final String ALL = "_____ALL_____";
    private Set<Bundle> lostAndFound = new HashSet<>();
    private LinkedHashMap<String, Address> addresses = new LinkedHashMap<>();

    public PostOffice() {
        // do nothing
    }

    public Address register(String addressName){
        if(addresses.containsKey(addressName)){
            return addresses.get(addressName);
        }
        Address address = new Address();
        addresses.put(addressName, address);
        return address;
    }

    public void unregister(String addressName){
        Address address = addresses.get(addressName);
        if(address != null){
            address.clear();
            addresses.remove(addressName);
        }
    }

    /**
     *
     * @param pack
     */
    public void toLostAndFound(Bundle pack){
        lostAndFound.add(pack);
    }


    /**
     *
     * @param bundleBreakedConsumer
     * @return
     */
    public Bundle checkAndGetFromLostAndFound(BreakedConsumer<Bundle> bundleBreakedConsumer){
        if(bundleBreakedConsumer == null){
            return null;
        }
        Iterator<Bundle> iterator = lostAndFound.iterator();
        while (iterator.hasNext()){
            Bundle b = iterator.next();
            if (bundleBreakedConsumer.breakingOn(b)) {
                iterator.remove();
                return b;
            }
        }
        return null;
    }

    /**
     *
     * @param addressName
     * @param pack
     */
    public void post(String addressName, Bundle pack){
        post(addressName, pack, false);
    }

    /**
     *
     * @param addressName
     * @param pack
     * @param usingAgent
     */
    public void post(String addressName, Bundle pack, boolean usingAgent){
        Address addr = addresses.get(addressName);
        if(addr != null){
            if(usingAgent && addr.addressAgent != null){
                ArrayList<Bundle> list = new ArrayList<>();
                list.add(pack);
                addr.addressAgent.onReceive(list);
            } else {
                addr.receive(pack);
            }
        }

    }

    public static class Address {
        private List<Bundle> packs = new LinkedList<>();
        private AdressAgent addressAgent;

        public void setAddressAgent(AdressAgent addressAgent) {
            this.addressAgent = addressAgent;
        }

        public List<Bundle> fetch(){
            List<Bundle> packs = new LinkedList<>(this.packs);
            this.packs.clear();
            return packs;
        }

        private void receive(Bundle packs){
            if(packs != null) {
                this.packs.add(packs);
            }
        }

        private void clear() {
            this.packs.clear();
        }
    }

    public interface AdressAgent {
         void onReceive(List<Bundle> bundles);
    }


}
