/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author miguel
 */
public class CoreProperties {

    static private Properties prop;
    static public volatile CoreProperties properties = null;
    static public int num_replicas;
    static public int num_workers;
    static public int quorom;
    static public int warmup_rounds;
    static public int message_size;
    static public int experiment_rounds;
    static public int queue_size;
    static public int destiny_port, listen_port;
    static public String ip;
    static public boolean debug;
    static public String shared_key_path;
    static public String private_key_path;
    static public String public_key_path;
    static public String algorithm;
    static public String crypto_scheme;
    static public int hmac_key_size;
    static public int signature_key_size;
    static public String experiments_ip;
    static public int latency_experiments_port, throughput_experiments_port;
    static public int messageRate;
    static public String throughput_experiments_file;
    static public String latency_experiments_file;
    static public String operator;
    static public boolean channel;
    static public int MAX_BUFFERED;

    static public String target;
    static public int max_queue;
    static public int batch_size;

    static public int payload;
    static public int targetID;
    static public long timeout;
    public static int ACK_RATE;

    static public int attack_threads;
    static public int size_message;
    static public String target_ip, experiment_type;
    static public int target_port;
    static public int rate_message;
    public static String rules;
    public static int patternlenght;
    public static int numberofpatterns;

    public CoreProperties(String file, String type) {
        try {
            prop = new Properties();
            prop.load(new FileInputStream(file));
            switch (type) {
                case "controller":
                    operator = prop.getProperty("controller.operator");
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    break;
                case "attacker":
                    destiny_port = Integer.parseInt(prop.getProperty("core.attacker.target.port"));
                    ip = prop.getProperty("core.attacker.target.ip");
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    payload = Integer.parseInt(prop.getProperty("core.attacker.payload"));
//                    targetID = Integer.parseInt(prop.getProperty("core.attacker.targetID"));
                    break;
                case "client":
                    debug = Boolean.parseBoolean(prop.getProperty("core.debug"));
                    num_replicas = Integer.parseInt(prop.getProperty("core.replicas"));
                    num_workers = Integer.parseInt(prop.getProperty("core.number_workers"));
                    experiment_type = prop.getProperty("core.experiments.type");
                    quorom = Integer.parseInt(prop.getProperty("core.replicas.quorom"));
                    warmup_rounds = Integer.parseInt(prop.getProperty("core.warmup"));
                    experiment_rounds = Integer.parseInt(prop.getProperty("core.rounds"));
                    message_size = Integer.parseInt(prop.getProperty("core.dataSize"));
                    queue_size = Integer.parseInt(prop.getProperty("core.queue_size"));
                    destiny_port = Integer.parseInt(prop.getProperty("core.port"));
                    listen_port = Integer.parseInt(prop.getProperty("core.listen_port"));
                    ip = prop.getProperty("core.ip");
                    shared_key_path = prop.getProperty("core.crypto.shared_key");
                    private_key_path = prop.getProperty("core.crypto.private_key");
                    public_key_path = prop.getProperty("core.crypto.public_key");
                    algorithm = prop.getProperty("core.crypto.algorithm");
                    hmac_key_size = Integer.parseInt(prop.getProperty("core.crypto.mac.keySize"));
                    signature_key_size = Integer.parseInt(prop.getProperty("core.crypto.signature.keySize"));
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    experiments_ip = prop.getProperty("core.experiments.ip");
                    latency_experiments_port = Integer.parseInt(prop.getProperty("core.experiments.latency.port"));
                    throughput_experiments_port = Integer.parseInt(prop.getProperty("core.experiments.throughput.port"));
                    messageRate = Integer.parseInt(prop.getProperty("core.experiments.messageRate"));
                    throughput_experiments_file = prop.getProperty("core.experiments.throughput.file");
                    latency_experiments_file = prop.getProperty("core.experiments.latency.file");
                    channel = Boolean.parseBoolean(prop.getProperty("core.channel"));
                    max_queue = Integer.parseInt(prop.getProperty("core.rounds")) * Integer.parseInt(prop.getProperty("core.experiments.messageRate"));
                    MAX_BUFFERED = Integer.parseInt(prop.getProperty("core.channel.max_buffered"));
                    break;
                case "replica":
                    channel = false;
                    attack_threads = Integer.parseInt(prop.getProperty("core.attacker.threads"));
                    rate_message = Integer.parseInt(prop.getProperty("core.attacker.rate"));
                    size_message = Integer.parseInt(prop.getProperty("core.attacker.size"));
                    target_ip = prop.getProperty("core.attacker.target.ip");
                    rules = prop.getProperty("core.rules");
                    numberofpatterns = Integer.parseInt(prop.getProperty("core.numberofpatterns"));
                    patternlenght = Integer.parseInt(prop.getProperty("core.patternlenght"));
                    target_port = Integer.parseInt(prop.getProperty("core.attacker.target.port"));
                    payload = Integer.parseInt(prop.getProperty("core.attacker.payload"));
                    targetID = Integer.parseInt(prop.getProperty("core.attacker.targetID"));
                    num_workers = Integer.parseInt(prop.getProperty("core.number_workers"));
                    debug = Boolean.parseBoolean(prop.getProperty("core.debug"));
                    queue_size = Integer.parseInt(prop.getProperty("core.queue_size"));
                    num_replicas = Integer.parseInt(prop.getProperty("core.quorom.replicas"));
                    quorom = Integer.parseInt(prop.getProperty("core.quorom.faulty"));
                    shared_key_path = prop.getProperty("core.crypto.shared_key");
                    private_key_path = prop.getProperty("core.crypto.private_key");
                    public_key_path = prop.getProperty("core.crypto.public_key");
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    algorithm = prop.getProperty("core.crypto.algorithm");
                    signature_key_size = Integer.parseInt(prop.getProperty("core.crypto.signature.keySize"));
                    hmac_key_size = Integer.parseInt(prop.getProperty("core.crypto.mac.keySize"));
                    // throughput_experiments_file = prop.getProperty("core.experiments.file");
                    break;
                case "prereplica":
                    channel = false;
                    debug = Boolean.parseBoolean(prop.getProperty("core.debug"));
                    messageRate = Integer.parseInt(prop.getProperty("core.experiments.messageRate"));
                    message_size = Integer.parseInt(prop.getProperty("core.dataSize"));
                    queue_size = Integer.parseInt(prop.getProperty("core.queue_size"));
                    destiny_port = Integer.parseInt(prop.getProperty("core.port"));
                    listen_port = Integer.parseInt(prop.getProperty("core.listen_port"));
                    ip = prop.getProperty("core.ip");
                    num_workers = Integer.parseInt(prop.getProperty("core.number_workers"));
                    shared_key_path = prop.getProperty("core.crypto.shared_key");
                    private_key_path = prop.getProperty("core.crypto.private_key");
                    public_key_path = prop.getProperty("core.crypto.public_key");
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    algorithm = prop.getProperty("core.crypto.algorithm");
                    signature_key_size = Integer.parseInt(prop.getProperty("core.crypto.signature.keySize"));
                    hmac_key_size = Integer.parseInt(prop.getProperty("core.crypto.mac.keySize"));
                    throughput_experiments_file = prop.getProperty("core.experiments.throughput.file");
                    //throughput_experiments_file = prop.getProperty("core.experiments.file");
                    break;
                case "server":
                    channel = false;
                    timeout = Long.parseLong(prop.getProperty("core.batch.timeout"));
                    batch_size = Integer.parseInt(prop.getProperty("core.batch.size"));
                    experiment_type = prop.getProperty("core.experiments.type");
                    ACK_RATE = Integer.parseInt(prop.getProperty("core.ackrate"));
                    num_workers = Integer.parseInt(prop.getProperty("core.number_workers"));
                    warmup_rounds = Integer.parseInt(prop.getProperty("core.warmup"));
                    max_queue = Integer.parseInt(prop.getProperty("core.rounds")) * Integer.parseInt(prop.getProperty("core.experiments.messageRate"));
                    debug = Boolean.parseBoolean(prop.getProperty("core.debug"));
                    queue_size = Integer.parseInt(prop.getProperty("core.queue_size"));
                    num_replicas = Integer.parseInt(prop.getProperty("core.quorom.replicas"));
                    quorom = Integer.parseInt(prop.getProperty("core.quorom.faulty"));
                    crypto_scheme = prop.getProperty("core.crypto.scheme");
                    shared_key_path = prop.getProperty("core.crypto.shared_key");
                    private_key_path = prop.getProperty("core.crypto.private_key");
                    public_key_path = prop.getProperty("core.crypto.public_key");
                    algorithm = prop.getProperty("core.crypto.algorithm");
                    hmac_key_size = Integer.parseInt(prop.getProperty("core.crypto.mac.keySize"));
                    signature_key_size = Integer.parseInt(prop.getProperty("core.crypto.signature.keySize"));
                    experiments_ip = prop.getProperty("core.experiments.ip");
                    experiment_rounds = Integer.parseInt(prop.getProperty("core.rounds"));
                    latency_experiments_port = Integer.parseInt(prop.getProperty("core.experiments.latency.port"));
                    throughput_experiments_port = Integer.parseInt(prop.getProperty("core.experiments.throughput.port"));
                    throughput_experiments_file = prop.getProperty("core.experiments.throughput.file");
                    latency_experiments_file = prop.getProperty("core.experiments.latency.file");
                    messageRate = Integer.parseInt(prop.getProperty("core.experiments.messageRate"));
                    channel = Boolean.parseBoolean("core.channel");
                    break;
            }
        } catch (IOException ex) {
            System.out.println("CoreProperties' exception: " + ex.getMessage());
        }
    }

    public static CoreProperties getProperties(String path, String type) {
        if (properties == null) {
            return new CoreProperties(path, type);
        } else {
            return properties;
        }
    }
}
