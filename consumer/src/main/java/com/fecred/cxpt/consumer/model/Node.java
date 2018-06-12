package com.fecred.cxpt.consumer.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "node")
    private String node;

    @Column(name = "bootTime")
    private long bootTime;

    @Column(name = "startTime")
    private long startTime;

    @OneToMany(mappedBy = "node")
    private Set<Logs> logs = new HashSet<Logs>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public long getBootTime() {
        return bootTime;
    }

    public void setBootTime(long bootTime) {
        this.bootTime = bootTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Set<Logs> getLogs() {
        return logs;
    }

    public void setLogs(Set<Logs> logs) {
        this.logs = logs;
    }
}
