package com.fecred.cxpt.consumer.generator;

import com.alibaba.fastjson.JSON;
import com.fecred.cxpt.consumer.common.Response;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.apache.curator.framework.CuratorFramework;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fecred.cxpt.consumer.repository.*;
import com.fecred.cxpt.consumer.model.Logs;
import com.fecred.cxpt.consumer.model.MPersonal;
import com.fecred.cxpt.consumer.model.Node;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@EnableAutoConfiguration
public class Ontid {

    String key;
    private Boolean started = false;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private LogsRepository logsRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    private OntSdk sdk;

    @Value("${node}")
    private String nodeName;
    private long bootTime;

    private Node node;
    private Logs logs;

    private int completed = 0;
    private int failed = 0;

    @Autowired
    private CuratorFramework client;

    Ontid() throws Exception {
        Double temp = Math.random();
        key = temp.toString();
    }

    @RequestMapping("/node/start")
    @ResponseBody
    public Response start() {

        Response response = new Response();

        try {
            if (started == false) {
                sdk = getSdk();
                generator(new MPersonal());
                started = true;

                Node node = new Node();
                node.setBootTime(this.bootTime);
                node.setNode(this.nodeName);
                node.setStartTime(new Date().getTime());

                this.node = node;
                this.nodeRepository.saveAndFlush(node);
            }

            response.setCode(200);
            response.setData("Started");
        } catch (InterruptedException e) {
            started = false;
            response.setCode(500);
            response.setData(e.getMessage());
        } catch (SDKException e) {
            response.setCode(500);
            response.setData(e.getMessage());
        }

        return response;
    }

    @RequestMapping("/node/status")
    @ResponseBody
    public Response status() {
        Response response = new Response();

        response.setCode(200);

        HashMap<Object, Object> data = new HashMap<>();

        if (this.node == null) {
            data.put("Status", 0);
            data.put("Message", "Waiting for start");
            data.put("Status", 0);
            data.put("Key", this.key);
            data.put("BootedAt", this.bootTime);
            data.put("Completed", this.completed);
            data.put("Failed", this.failed);

            response.setData(data);
            return  response;
        }

        data.put("Message", "Working");
        data.put("Status", 1);
        data.put("Node", this.nodeName);
        data.put("Key", this.key);
        data.put("BootedAt", this.bootTime);
        data.put("StartedAt", this.node.getStartTime());
        data.put("Completed", this.completed);
        data.put("Failed", this.failed);

        response.setData(data);

        return response;
    }

    @KafkaListener(id = "test", topics = "generate")
    public void listener(ConsumerRecord<String, String> cr) throws Exception {


        if (cr.key().equals(this.key)) {
            MPersonal personal = JSON.parseObject(cr.value(), MPersonal.class);


            if (personal == null || personal.getTid() == null) {
                return;
            }

            this.generator(personal);
        }
    }

    private void generator(MPersonal personal) throws InterruptedException {

        if (personal != null && personal.getTid() != null) {
            sdk.openWalletFile("/home/mio/Template/wallets/"+ personal.getTid().toString() +".json");
            WalletMgr walletMgr = sdk.getWalletMgr();
            Identity identity;

            walletMgr.getWallet().setName("mio.renshan");

            logs = new Logs();
            logs.setStartedAt(new Date().getTime());
            logs.setNodeKey(this.key);
            logs.setSfzhm(personal.getSfzhm());
            logs.setTid(personal.getTid());
            logs.setSuccess(true);

            try {

                if (walletMgr.getAccounts().size() == 0) {
                    Account account = walletMgr.createAccount("123456");
                    account.label = personal.getSfzhm();

                    walletMgr.getWallet().setDefaultAccount(account.address);
                }

                identity = walletMgr.createIdentity("123456");
                sdk.getOntIdTx().sendRegister(identity, "123456");
                walletMgr.getWallet().setDefaultIdentity(identity.ontid);
                walletMgr.writeWallet();
                personal.setOntid(identity.ontid);

                logs.setOntid(identity.ontid);

                this.completed ++;

            } catch (Exception e) {
                logs.setSuccess(false);
                this.failed ++;
            }

            logs.setNode(this.node);
            logs.setFinishedAt(new Date().getTime());

            logsRepository.saveAndFlush(logs);

            personal.setRunning(0);
        }

        // 完成以后，发回更新到数据库
        template.send("completed", this.key, JSON.toJSONString(personal));
    }

    // 定时推送节点信息
    @Scheduled(cron = "*/5 * * * * ?")
    private void selfMessage() {
        Map data = new HashMap();

        data.put("Message", this.started ? "Node is running" : "Waiting for start");
        data.put("Status", this.started);
        data.put("NodeName", this.nodeName);
        data.put("Key", this.key);
        data.put("BootedAt", this.bootTime);
        data.put("Completed", this.completed);
        data.put("Failed", this.failed);

        System.out.println(data);

        template.send("status", this.nodeName, JSON.toJSONString(data));
    }

    private OntSdk getSdk() throws SDKException {
        OntSdk sdk = OntSdk.getInstance();

        sdk.setRestful("http://localhost:21334");
        sdk.setCodeAddress("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
        sdk.setDefaultConnect(sdk.getRestful());

        return sdk;
    }
}
