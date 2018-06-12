package com.fecred.cxpt.consumer.model;

import javax.persistence.*;

@Entity
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nodeKey", length = 32, nullable = false)
    private String nodeKey;

    @Column(name = "startedAt", nullable = true)
    private long startedAt;

    @Column(name = "finishedAt", nullable = true)
    private long finishedAt;

    @Column(name = "tid", nullable = false)
    private int tid;

    @Column(name = "sfzhm")
    private String sfzhm;

    @Column(name = "success", columnDefinition = "int(1) not null default 0")
    private boolean success;

    @Column(name = "ontid", unique = true, nullable = true)
    private String ontid;

    @ManyToOne
    @JoinColumn(name = "nodeId")
    Node node;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(long finishedAt) {
        this.finishedAt = finishedAt;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getSfzhm() {
        return sfzhm;
    }

    public void setSfzhm(String sfzhm) {
        this.sfzhm = sfzhm;
    }

    public String getOntid() {
        return ontid;
    }

    public void setOntid(String ontid) {
        this.ontid = ontid;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
