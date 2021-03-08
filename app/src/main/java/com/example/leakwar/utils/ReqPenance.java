package com.example.leakwar.utils;

import com.example.leakwar.models.Slave;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReqPenance {
    private Map<String, Integer> publicList;
    private List<Slave> slaves;
    private LinkedList<String> blackList;

    public ReqPenance() {

    }

    public Map<String, Integer> getPublicList() {
        return this.publicList;
    }

    public List<Slave> getSlaves() {
        return this.slaves;
    }

    public LinkedList<String> getBlackList() {
        return this.blackList;
    }
}
